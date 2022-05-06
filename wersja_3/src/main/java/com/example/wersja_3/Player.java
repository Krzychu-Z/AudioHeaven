package com.example.wersja_3;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Player{

    private final Equalizer dj;
    private final List<Double> amplifying;
    private int volume;
    Clip clip = null;

    public Player(String songName, List<Double> amplifyingValues, int volumePassed) throws UnsupportedAudioFileException, IOException {
        dj = new Equalizer();
        amplifying = amplifyingValues;
        dj.setSong(songName);
        volume = volumePassed;
    }

    public Equalizer getDj() {
        return dj;
    }

    public void play(){
        final List<double [][]> line = dj.frameFeeder(dj.samples, 524288);
        final File outFile = new File("out.wav");
        final int placeHold = line.size();
        AudioInputStream stream;
        for (int i = 0; i < placeHold; i++) {
            double [][] pointing = line.get(i);
            double [] result = dj.equaliseMe(pointing, amplifying, (int)dj.sampleReader.getFormat().getSampleRate(), volume);

            AudioWriter audioWriter;
            try {
                audioWriter = new AudioWriter(outFile, dj.sampleReader.getFormat(), AudioFileFormat.Type.WAVE);
                audioWriter.writeInterleavedSamples(result, result.length);
                audioWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                AudioFormat format;
                DataLine.Info info;
                stream = AudioSystem.getAudioInputStream(outFile);
                if (i != 0) {
                    while (clip.getMicrosecondLength() != clip.getMicrosecondPosition()) {
                        // Do nothing
                    }
                }
                format = stream.getFormat();
                info = new DataLine.Info(Clip.class, format);
                clip = (Clip) AudioSystem.getLine(info);
                clip.open(stream);
                clip.start();
            }
            catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}

