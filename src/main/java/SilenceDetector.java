import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.ArrayList;
import java.util.List;

public class SilenceDetector {

    private String fileName;
    private double threshold_mult;
    private int min_sample_length;
    private double[] rawSound;

    public SilenceDetector(String fileName, double threshold_mult, int min_sample_length) {
        this.fileName = fileName;
        try {
            this.rawSound = SoundLoader.read(fileName);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        this.threshold_mult = threshold_mult;
        this.min_sample_length = min_sample_length;
    }

    public List<Interval> detectSilence() throws UnsupportedAudioFileException {

        double[] reducedSound = SoundLoader.reduce(this.rawSound, SoundLoader.REDUCTION_FACTOR);
        int kSize = (int) (1.5 * (SoundLoader.SAMPLE_RATE / SoundLoader.REDUCTION_FACTOR));
        Filter filter = new GaussFilter(kSize / 6, kSize);
        double[] filteredSound = filter.filter(reducedSound);

        double max = 0;
        for (double v : filteredSound) max = Double.max(max, Math.abs(v));

        boolean currentlySilent;
        int startSample = -1;
        List<Interval> fupelList = new ArrayList<>();
        for (int i = 1; i < filteredSound.length; i++) {
            currentlySilent = Math.abs(filteredSound[i]) < threshold_mult * max;
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
        System.out.printf("Percent saved: %.2f\n", 100.0 * secondsSaved / (rawSound.length / SoundLoader.SAMPLE_RATE));

    }
}
