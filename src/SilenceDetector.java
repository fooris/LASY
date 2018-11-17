public class SilenceDetector {

    public static IntervalHandler detectSilence(double[] values, double threshold_mult) {

        double max = 0;
        for (double v : values) max = Double.max(max, v);
        boolean currentlySilent;
        int startSample = -1;
        IntervalHandler fupelList = new IntervalHandler();
        for (int i = 1; i < values.length; i++) {
            currentlySilent = values[i] < threshold_mult * max;
            // silence star
            if (startSample == -1 & currentlySilent) {
                startSample = i;
            }
            // silence stop
            if (startSample != -1 & !currentlySilent) {
                fupelList.addInterval(startSample, i - 1);
                startSample = -1;
            }
        }
        return fupelList;
    }
}
