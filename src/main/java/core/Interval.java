package core;

public class Interval {

    private double timeStart;
    private double timeEnd;

    private int sampleStart;
    private int sampleEnd;

    public Interval(int sampleStart, int sampleEnd) {
        //Convert to frame num and save in framePos
        this.sampleStart = sampleStart;
        this.sampleEnd = sampleEnd;

        this.timeStart = sampleStart / (double) AudioIO.SAMPLE_RATE;
        this.timeEnd = sampleEnd / (double) AudioIO.SAMPLE_RATE;
    }

    public double getTimeStart() {
        return timeStart;
    }

    public double getTimeEnd() {
        return timeEnd;
    }

    public int getSampleStart() {
        return sampleStart;
    }

    public int getSampleEnd() {
        return sampleEnd;
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f)", timeStart, timeEnd);
    }
}