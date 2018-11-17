import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.ArrayList;
import java.util.List;

public class SilenceDetector {

    private double thresholdMult;
    private double minCutLength;
    private double[] rawSound;

    public SilenceDetector(String fileName, double thresholdMult, double minCutLength) {
        try {
            this.rawSound = SoundIO.read(fileName);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        this.thresholdMult = thresholdMult;
        this.minCutLength = minCutLength;
    }

    public List<Interval> detectSilence() {
        double[] reducedSound = SoundIO.reduce(this.rawSound, SoundIO.REDUCTION_FACTOR);
        int kSize = (int) (1.5 * (SoundIO.SAMPLE_RATE / SoundIO.REDUCTION_FACTOR));
        Filter filter = new GaussFilter(kSize / 6, kSize);
        double[] filteredSound = filter.filter(reducedSound);

        double max = 0;
        for (double v : filteredSound) max = Double.max(max, Math.abs(v));

        boolean currentlySilent;
        int startSample = -1;
        List<Interval> fupelList = new ArrayList<>();
        for (int i = 1; i < filteredSound.length; i++) {
            currentlySilent = Math.abs(filteredSound[i]) < thresholdMult * max;
            // silence star
            if (startSample == -1 & currentlySilent) {
                startSample = i;
            }
            // silence stop
            if (startSample != -1 & !currentlySilent) {
                if (i - startSample > minCutLength * (SoundIO.SAMPLE_RATE / SoundIO.REDUCTION_FACTOR))
                    fupelList.add(new Interval(startSample, i - 1));
                startSample = -1;
            }
        }

        return fupelList;
    }

    public List<Interval> detectNotSilence() {
        List<Interval> silenceFupelList = detectSilence();

        // we disregard first and last silence
        List<Interval> fupelList = new ArrayList<>();
        for (int i = 1; i < silenceFupelList.size(); i++) {
            fupelList.add(new Interval(silenceFupelList.get(i - 1).getTimeEnd(),
                    silenceFupelList.get(i).getTimeStart()));
        }

        return fupelList;
    }

    public double[] getRawSound() {
        return this.rawSound;
    }

    public void report(List<Interval> fupelList) {
        double secondsSaved = fupelList.stream()
                .map(i -> i.getTimeEnd() - i.getTimeStart())
                .reduce(Double::sum)
                .orElse(0.0);

        double maxSecondsSaved = fupelList.stream()
                .map(i -> i.getTimeEnd() - i.getTimeStart())
                .reduce(Double::max)
                .orElse(0.0);

        double avgSecondsSaved = secondsSaved / fupelList.size();

        System.out.println("Number of cuts: " + fupelList.size());
        System.out.printf("Avg seconds saved/cut: %.2f\n", avgSecondsSaved);
        System.out.printf("Max seconds saved/cut: %.2f\n", maxSecondsSaved);
        System.out.printf("Seconds saved: %.2f\n", secondsSaved);
        System.out.printf("Percent saved: %.2f\n", 100.0 * secondsSaved / (rawSound.length / SoundIO.SAMPLE_RATE));

    }
}
