package com.example.wersja_3;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataHandler {

    public final AudioInputStream inputStream;
    private final AudioFormat format;
    private final Logger logger = Logger.getLogger(DataHandler.class.getName());

    public DataHandler (String song) throws UnsupportedAudioFileException, IOException {
        File input = new File(song);
        inputStream = AudioSystem.getAudioInputStream(input);
        format = inputStream.getFormat();
    }

    public AudioFormat getFormat() {
        return format;
    }

    public long getSampleCount() {
        long total = (inputStream.getFrameLength() * format.getFrameSize() * 8) / format.getSampleSizeInBits();
        return total / format.getChannels();
    }

    private void decodeBytes(byte[] audioBytes, double[] audioSamples) {
        int sampleSizeInBytes = format.getSampleSizeInBits() / 8;
        int[] sampleBytes = new int[sampleSizeInBytes];
        int k = 0; // index in audioBytes
        for (int i = 0; i < audioSamples.length; i++) {
            // collect sample byte in big-endian order
            if (format.isBigEndian()) {
                // bytes start with MSB
                for (int j = 0; j < sampleSizeInBytes; j++) {
                    sampleBytes[j] = audioBytes[k++];
                }
            } else {
                // bytes start with LSB
                for (int j = sampleSizeInBytes - 1; j >= 0; j--) {
                    sampleBytes[j] = audioBytes[k++];
                    if (sampleBytes[j] != 0) {
                        logger.log(Level.FINE, "Decoding!");
                    }

                }
            }
            // get integer value from bytes
            int ival = 0;
            for (int j = 0; j < sampleSizeInBytes; j++) {
                ival += sampleBytes[j];
                if (j < sampleSizeInBytes - 1) ival <<= 8;
            }
            // decode value
            double ratio = Math.pow(2., format.getSampleSizeInBits() - 1.0);
            double val = ival / ratio;
            audioSamples[i] = val;
        }
    }

    public void getInterleavedSamples(long begin, long end, double[] samples) throws IOException, IllegalArgumentException {
        long numSamples = end - begin;
        // nbBytes = numSamples * sampleSizeinByte * nbChannels
        long nbBytes = numSamples * (format.getSampleSizeInBits() / 8) *
                format.getChannels();
        if (nbBytes > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Too perfectly recorded (too many samples)");
        // allocate a byte buffer
        byte[] inBuffer = new byte[(int)nbBytes];
        // read bytes from audio file
        inputStream.read(inBuffer, 0, inBuffer.length);
        // decode bytes into samples. Supported encodings are:
        // PCM-SIGNED, PCM-UNSIGNED, A-LAW, U-LAW
        decodeBytes(inBuffer, samples);
    }
}
