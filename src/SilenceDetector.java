import java.util.List;

public class SilenceDetector {

    public static List<IntervalHandler.Interval> detectSilence(double[] values, double threshold_mult, int min_sample_length) {
        double max = 0;
        for (double v : values) max = Double.max(max, v);
        boolean currentlySilent;
        int startSample = -1;
        IntervalHandler fupelHandler = new IntervalHandler();
        for (int i = 1; i < values.length; i++) {
            currentlySilent = values[i] < threshold_mult * max;
            // silence star
            if (startSample == -1 & currentlySilent) {
                startSample = i;
            }
            // silence stop
            if (startSample != -1 & !currentlySilent) {
                if (i - startSample > min_sample_length) fupelHandler.addInterval(startSample, i - 1);
                startSample = -1;
            }
        }
        return fupelHandler.getIntervals();
    }
}
