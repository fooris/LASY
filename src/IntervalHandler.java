import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class IntervalHandler {

    public static final int SAMPLE_RATE = 16000;

    private List<Interval> intervals = new LinkedList<>();

    public void addInterval(int samplePosStart, int samplePosEnd) {
        intervals.add(new Interval(samplePosStart, samplePosEnd));
    }

    public List<Interval> getIntervals(){
        return intervals;
    }

    public class Interval {
        private int samplePosStart;
        private int samplePosEnd;

        private Interval(int samplePosStart, int samplePosEnd) {

            //Convert to frame num and save in framePos
            double posStart = ((float) samplePosStart) / SAMPLE_RATE;
            double posEnd = ((float) samplePosEnd) / SAMPLE_RATE;

            this.samplePosStart = samplePosStart;
            this.samplePosEnd = samplePosEnd;
        }

        public int getFramePosStart(int frameRate) {
            return samplePosStart * frameRate / SAMPLE_RATE;
        }

        public int getFramePosEnd(int frameRate) {
            return samplePosEnd * frameRate / SAMPLE_RATE;
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


}
