package core;

import java.util.ArrayList;
import java.util.List;

public class SilenceDetector {

    public static List<Interval> detectSilence(double[] samples, double thresholdMult, double minCutLength, boolean invert) {
        int minSampleLength = (int) (minCutLength * AudioIO.SAMPLE_RATE);

        double[] reducedSound = AudioIO.reduce(samples, AudioIO.REDUCTION_FACTOR);

        int filterSize = (int) ((1.5 * AudioIO.SAMPLE_RATE) / AudioIO.REDUCTION_FACTOR) + 1;
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
                // if min finalize length is satisfied
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

        //TODO FIX INVERT=TRUE
        if(invert){
            return AudioTools.inverse(intervals, samples.length);
        }else{
            return intervals;
        }
    }

}
