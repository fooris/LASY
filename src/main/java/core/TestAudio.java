package core;

import javax.sound.sampled.UnsupportedAudioFileException;

public class TestAudio {


    public static void main(String[] args) throws UnsupportedAudioFileException {

        double[] samples = AudioIO.load("./temp/audio.wav");

        SilenceDetector sl = new SilenceDetector(samples, 0.1, 0.5);
        sl.detectNotSilence();
        sl.report();

        double[] newSound = AudioTools.cut(sl.getCutSequence(), samples);

        AudioIO.save("./temp/test.wav", newSound);
        AudioTools.split("./temp/test", newSound, 10 * 1000 * 1000);

    }


}
