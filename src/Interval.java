public class Interval {

    private int samplePosStart;
    private int samplePosEnd;

    public Interval(int samplePosStart, int samplePosEnd) {

        //Convert to frame num and save in framePos
        double posStart = samplePosStart / (double) SoundLoader.SAMPLE_RATE;
        double posEnd = samplePosEnd / (double) SoundLoader.SAMPLE_RATE;

        this.samplePosStart = samplePosStart;
        this.samplePosEnd = samplePosEnd;
    }

    public double getTimEnd() {
        return samplePosStart / (double) SoundLoader.SAMPLE_RATE;
    }

    public double getTimeEnd() {
        return samplePosEnd / (double) SoundLoader.SAMPLE_RATE;
    }

    public int getSamplePosStart() {
        return samplePosStart;
    }

    public int getSamplePosEnd() {
        return samplePosEnd;
    }

    @Override
    public String toString() {
        return "(" + samplePosStart + "," + samplePosEnd + ')';
    }
}