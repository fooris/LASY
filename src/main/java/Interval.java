public class Interval {

    private double timeStart;
    private double timeEnd;



    public Interval(int samplePosStart, int samplePosEnd) {
        //Convert to frame num and save in framePos
        this.timeStart = samplePosStart * SoundLoader.REDUCTION_FACTOR / (double) SoundLoader.SAMPLE_RATE;
        this.timeEnd = samplePosEnd * SoundLoader.REDUCTION_FACTOR / (double) SoundLoader.SAMPLE_RATE;
    }

    public Interval(double timeStart, double timeEnd) {
        //Convert to frame num and save in framePos
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public double getTimeStart() {
        return timeStart;
    }

    public double getTimeEnd() {
        return timeEnd;
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f)", timeStart, timeEnd);
    }
}