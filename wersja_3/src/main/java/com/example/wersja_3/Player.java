package com.example.audioheaven;

import jaco.mp3.a.D;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Player{

    public void play(String song, List<Double> amplifying) throws UnsupportedAudioFileException, IOException {
        final Equalizer dj = new Equalizer();
        dj.setSong(song);
        final Queue<double [][]> line = dj.frameFeeder(dj.samples, 131072);
        final File outFile = new File("out.wav");
        final int placeHold = line.size();
        AudioInputStream stream;
        Clip clip = null;
        for (int i = 0; i < placeHold; i++) {
            dj.setAmplifying(amplifying);
            double [][] pointing = line.poll();
            double [] result = dj.equaliseMe(pointing, amplifying, 48000);

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

    public static void main(String[] args) {
        List<Double> amplifying = new ArrayList<>(List.of(-2.0, 5.0, -8.0, 0.0, 8.0, 7.0, 6.0, 0.0, 0.0, 0.0));
        Player bartender = new Player();

        try {
            bartender.play("Daft Punk - Get Lucky (Album Version).wav", amplifying);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

    }
}

