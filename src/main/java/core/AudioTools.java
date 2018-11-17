package core;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AudioTools {

    public static List<String> split(String videoPath, int byteLength) throws IOException, UnsupportedAudioFileException {
        String audioPath = FFMPEG.convertToAudioAndGetPath(videoPath);
        double[] samples = AudioIO.load(audioPath);

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
            String fileName = "/tmp/split"+ i + ".wav";
            AudioIO.save(fileName, split);
            outPaths.add(fileName);
        }

        return outPaths;
    }

    public static double[] cut(List<Interval> cutSequence, double[] samples) {
        // invert to get intervals that are supposed to be kept
        List<Interval> keepSequence = AudioTools.inverse(cutSequence, samples.length);

        // calc length of finalize audio
        int newLength = keepSequence.stream()
                .map(i -> i.getSampleEnd() - i.getSampleStart() + 1)
                .reduce(Integer::sum)
                .orElse(0);

        double[] newSamples = new double[newLength];
        int newIndex = 0;

        // copy intervals
        for (Interval i : keepSequence) {
            for (int n = i.getSampleStart(); n <= i.getSampleEnd(); n++) {
                newSamples[newIndex++] = samples[n];
            }
        }

        return newSamples;
    }

    public static double[] smoothCuts(List<Interval> cutSequence, double[] samples, double smoothingInterval) {
        // copy samples
        double[] smoothSamples = Arrays.copyOf(samples, samples.length);

        // kernel size (make sure its uneven)
        int kernelSize = (int) (smoothingInterval * AudioIO.SAMPLE_RATE) | 1;

        // compute kernel
        double[] kernel = new double[kernelSize / 2 + 1];
        for (int i = 0; i < kernelSize / 2 + 1; i++) {
            kernel[i] = i / (kernelSize / 2);
        }

        // compute finalize points
        List<Integer> cutPoints = new LinkedList<>();
        for (Interval i : cutSequence) {
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

    public static List<Interval> trim(List<Interval> cutSequence, double trimInterval) {
        int trimIntervalSamples = (int) (trimInterval * AudioIO.SAMPLE_RATE) / 2;
        List<Interval> trimIntervals = new LinkedList<>();

        // start
        Interval trimStartInterval = new Interval(
                cutSequence.get(0).getSampleStart(),
                cutSequence.get(0).getSampleEnd() - trimIntervalSamples);
        trimIntervals.add(trimStartInterval);

        // middle
        for (int i = 1; i < cutSequence.size() - 1; i++) {
            Interval paddedInterval = new Interval(
                    cutSequence.get(i).getSampleStart() + trimIntervalSamples,
                    cutSequence.get(i).getSampleEnd() - trimIntervalSamples);
            trimIntervals.add(paddedInterval);
        }

        //end
        Interval trimEndInterval = new Interval(
                cutSequence.get(cutSequence.size() - 1).getSampleStart() + trimIntervalSamples,
                cutSequence.get(cutSequence.size() - 1).getSampleEnd());
        trimIntervals.add(trimEndInterval);

        return trimIntervals.stream()
                .filter(i -> (i.getSampleEnd() - i.getSampleStart() > 0))
                .collect(Collectors.toList());
    }
}
