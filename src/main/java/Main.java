import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.List;

public class Main {


    public static void main(String args[]) throws UnsupportedAudioFileException {

        String audioPath = FFMPEG.convertToAudioAndGetPath(args[0]);

        double threshold = 0.1;
        if (args.length > 1) {
            threshold = Double.parseDouble(args[1]);
        }

        double minCutLength = 0.5;
        if (args.length > 2) {
            minCutLength = Double.parseDouble(args[2]);
        }

        double[] samples = AudioIO.read(audioPath);

        SilenceDetector sl = new SilenceDetector(samples, threshold, minCutLength);
        sl.detectNotSilence();
        sl.report();

        double[] newSamples = AudioTools.cut(sl.getCutSequence(), samples);
        AudioIO.save("./temp/test.wav", newSamples);
    }

}
