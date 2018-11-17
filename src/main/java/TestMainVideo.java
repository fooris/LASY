import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.List;

public class TestMainVideo {


    public static void main(String[] args) throws UnsupportedAudioFileException {

        SilenceDetector sl = new SilenceDetector("./temp/audio.wav", 0.1, 0.5);
        List<Interval> fupelList = sl.detectNotSilence();
        sl.report(fupelList);

        double[] newSound = SimpleCutter.cut(fupelList, sl.getRawSound());

        SoundIO.save("./temp/test.wav", newSound);

    }


}
