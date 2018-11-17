import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.List;

public class TestMain {


    public static void main(String[] args) throws UnsupportedAudioFileException {

        SilenceDetector sl = new SilenceDetector("./temp/audio.wav", 0.1, 0.05);
        List<Interval> fupelList = sl.detectSilence();
        sl.report(fupelList);

        double[] newSound = SimpleCutter.cut(fupelList, sl.getRawSound());

        SoundIO.save("./temp/test.wav", newSound);
        AudioSplitter.split("./temp/test", newSound, 10 * 1000 * 1000);

    }


}
