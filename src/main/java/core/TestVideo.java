package core;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TestVideo {


    public static void main(String args[]) throws UnsupportedAudioFileException {
        String videoPath = "./temp/1lasser_short.mp4";
        if (args.length > 0) {
            videoPath = args[0];
        }

        String audioPath = null;
        try {
            audioPath = FFMPEG.convertToAudioAndGetPath(videoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        double threshold = 0.1;
        if (args.length > 1) {
            threshold = Double.parseDouble(args[1]);
        }

        double minCutLength = 0.5;
        if (args.length > 2) {
            minCutLength = Double.parseDouble(args[2]);
        }

        double[] samples = AudioIO.load(audioPath);

        SilenceDetector sl = new SilenceDetector(samples, threshold, minCutLength);
        sl.detectSilence();

        List<Interval> trimmedCutSequence = AudioTools.trim(sl.getCutSequence(), 0.1);
        CutStatistics.report(trimmedCutSequence, samples.length);

        // TODO: audio preprocessing
        //double[] smoothedSamples = AudioTools.smoothCuts(paddedCutSequence, samples, 0.01);
        //double[] newSamples = AudioTools.cut(paddedCutSequence, smoothedSamples);

        try {
            FFMPEG.cut(videoPath, "/tmp/out.mp4", trimmedCutSequence, true, 0.0f, samples.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
