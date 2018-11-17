package core;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.ArrayList;
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
        // invert to get intervals that are supposed to be kept
        intervals = AudioTools.inverse(intervals, samples.length);

        // calc length of cut audio
        int newLength = intervals.stream()
                .map(i -> i.getSampleEnd() - i.getSampleStart() + 1)
                .reduce(Integer::sum)
                .orElse(0);

        double[] newSamples = new double[newLength];
        int newIndex = 0;

        // copy intervals
        for (Interval i : intervals) {
            for (int n = i.getSampleStart(); n <= i.getSampleEnd(); n++) {
                newSamples[newIndex++] = samples[n];
            }
        }

        return newSamples;
    }

    public static double[] smoothCuts(List<Interval> cutIntervals, double[] samples, double smoothingInterval) {
        // copy samples
        double[] smoothSamples = Arrays.copyOf(samples, samples.length);

        // kernel size (make sure its uneven)
        int kernelSize = (int) (smoothingInterval * AudioIO.SAMPLE_RATE) | 1;

        // compute kernel
        double[] kernel = new double[kernelSize / 2 + 1];
        for (int i = 0; i < kernelSize / 2 + 1; i++) {
            kernel[i] = i / (kernelSize / 2);
        }

        // compute cut points
        List<Integer> cutPoints = new LinkedList<>();
        for (Interval i : cutIntervals) {
            cutPoints.add(i.getSampleStart());
            cutPoints.add(i.getSampleEnd());
        }

        // apply kernels
        for (Integer cutPoint : cutPoints) {
            for (int i = 0; i < kernelSize / 2 + 1; i++) {
                if (cutPoint + i < smoothSamples.length) smoothSamples[cutPoint + i] *= kernel[i];
                if (cutPoint - i >= 0) smoothSamples[cutPoint - i] *= kernel[i];
            }
        }


        return smoothSamples;
    }

    public static List<Interval> inverse(List<Interval> intervals, int numSamples) {
        List<Interval> inverseIntervals = new ArrayList<>();

        // start
        Interval startInterval = intervals.get(0);
        if (startInterval.getSampleStart() > 0) {
            Interval inverseStartInterval = new Interval(0, intervals.get(0).getSampleStart());
            inverseIntervals.add(inverseStartInterval);
        }

        // middle
        for (int i = 1; i < intervals.size(); i++) {
            inverseIntervals.add(new Interval(intervals.get(i - 1).getSampleEnd(),
                    intervals.get(i).getSampleStart()));
        }

        // end
        Interval endInterval = intervals.get(intervals.size() - 1);
        int lastSample = (numSamples - 1);
        if (lastSample - endInterval.getSampleEnd() > 0) {
            Interval inverseEndInterval = new Interval(endInterval.getSampleEnd(), lastSample);
            inverseIntervals.add(inverseEndInterval);
        }
        return inverseIntervals;
    }

}