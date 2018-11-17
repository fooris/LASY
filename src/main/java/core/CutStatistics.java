package core;

import java.util.List;

public class CutStatistics {

    public static double getSecondsCut(List<Interval> intervals) {
        return intervals.stream()
                .map(i -> i.getTimeEnd() - i.getTimeStart())
                .reduce(Double::sum)
                .orElse(0.0);
    }

    public static double getMaxSecondsCut(List<Interval> intervals) {
        return intervals.stream()
                .map(i -> i.getTimeEnd() - i.getTimeStart())
                .reduce(Double::max)
                .orElse(0.0);
    }

    public static double getMinSecondsCut(List<Interval> intervals) {
        return intervals.stream()
                .map(i -> i.getTimeEnd() - i.getTimeStart())
                .reduce(Double::min)
                .orElse(0.0);
    }

    public static void report(List<Interval> intervals, int origSampleLength) {
        double secondsCut = getSecondsCut(intervals);
        double maxSecondsSaved = getMaxSecondsCut(intervals);
        double minSecondsSaved = getMinSecondsCut(intervals);

        double avgSecondsCut = secondsCut / intervals.size();

        System.out.printf("Number of cuts: %d\n", intervals.size());
        System.out.printf("Seconds saved: %.2f\n", secondsCut);
        System.out.printf("Avg seconds saved/cut: %.2f\n", avgSecondsCut);
        System.out.printf("Max seconds saved/cut: %.2f\n", maxSecondsSaved);
        System.out.printf("Min seconds saved/cut: %.2f\n", minSecondsSaved);
        System.out.printf("Seconds saved: %.2f\n", secondsCut);
        System.out.printf("Percent saved: %.2f\n", 100.0 * secondsCut / (origSampleLength / AudioIO.SAMPLE_RATE));
    }

}
