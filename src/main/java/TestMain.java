import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.List;

public class TestMain {


    public static void main(String[] args) throws UnsupportedAudioFileException {

        SilenceDetector sl = new SilenceDetector("./temp/audio.wav", 0.1, 0.5);
        List<Interval> fupelList = sl.detectSilence();
        sl.report(fupelList);

        double[] newSound = TestCutter.cut(fupelList, sl.getRawSound());

        SoundLoader.save("./temp/test.wav", newSound);

    }


}
