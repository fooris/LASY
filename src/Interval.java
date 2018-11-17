public class Interval {

    public static final int SAMPLE_RATE = 16000;

    private int samplePosStart;
    private int samplePosEnd;

    public Interval(int samplePosStart, int samplePosEnd) {

        //Convert to frame num and save in framePos
        double posStart = samplePosStart / (double) SAMPLE_RATE;
        double posEnd = samplePosEnd / (double) SAMPLE_RATE;

        this.samplePosStart = samplePosStart;
        this.samplePosEnd = samplePosEnd;
    }

    public double getTimEnd() {
        return samplePosStart / (double) SAMPLE_RATE;
    }

    public double getTimeEnd() {
        return samplePosEnd / (double) SAMPLE_RATE;
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