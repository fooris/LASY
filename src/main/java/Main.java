import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.List;

public class Main {


    public static void main(String args[]) throws UnsupportedAudioFileException {

        String audio = FFMPEG.convertToAudioAndGetPath(args[0]);
        double thres = 0.1;
        if(args.length >1){
            thres = Double.parseDouble(args[1]);
        }
        SilenceDetector sl =  new SilenceDetector(audio, thres, 0.5);
        List<Interval> fupelList = sl.detectSilence();
        sl.report(fupelList);
    }

}
