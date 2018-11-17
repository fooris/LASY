import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.List;

public class Main {


    public static void main(String args[]) throws UnsupportedAudioFileException {

        String audio = null;
        try {
            audio = FFMPEG.convertToAudioAndGetPath(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        double thres = 0.1;
        if(args.length >1){
            thres = Double.parseDouble(args[1]);
        }
        SilenceDetector sl =  new SilenceDetector(audio, thres, 0.5);
        List<Interval> fupelList = sl.detectNotSilence();
        sl.report(fupelList);
        try {
            FFMPEG.cut(args[0],"/tmp/out.mp4", fupelList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
