package core;

import java.util.ArrayList;
import java.util.List;

public class SilenceDetector {

    private double thresholdMult;
    private int minSampleLength;
    private double[] samples;
    private List<Interval> intervals;

    public SilenceDetector(double[] samples, double thresholdMult, double minCutLength) {
        this.samples = samples;
        this.thresholdMult = thresholdMult;
        this.minSampleLength = (int) (minCutLength * AudioIO.SAMPLE_RATE);
        intervals = new ArrayList<>();
    }

    public void detectSilence() {
        double[] reducedSound = AudioIO.reduce(this.samples, AudioIO.REDUCTION_FACTOR);

        int filterSize = (int) ((1.5 * AudioIO.SAMPLE_RATE) / AudioIO.REDUCTION_FACTOR)+1;
        int sigma = filterSize / 6;
        Filter filter = new GaussFilter(sigma, filterSize);
        double[] filteredReducedSound = filter.filter(reducedSound);

        double max = 0;
        for (double v : filteredReducedSound) max = Double.max(max, Math.abs(v));

        boolean currentlySilent;
        int startSample = -1;
        List<Interval> intervals = new ArrayList<>();
        for (int i = 1; i < filteredReducedSound.length; i++) {
            currentlySilent = Math.abs(filteredReducedSound[i]) < thresholdMult * max;
            // silence start
            if (startSample == -1 & currentlySilent) {
                startSample = i;
            }
            // silence stop
            if (startSample != -1 & !currentlySilent) {
                // if min cut length is satisfied
                if (i - startSample > minSampleLength / AudioIO.REDUCTION_FACTOR) {
                    Interval detectedInterval = new Interval(
                            startSample * AudioIO.REDUCTION_FACTOR,
                            (i - 1) * AudioIO.REDUCTION_FACTOR
                    );
                    intervals.add(detectedInterval);
                }
                startSample = -1;
            }
        }

        this.intervals = intervals;
    }

    public void detectNotSilence() {
        detectSilence();
        intervals = AudioTools.inverse(intervals, samples.length);
    }

    public double getSecondsCut() {
        return intervals.stream()
                .map(i -> i.getTimeEnd() - i.getTimeStart())
                .reduce(Double::sum)
                .orElse(0.0);
    }

    public double getMaxSecondsCut() {
        return intervals.stream()
                .map(i -> i.getTimeEnd() - i.getTimeStart())
                .reduce(Double::max)
                .orElse(0.0);
    }

    public void report() {

        double secondsCut = getSecondsCut();
        double maxSecondsSaved = getMaxSecondsCut();

        double avgSecondsCut = secondsCut / intervals.size();

        System.out.printf("Number of cuts: %d\n", intervals.size());
        System.out.printf("Seconds saved/cut: %.2f\n", secondsCut);
        System.out.printf("Avg seconds saved/cut: %.2f\n", avgSecondsCut);
        System.out.printf("Max seconds saved/cut: %.2f\n", maxSecondsSaved);
        System.out.printf("Seconds saved: %.2f\n", secondsCut);
        System.out.printf("Percent saved: %.2f\n", 100.0 * secondsCut / (this.samples.length / AudioIO.SAMPLE_RATE));
    }

    public List<Interval> getCutSequence() {
        return intervals;
    }
}
