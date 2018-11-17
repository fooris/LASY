import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.List;

public class Main {


    public static void main(String args[]) throws UnsupportedAudioFileException {

        String audio = FFMPEG.convertToAudioAndGetPath(args[0]);
        double thres = 0.1;
        if(args.length >1){
            thres = Double.parseDouble(args[1]);
        }
        SilenceDetector sl =  new SilenceDetector(audio, thres, (SoundLoader.SAMPLE_RATE / SoundLoader.REDUCTION_FACTOR) / 2);
        List<Interval> fupelList = sl.detectSilence();
        sl.report(fupelList);
    }

}
