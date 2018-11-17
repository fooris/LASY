package core;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AudioTools {

    public static List<String> split(String outFilename, double[] samples, int byteLength) throws UnsupportedAudioFileException {

        List<String> outPaths = new LinkedList<>();

        int samplesPerSplit = (int) ((byteLength - 44) * (1.0 / 2));
        int numSplits = samples.length / samplesPerSplit;
        if (samples.length % samplesPerSplit != 0) numSplits++;

        for (int i = 0; i < numSplits; i++) {
            double[] split = Arrays.copyOfRange(
                    samples,
                    i * samplesPerSplit,
                    Integer.min((i + 1) * samplesPerSplit, samples.length)
            );
            AudioIO.save(outFilename + "_" + i + ".wav", split);
            outPaths.add(outFilename + "_" + i + ".wav");
        }

        return outPaths;
    }

    public static double[] cut(List<Interval> intervals, double[] samples) {

        int newLength = intervals.stream()
                .map(i -> (int) (i.getTimeEnd() * AudioIO.SAMPLE_RATE) -
                        (int) (i.getTimeStart() * AudioIO.SAMPLE_RATE) + 1)
                .reduce(Integer::sum)
                .orElse(0);

        double[] newSamples = new double[newLength];
        int newIndex = 0;

        for (Interval i : intervals) {
            int startSample = (int) (i.getTimeStart() * AudioIO.SAMPLE_RATE);
            int endSample = (int) (i.getTimeEnd() * AudioIO.SAMPLE_RATE);
            for (int n = startSample; n <= endSample; n++) {
                newSamples[newIndex++] = samples[n];
            }
        }

        return newSamples;
    }

    public static double[] smoothCuts(List<Interval> intervals, double[] samples){
        return samples;
    }

}
