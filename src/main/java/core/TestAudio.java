package core;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.List;

public class TestAudio {


    public static void main(String[] args) throws UnsupportedAudioFileException {

        double[] samples = AudioIO.load("./temp/audio.wav");

        SilenceDetector sl = new SilenceDetector(samples, 0.10, 0.3);
        sl.detectSilence();

        List<Interval> cutSequence = sl.getCutSequence();
        CutStatistics.report(cutSequence, samples.length);

        List<Interval> paddedCutSequence = AudioTools.trim(cutSequence, 0.1);
        CutStatistics.report(paddedCutSequence, samples.length);

        double[] smoothedSamples = AudioTools.smoothCuts(paddedCutSequence, samples, 0.01);
        double[] newSamples = AudioTools.cut(paddedCutSequence, smoothedSamples);

        AudioIO.save("./temp/test.wav", newSamples);
        //AudioTools.split("./temp/test", newSamples, 10 * 1000 * 1000);

    }


}
