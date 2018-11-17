package core;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.List;

public class TestAudio {


    public static void main(String[] args) throws UnsupportedAudioFileException {

        double[] samples = AudioIO.load("./temp/audio.wav");

        double minCutLength = 0.5;

        SilenceDetector sl = new SilenceDetector(samples, 0.1, minCutLength);
        sl.detectSilence();
        sl.report();

        List<Interval> cutSequence = sl.getCutSequence();
        cutSequence = AudioTools.pad(cutSequence, 0.1);
        double[] smoothedSamples = AudioTools.smoothCuts(sl.getCutSequence(), samples, 0.1);
        double[] newSamples = AudioTools.cut(sl.getCutSequence(), smoothedSamples);


        AudioIO.save("./temp/test.wav", newSamples);
        AudioTools.split("./temp/test", newSamples, 10 * 1000 * 1000);

    }


}
