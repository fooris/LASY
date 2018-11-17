import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.ArrayList;
import java.util.List;

public class SilenceDetector {

    private String fileName;
    private double threshold_mult;
    private int min_sample_length;
    private int soundLength;
    public SilenceDetector(String fileName, double threshold_mult, int min_sample_length ){
        this.fileName = fileName;
        this.threshold_mult = threshold_mult;
        this.min_sample_length = min_sample_length;

    }

    public List<Interval> detectSilence() throws UnsupportedAudioFileException {


        double[] sound = SoundLoader.read(fileName );


        sound = SoundLoader.reduce(sound, SoundLoader.REDUCTION_FACTOR);
        int kSize = (int) (1.5 * (SoundLoader.SAMPLE_RATE / SoundLoader.REDUCTION_FACTOR));
        GaussFilter filter = new GaussFilter(kSize / 6, kSize);
        sound = filter.filter(sound);

        double max = 0;
        for (double v : sound) max = Double.max(max, Math.abs(v));

        boolean currentlySilent;
        int startSample = -1;
        List<Interval> fupelList = new ArrayList<>();
        for (int i = 1; i < sound.length; i++) {
            currentlySilent = Math.abs(sound[i]) < threshold_mult * max;
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

        soundLength = sound.length;
        return fupelList;
    }



    public void report(List<Interval> fupelList){
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
        System.out.printf("Percent saved: %.2f\n", 100.0 * secondsSaved / (soundLength / (SoundLoader.SAMPLE_RATE / SoundLoader.REDUCTION_FACTOR)));
    }
}
