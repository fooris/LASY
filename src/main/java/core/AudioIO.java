package core;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AudioIO {

    /**
     * Copyright © 2000–2018 Robert Sedgewick and Kevin Wayne.
     * Copyright © 2018 Felix Fischer, Sebastian Kappes, Ben Loehner, Matthias Bungeroth, Floris Westermann.
     * SOURCE: https://introcs.cs.princeton.edu/java/stdlib/StdAudio.java.html
     */

    public static final int SAMPLE_RATE = 16000;
    public static final int REDUCTION_FACTOR = 50;
    private static final double MAX_16_BIT = Short.MAX_VALUE;     // 32,767

    public static double[] reduce(double[] orig, int reductionFactor) {
        double[] reduced = new double[orig.length / reductionFactor];
        for (int i = 0; i < orig.length / reductionFactor; i++) {
            reduced[i] = orig[i * reductionFactor];
        }
        return reduced;
    }

    // loads audio samples from file returns double[], values in [-1.0,1.0]
    public static double[] load(String filename) throws UnsupportedAudioFileException {
        byte[] data = readByte(filename);
        int n = data.length;
        double[] d = new double[n / 2];
        for (int i = 0; i < n / 2; i++) {
            d[i] = ((short) (((data[2 * i + 1] & 0xFF) << 8) + (data[2 * i] & 0xFF))) / ((double) MAX_16_BIT);
        }
        return d;
    }

    // return data as a byte array
    private static byte[] readByte(String filename) {
        byte[] data;
        AudioInputStream ais;
        try {
            // try to read from file
            File file = new File(filename);
            if (file.exists()) {
                ais = AudioSystem.getAudioInputStream(file);
                int bytesToRead = ais.available();
                data = new byte[bytesToRead];
                int bytesRead = ais.read(data);
                if (bytesToRead != bytesRead)
                    throw new IllegalStateException("read only " + bytesRead + " of " + bytesToRead + " bytes");
            }

            // try to read from URL
            else {
                URL url = new URL(filename);
                ais = AudioSystem.getAudioInputStream(url);
                int bytesToRead = ais.available();
                data = new byte[bytesToRead];
                int bytesRead = ais.read(data);
                if (bytesToRead != bytesRead)
                    throw new IllegalStateException("read only " + bytesRead + " of " + bytesToRead + " bytes");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("could not read '" + filename + "'", e);
        } catch (UnsupportedAudioFileException e) {
            throw new IllegalArgumentException("unsupported audio format: '" + filename + "'", e);
        }

        return data;
    }

    // saves audio samples to file
    public static void save(String filename, double[] samples) {
        if (samples == null) {
            throw new IllegalArgumentException("samples[] is null");
        }

        // use 16000hz 16-bit audio, mono, signed PCM, little Endian
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        byte[] data = new byte[2 * samples.length];
        for (int i = 0; i < samples.length; i++) {
            int temp = (short) (samples[i] * MAX_16_BIT);
            data[2 * i + 0] = (byte) temp;
            data[2 * i + 1] = (byte) (temp >> 8);
        }

        // now save the file
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            AudioInputStream ais = new AudioInputStream(bais, format, samples.length);
            if (filename.endsWith(".wav") || filename.endsWith(".WAV")) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filename));
            } else if (filename.endsWith(".au") || filename.endsWith(".AU")) {
                AudioSystem.write(ais, AudioFileFormat.Type.AU, new File(filename));
            } else {
                throw new IllegalArgumentException("unsupported audio format: '" + filename + "'");
            }
        } catch (IOException ioe) {
            throw new IllegalArgumentException("unable to save file '" + filename + "'", ioe);
        }
    }


}
