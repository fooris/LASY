package core;

import javax.sound.sampled.UnsupportedAudioFileException;

public class TestAudio {


    public static void main(String[] args) throws UnsupportedAudioFileException {

        double[] samples = AudioIO.load("./temp/audio.wav");

        SilenceDetector sl = new SilenceDetector(samples, 0.1, 0.5);
        sl.detectSilence();
        sl.report();

        double[] smoothedSamples = AudioTools.smoothCuts(sl.getCutSequence(), samples, 0.1);
        double[] newSamples = AudioTools.cut(sl.getCutSequence(), smoothedSamples);


        AudioIO.save("./temp/test.wav", newSamples);
        AudioTools.split("./temp/test", newSamples, 10 * 1000 * 1000);

    }


}
