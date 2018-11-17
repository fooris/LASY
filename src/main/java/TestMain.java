import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.List;

public class TestMain {


    public static void main(String[] args) throws UnsupportedAudioFileException {

        SilenceDetector sl = new SilenceDetector("./temp/audio.wav", 0.1, (SoundLoader.SAMPLE_RATE / SoundLoader.REDUCTION_FACTOR) / 2);
        List<Interval> fupelList = sl.detectSilence();
        sl.report(fupelList);

    }


}
