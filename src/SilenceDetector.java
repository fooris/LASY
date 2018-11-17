import java.util.ArrayList;
import java.util.List;

public class SilenceDetector {

    public static List<Interval> detectSilence(double[] values, double threshold_mult, int min_sample_length) {
        double max = 0;
        for (double v : values) max = Double.max(max, v);
        boolean currentlySilent;
        int startSample = -1;
        List<Interval> fupelList = new ArrayList<>();
        for (int i = 1; i < values.length; i++) {
            currentlySilent = values[i] < threshold_mult * max;
            // silence star
            if (startSample == -1 & currentlySilent) {
                startSample = i;
            }
            // silence stop
            if (startSample != -1 & !currentlySilent) {
                if (i - startSample > min_sample_length) fupelList.add(new Interval(startSample, i - 1));
                startSample = -1;
            }
        }
        return fupelList;
    }
}
