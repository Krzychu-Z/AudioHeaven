package com.example.audioheaven;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class Utilities {

    private Utilities(){
        throw new IllegalStateException("Utility class");
    }

    // Desperately needed true deep copy - pls don't judge
    public static double [][] deepcopy(double[][] source) {
        double [][] twin = new double[source.length][2];
        for (int i = 0; i < source.length; i++) {
            twin[i][0] = source[i][0];
            twin[i][1] = source[i][1];
        }
        return twin;
    }

    /* - Following function is used as low-pass filter.
      - Low-pass filter cuts out frequencies (frame) above certain value (upperIndex)
    */
    public static double [][] lowpassSymmetric(double [][] frame, int upperIndex) {
        double [][] canvas = deepcopy(frame);

        if (upperIndex > 0 && upperIndex < canvas.length) {
            for (int i = upperIndex; i < canvas.length - upperIndex; i++) {
                canvas[i][0] = 0.0;
                canvas[i][1] = 0.0;
            }
        } else if (upperIndex <= 0) {
            return new double[frame.length][2];
        }
        return canvas;
    }

    /* - Following function is used as band-pass filter.
       - Band-pass filter cuts out frequencies (frame) below certain value (lowerIndex)
       - and above certain different value as well (upperIndex)
     */
    public static double [][] bandpass(double [][] frame, int lowerIndex, int upperIndex) {
        double [][] canvas = deepcopy(frame);

        if (lowerIndex < upperIndex && upperIndex < canvas.length && lowerIndex > 0) {
            // Band-pass
            for (int i = 0; i < lowerIndex; i++) {
                canvas[i][0] = 0.0;
                canvas[i][1] = 0.0;
            }
            for (int i = upperIndex; i < frame.length; i++) {
                canvas[i][0] = 0.0;
                canvas[i][1] = 0.0;
            }
        } else if (lowerIndex < upperIndex && upperIndex < canvas.length) {
            // Low-pass
            for (int i = upperIndex; i < canvas.length; i++) {
                canvas[i][0] = 0.0;
                canvas[i][1] = 0.0;
            }
        } else if (lowerIndex < upperIndex && lowerIndex > 0) {
            // High-pass
            for (int i = 0; i < lowerIndex; i++) {
                canvas[i][0] = 0.0;
                canvas[i][1] = 0.0;
            }
        }
        return canvas;
    }

    /* - Following function is used as band-pass filter.
       - Band-pass filter cuts out frequencies (frame) below certain value (lowerIndex)
       - and above certain different value as well (upperIndex)
    */
    public static double [][] bandpassSymmetric(double [][] frame, int lowerIndex, int upperIndex) {
        double [][] canvas = deepcopy(frame);

        if (lowerIndex < upperIndex && upperIndex < canvas.length && lowerIndex > 0) {
            // Band-pass (symmetric)
            for (int i = 0; i < lowerIndex; i++) {
                canvas[i][0] = 0.0;
                canvas[i][1] = 0.0;
            }
            for (int i = upperIndex; i < canvas.length - upperIndex; i++) {
                canvas[i][0] = 0.0;
                canvas[i][1] = 0.0;
            }
            for (int i = canvas.length - lowerIndex; i < canvas.length; i++) {
                canvas[i][0] = 0.0;
                canvas[i][1] = 0.0;
            }
        }
        return canvas;
    }

    // - Following function is used to translate frequencies in Hertz to array indices of FFT result.
    public static List<Integer> positioning(int length, int fs) {
        List<Integer> freqs = new ArrayList<>(List.of(32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384));
        return freqs.stream().map(x -> (length/fs)*x).toList();
    }

    // - Following function is used to cut out imaginary part of complex numbers
    public static double [] getReal(double [][] complex) {
        double [] realData = new double[complex.length];
        for (int i = 0; i < complex.length; i++) {
            realData[i] = complex[i][0];
        }
        return realData;
    }

    /* - !NOTE! Overloaded function bitreverse2power for 2D array input
       - Following recursive function is used in Inverse FFT algorithm.
       - Recursive version of bit reversing algorithm of array indices
       - e.g. array index = 2 in array of size 16  -> 0010 -bitreverse2power-> 0100 = 4
       - !NOTE! Function for data of non-2-power size returns empty array
    */
    public static double [][] bitreverse2power(double [][] data) {
        // Check if data is power-of-two long
        int n = data.length;
        double testValue = Math.log(n) / Math.log(2);
        if ((int)(Math.ceil(testValue)) == (int)(Math.floor(testValue))) {
            if (n != 1) {
                // Divide in half
                double [][] odd = new double[n/2][2];

                double [][] even = new double[n/2][2];
                int stupidIndexEven = 0;
                int stupidIndexOdd = 0;
                for (int i = 0; i < n; i++) {
                    if (i % 2 == 0) {
                        even[stupidIndexEven] = data[i];
                        stupidIndexEven += 1;
                    } else {
                        odd[stupidIndexOdd] = data[i];
                        stupidIndexOdd += 1;
                    }
                }

                even = bitreverse2power(even);
                odd = bitreverse2power(odd);

                double [][] segment = new double[n][2];

                System.arraycopy(even, 0, segment, 0, n/2);
                System.arraycopy(odd, 0, segment, n/2, n/2);
                return segment;
            } else {
                return data;
            }
        }
        return new double[0][0];
    }

    /*  - FFT (Fast Fourier Transform) algorithm implementation.
        - It is used to transform digital data from one domain to another.
        - Mostly it is used to transform data from time into frequency domain.
        - Function takes imaginary data written as 2D array of double
        - Function uses radix-2 algorithm
        - Function outputs imaginary data written as 2D array of double
        - !NOTE! Function for arrays of not-2-power size returns empty 2D array
        - !NOTE! Complex numbers format: [[real, imag], [real, imag], ...]
     */
    public static double[] @NotNull [] fft2power(double [][] data) {
        int n = data.length;
        double [][] canvas = deepcopy(data);

        canvas = bitreverse2power(canvas);          // Shuffle algorithm
        int rounds = (int)(Math.log10(n)/Math.log10(2));    // For power of 2 inner part is always integer
        double testValue = Math.log(n) / Math.log(2);

        if ((int)(Math.ceil(testValue)) == (int)(Math.floor(testValue))) {

            // Butterfly "convolution"
            for (double i = 1; i <= rounds; i++) {
                int dftL = (int)Math.pow(2.0, i);
                int butterM = (int)Math.pow(2.0, i - 1);
                double [][] wInitial = {{1.0, 0.0}};
                double [][] wFactor = {{Math.cos(2.0*Math.PI/dftL), -1*Math.sin(2.0*Math.PI/dftL)}};

                for (int k = 1; k <= butterM; k++) {
                    for (int j = k; j <= n; j = j + dftL) {
                        int smallD = j + butterM;
                        double realHeart = canvas[smallD - 1][0] * wInitial[0][0] - canvas[smallD - 1][1] * wInitial[0][1];
                        double imagHeart = canvas[smallD - 1][0] * wInitial[0][1] + canvas[smallD - 1][1] * wInitial[0][0];
                        double [][] heart = {{realHeart, imagHeart}};
                        canvas[smallD - 1][0] = canvas[j - 1][0] - heart[0][0];   // New lower sample
                        canvas[smallD - 1][1] = canvas[j - 1][1] - heart[0][1];

                        canvas[j - 1][0] = canvas[j - 1][0] + heart[0][0];   // New upper sample
                        canvas[j - 1][1] = canvas[j - 1][1] + heart[0][1];
                    }
                    double tmpSave = wInitial[0][0];
                    wInitial[0][0] = wInitial[0][0] * wFactor[0][0] - wInitial[0][1] * wFactor[0][1];   // Next W factor
                    wInitial[0][1] = tmpSave * wFactor[0][1] + wInitial[0][1] * wFactor[0][0];
                }
            }

        }
        return canvas;
    }

    /*  - Inverse FFT (Fast Fourier Transform) algorithm implementation.
        - It is used to transform digital data from one domain to another.
        - Mostly it is used to transform data from frequency into time domain.
        - Function takes imaginary data written as 2D array of double
        - Function uses radix-2 algorithm for ifft
        - Function outputs imaginary data written as 2D array of double
        - !NOTE! Function for arrays of not-2-power size returns empty 2D array
        - !NOTE! Complex numbers format: [[real, imag], [real, imag], ...]
    */
    public static double[] @NotNull [] ifft2power(double [][] data) {
        int n = data.length;
        int rounds = (int)(Math.log10(n)/Math.log10(2));    // For power of 2 inner part is always integer
        double testValue = Math.log(n) / Math.log(2);
        double [][] canvas = deepcopy(data);

        if ((int)(Math.ceil(testValue)) == (int)(Math.floor(testValue))) {
            // Butterfly convolution
            canvas = bitreverse2power(canvas);          // Shuffle algorithm
            for (double i = 1; i <= rounds; i++) {
                int dftL = (int)Math.pow(2.0, i);
                int butterM = (int)Math.pow(2.0, i - 1);
                double [][] wInitial = {{1.0, 0.0}};
                double [][] wFactor = {{Math.cos(2.0*Math.PI/dftL), Math.sin(2.0*Math.PI/dftL)}};

                for (int k = 1; k <= butterM; k++) {
                    for (int j = k; j <= n; j = j + dftL) {
                        int smallD = j + butterM;
                        double realHeart = canvas[smallD - 1][0] * wInitial[0][0] - canvas[smallD - 1][1] * wInitial[0][1];
                        double imagHeart = canvas[smallD - 1][0] * wInitial[0][1] + canvas[smallD - 1][1] * wInitial[0][0];
                        double [][] heart = {{realHeart, imagHeart}};
                        canvas[smallD - 1][0] = canvas[j - 1][0] - heart[0][0];   // New lower sample
                        canvas[smallD - 1][1] = canvas[j - 1][1] - heart[0][1];

                        canvas[j - 1][0] = canvas[j - 1][0] + heart[0][0];   // New upper sample
                        canvas[j - 1][1] = canvas[j - 1][1] + heart[0][1];
                    }
                    double tmpSave = wInitial[0][0];
                    wInitial[0][0] = wInitial[0][0] * wFactor[0][0] - wInitial[0][1] * wFactor[0][1];   // Next W factor
                    wInitial[0][1] = tmpSave * wFactor[0][1] + wInitial[0][1] * wFactor[0][0];
                }
            }

            for (int i = 0; i < n; i++) {
                canvas[i][0] = canvas[i][0]/n;
                canvas[i][1] = canvas[i][1]/n;
            }

        }
        return canvas;
    }
}
