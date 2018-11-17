package core;

import java.util.List;

public class TestVideo {


    public static void main(String args[]) throws Exception {
//        String videoPath = "./temp/1lasser_short.mp4";
//
//        String audioPath = FFMPEG.convertToAudioAndGetPath(videoPath);
//
//        double threshold = 0.1;
//        double minCutLength = 0.5;
//
//
//        double[] samples = AudioIO.load(audioPath);
//
//        List<Interval> cutSequence = SilenceDetector.detectSilence(samples, threshold, minCutLength, false);
//        List<Interval> trimmedCutSequence = AudioTools.trim(cutSequence, 0.1);
//        CutStatistics.report(trimmedCutSequence, samples.length);
//
//        List<Interval> keepSequence = AudioTools.inverse(trimmedCutSequence, samples.length);
//
//        try {
//            FFMPEG.finalize(videoPath, "/tmp/out.mp4", keepSequence, true, 1.0f);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        LectureMaker lm = new LectureMaker("./temp/c14e2056-4b19-4e81-97ac-4a3046f30a3a.mp4");
        System.out.println(lm.genPreview());
        lm.genFinal();
        List<String> splitAudioPaths = AudioTools.split(lm.getOutputPath(), 10 * 1000 * 1000);
        for (String s : splitAudioPaths) {
            System.out.println(s);
        }
    }

}