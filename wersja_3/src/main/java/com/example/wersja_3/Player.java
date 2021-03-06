package com.example.wersja_3;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.example.wersja_3.PlayerController.nullCounter;

public class Player extends Thread{

    private final Equalizer dj;
    private List<Double> amplifying;
    private int volume;
    Clip clip = null;
    boolean play = true;
    int it;

    public Player(String songName, List<Double> amplifyingValues, int volumePassed) throws UnsupportedAudioFileException, IOException {
        dj = new Equalizer();
        amplifying = amplifyingValues;
        dj.setSong(songName);
        volume = volumePassed;
    }

    public void playCheck(boolean info) {
        play = info;
    }

    public void setEqualise(List<Double> infoEq) {
        amplifying = new ArrayList<>(infoEq);
    }

    public void replay() {
        it = -1;
        play = false;
        clip.stop();
        play = true;
        clip.start();
    }

    public void rewind() {
        it = it - 8;
    }

    public void forward() {
        it = it + 8;
    }

    public void setVolume(int infoVol) {
        volume = infoVol;
    }

    public Equalizer getDj() {
        return dj;
    }

    public void play(){
        final List<double [][]> line = dj.frameFeeder(dj.samples, 65536);
        final File outFile = new File("out.wav");
        final int placeHold = line.size();
        AudioInputStream stream;
        for (it = 0; it < placeHold; it++) {
            double [][] pointing = line.get(it);
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
                if (it != 0) {
                    while (clip.getMicrosecondLength() != clip.getMicrosecondPosition()) {
                        // Do nothing
                    }
                }
                while (!play) {
                    // Do nothing
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
        nullCounter();
    }

    @Override
    public void run() {
        this.play();
    }
}

