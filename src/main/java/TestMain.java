import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.List;

public class TestMain {


    public static final int SAMPLE_RATE = 16000;
    public static final int REDUCTION_FACTOR = 50;


    public static void main(String[] args) throws UnsupportedAudioFileException {

        double[] sound = read("./temp/audio.wav");

        sound = reduce(sound, REDUCTION_FACTOR);
        int kSize = (int) (1.5 * (SAMPLE_RATE / REDUCTION_FACTOR));
        GaussFilter filter = new GaussFilter(kSize / 6, kSize);
        sound = filter.filter(sound);

        List<Interval> fupelList = SilenceDetector.detectSilence(sound, 0.1, (SAMPLE_RATE / REDUCTION_FACTOR) / 2);

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
        System.out.printf("Percent saved: %.2f\n", 100.0 * secondsSaved / (sound.length / (SoundLoader.SAMPLE_RATE / REDUCTION_FACTOR)));

    }



}
