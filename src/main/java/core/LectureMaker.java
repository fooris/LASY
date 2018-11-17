package core;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class LectureMaker {

    String videoPath;
    String outputPath;

    boolean reencodeVideo;
    float speedUpFactor;
    double threshold;
    double minCutLength;
    double trimLength;
    boolean invert;

    private String audioPath;
    private double[] audioSamples;
    private List<Interval> finalCutSequence;

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void setReencodeVideo(boolean reencodeVideo) {
        this.reencodeVideo = reencodeVideo;
    }

    public void setSpeedUpFactor(float speedUpFactor) {
        this.speedUpFactor = speedUpFactor;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public void setMinCutLength(double minCutLength) {
        this.minCutLength = minCutLength;
    }

    public void setTrimLength(double trimLength) {
        this.trimLength = trimLength;
    }

    public void setInvert(boolean invert) {
        this.invert = invert;
    }

    LectureMaker(String videoPath) {
        this.videoPath = videoPath;
        outputPath = videoPath + ".conv.mp4";
        reencodeVideo = true;
        speedUpFactor = 1.0f;
        threshold = 0.1;
        minCutLength = 0.5;
        trimLength = 0.1;
        invert = false;
        try {
            applyParams();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyParams() throws Exception {
        audioPath = FFMPEG.convertToAudioAndGetPath(videoPath);
        audioSamples = AudioIO.load(audioPath);

        List<Interval> silenceSequence = SilenceDetector.detectSilence(audioSamples, threshold, minCutLength, invert);
        finalCutSequence = AudioTools.trim(silenceSequence, 0.1);
    }

    public String genPreview() throws IOException {
        // purge preview
        List<Interval> shortenedFinalCutSequence = finalCutSequence.stream()
                .filter(i -> i.getTimeEnd() < 30.0f)
                .collect(Collectors.toList());

        // invert finalize sequence (the final sequence is what we want to *keep*)
        List<Interval> shortenedFinalKeep = AudioTools.inverse(shortenedFinalCutSequence, (int) (30.0f * AudioIO.SAMPLE_RATE));

        // make call to finalize video
        FFMPEG.finalize(videoPath, "/tmp/preview.mp4", shortenedFinalKeep, reencodeVideo, speedUpFactor);

        return "/tmp/preview.mp4";
    }

    public void genFinal() throws IOException {
        // invert finalize sequence (the final sequence is what we want to *keep*)
        List<Interval> shortenedFinalKeep = AudioTools.inverse(finalCutSequence, (int) (30.0f * AudioIO.SAMPLE_RATE));
        // make call to finalize video
        FFMPEG.finalize(videoPath, outputPath, shortenedFinalKeep, reencodeVideo, speedUpFactor);
    }

    public double getVideoLength() {
        return audioSamples.length * AudioIO.SAMPLE_RATE;
    }

    public double getFinalVideoLength() {
        return CutStatistics.getSecondsCut(finalCutSequence) / speedUpFactor;
    }
}
