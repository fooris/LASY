import java.util.ArrayList;
import java.util.List;

public class SilenceDetector {

    private double thresholdMult;
    private double minCutLength;
    private double[] samples;
    private List<Interval> intervals;

    public SilenceDetector(double[] samples, double thresholdMult, double minCutLength) {
        this.samples = samples;
        this.thresholdMult = thresholdMult;
        this.minCutLength = minCutLength;
        intervals = new ArrayList<>();
    }

    public void detectSilence() {
        double[] reducedSound = AudioIO.reduce(this.samples, AudioIO.REDUCTION_FACTOR);
        int kSize = (int) (1.5 * (AudioIO.SAMPLE_RATE / AudioIO.REDUCTION_FACTOR));
        Filter filter = new GaussFilter(kSize / 6, kSize);
        double[] filteredSound = filter.filter(reducedSound);

        double max = 0;
        for (double v : filteredSound) max = Double.max(max, Math.abs(v));

        boolean currentlySilent;
        int startSample = -1;
        List<Interval> intervals = new ArrayList<>();
        for (int i = 1; i < filteredSound.length; i++) {
            currentlySilent = Math.abs(filteredSound[i]) < thresholdMult * max;
            // silence star
            if (startSample == -1 & currentlySilent) {
                startSample = i;
            }
            // silence stop
            if (startSample != -1 & !currentlySilent) {
                if (i - startSample > minCutLength * (AudioIO.SAMPLE_RATE / AudioIO.REDUCTION_FACTOR))
                    intervals.add(new Interval(startSample, i - 1));
                startSample = -1;
            }
        }

        this.intervals = intervals;
    }

    public void detectNotSilence() {
        detectSilence();
        List<Interval> oldIntervals = this.intervals;

        // we disregard first and last silence
        List<Interval> intervals = new ArrayList<>();
        for (int i = 1; i < oldIntervals.size(); i++) {
            intervals.add(new Interval(oldIntervals.get(i - 1).getTimeEnd(),
                    oldIntervals.get(i).getTimeStart()));
        }

        this.intervals = intervals;
    }

    public void report() {
        double secondsSaved = intervals.stream()
                .map(i -> i.getTimeEnd() - i.getTimeStart())
                .reduce(Double::sum)
                .orElse(0.0);

        double maxSecondsSaved = intervals.stream()
                .map(i -> i.getTimeEnd() - i.getTimeStart())
                .reduce(Double::max)
                .orElse(0.0);

        double avgSecondsSaved = secondsSaved / intervals.size();

        System.out.println("Number of cuts: " + intervals.size());
        System.out.printf("Avg seconds saved/cut: %.2f\n", avgSecondsSaved);
        System.out.printf("Max seconds saved/cut: %.2f\n", maxSecondsSaved);
        System.out.printf("Seconds saved: %.2f\n", secondsSaved);
        System.out.printf("Percent saved: %.2f\n", 100.0 * secondsSaved / (this.samples.length / AudioIO.SAMPLE_RATE));

    }

    public List<Interval> getCutSequence() {
        return intervals;
    }
}
