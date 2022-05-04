package com.example.audioheaven;

import java.io.IOException;
import java.util.*;
import javax.sound.sampled.UnsupportedAudioFileException;

import static com.example.audioheaven.Utilities.*;


/*  frame: list of values of a song
    dBTable: list of consecutive amplifying/attenuating values for each freq channel in dB passed by user
    Fs: sampling frequency
 */
public class Equalizer{

    DataHandler sampleReader;
    long nbSamples;
    double [] samples;
    List<Double> amplifying;

    public void setSong(String songName) throws UnsupportedAudioFileException, IOException {
        sampleReader = new DataHandler(songName);
        nbSamples = 2*sampleReader.getSampleCount();
        samples = new double[(int)nbSamples];
        sampleReader.getInterleavedSamples(0, nbSamples, samples);
    }

    public void setAmplifying (List<Double> sliderValues) {
        amplifying = sliderValues;
    }

    /* Method responsible for delivering audio data to be live equalised
       It's also responsible for making any song power-of-2 samples-long
       Output is 2048 samples-long audio data
    */
    public Queue<double [][]> frameFeeder(double [] samples, int frameSize) {
        Queue<double [][]> liveMusic = new LinkedList<>();

        for (int k = 0; k < samples.length; k = k + frameSize) {
            double [][] complexSamples = new double[frameSize][2];

            for (int i = 0; i < frameSize; i++) {
                try {
                    complexSamples[i][0] = samples[i + k];
                    complexSamples[i][1] = 0.0;
                } catch (ArrayIndexOutOfBoundsException exc) {
                    // Do nothing
                }
            }

            liveMusic.add(complexSamples);
        }

        return liveMusic;
    }

    /*  Method responsible for audio processing of a song
        frame: list of values of a song
        dBTable: list of consecutive amplifying/attenuating values for each freq channel in dB passed by user
        Fs: sampling frequency
    */
    public double [] equaliseMe(double [][] frame, List<Double> dBTable,int fs) {
        int n = frame.length;
        List<Double> linearAmplTable = new ArrayList<>();

        // Change dB to linear
        for (Double each: dBTable) {
            assert false;
            linearAmplTable.add(Math.pow(10, each/20));
        }

        double [][] largeX = fft2power(frame);

        List<Integer> frequenciesLocation = positioning(n, fs);

        double [][] largeX32 = lowpassSymmetric(largeX, frequenciesLocation.get(0));
        double [][] largeX64 = bandpassSymmetric(largeX, frequenciesLocation.get(0), frequenciesLocation.get(1));
        double [][] largeX128 = bandpassSymmetric(largeX, frequenciesLocation.get(1), frequenciesLocation.get(2));
        double [][] largeX256 = bandpassSymmetric(largeX, frequenciesLocation.get(2), frequenciesLocation.get(3));
        double [][] largeX512 = bandpassSymmetric(largeX, frequenciesLocation.get(3), frequenciesLocation.get(4));
        double [][] largeX1k = bandpassSymmetric(largeX, frequenciesLocation.get(4), frequenciesLocation.get(5));
        double [][] largeX2k = bandpassSymmetric(largeX, frequenciesLocation.get(5), frequenciesLocation.get(6));
        double [][] largeX4k = bandpassSymmetric(largeX, frequenciesLocation.get(6), frequenciesLocation.get(7));
        double [][] largeX8k = bandpassSymmetric(largeX, frequenciesLocation.get(7), frequenciesLocation.get(8));
        double [][] largeX16k = bandpass(largeX, frequenciesLocation.get(8), n - 1 - frequenciesLocation.get(8));
        double [][] largeXNew = new double[n][2];

        for (int i = 0; i < n; i++) {
            largeX32[i][0] = largeX32[i][0]*linearAmplTable.get(0);
            largeX32[i][1] = largeX32[i][1]*linearAmplTable.get(0);

            largeX64[i][0] = largeX64[i][0]*linearAmplTable.get(1);
            largeX64[i][1] = largeX64[i][1]*linearAmplTable.get(1);

            largeX128[i][0] = largeX128[i][0]*linearAmplTable.get(2);
            largeX128[i][1] = largeX128[i][1]*linearAmplTable.get(2);

            largeX256[i][0] = largeX256[i][0]*linearAmplTable.get(3);
            largeX256[i][1] = largeX256[i][1]*linearAmplTable.get(3);

            largeX512[i][0] = largeX512[i][0]*linearAmplTable.get(4);
            largeX512[i][1] = largeX512[i][1]*linearAmplTable.get(4);

            largeX1k[i][0] = largeX1k[i][0]*linearAmplTable.get(5);
            largeX1k[i][1] = largeX1k[i][1]*linearAmplTable.get(5);

            largeX2k[i][0] = largeX2k[i][0]*linearAmplTable.get(6);
            largeX2k[i][1] = largeX2k[i][1]*linearAmplTable.get(6);

            largeX4k[i][0] = largeX4k[i][0]*linearAmplTable.get(7);
            largeX4k[i][1] = largeX4k[i][1]*linearAmplTable.get(7);

            largeX8k[i][0] = largeX8k[i][0]*linearAmplTable.get(8);
            largeX8k[i][1] = largeX8k[i][1]*linearAmplTable.get(8);

            largeX16k[i][0] = largeX16k[i][0]*linearAmplTable.get(9);
            largeX16k[i][1] = largeX16k[i][1]*linearAmplTable.get(9);

            largeXNew[i][0] = largeX32[i][0] + largeX64[i][0] + largeX128[i][0] + largeX256[i][0] + largeX512[i][0] + largeX1k[i][0] + largeX2k[i][0] + largeX4k[i][0] + largeX8k[i][0] + largeX16k[i][0];
            largeXNew[i][1] = largeX32[i][1] + largeX64[i][1] + largeX128[i][1] + largeX256[i][1] + largeX512[i][1] + largeX1k[i][1] + largeX2k[i][1] + largeX4k[i][1] + largeX8k[i][1] + largeX16k[i][1];
        }

        double [] result = getReal(ifft2power(largeXNew));

        double maxVal = 0.0;
        for (int i = 0; i < n; i++) {
            if (result[i] > maxVal) {
                maxVal = result[i];
            }
        }

        if (maxVal != 0.0) {
            for (int i = 0; i < n; i++) {
                result[i] = result[i]/maxVal;
            }
        }

        return result;
    }
}
