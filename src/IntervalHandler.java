import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class IntervalHandler implements Iterable {


    //Values per Second
    private int sampleRate = 16000;

    //Frames per second
    private int frameRate;


    private List<Interval> intervals = new LinkedList<>();


    public IntervalHandler(int frameRate, int sampleRate){
        this.frameRate = frameRate;
        this.sampleRate = sampleRate;
    }

    public IntervalHandler(int frameRate){
        this.frameRate = frameRate;
    }

    public void addInterval(long samplePosStart, long samplePosEnd){
        intervals.add(new Interval(samplePosStart , samplePosEnd));
    }

    @Override
    public Iterator iterator() {
        return intervals.iterator();
    }


    public class Interval{
        private int framePosStart;
        private int framePosEnd;



        private Interval(long samplePosStart, long samplePosEnd){

            //Convert to frame num and save in framePos
            double posStart = ((float) samplePosStart) / sampleRate;
            double posEnd = ((float) samplePosEnd) / sampleRate;

            framePosStart = (int) Math.round(posStart * frameRate);
            framePosEnd = (int) Math.round(posEnd * frameRate);
        }

        public int getFramePosStart() {
            return framePosStart;
        }

        public int getFramePosEnd() {
            return framePosEnd;
        }
    }


}
