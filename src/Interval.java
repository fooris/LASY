public class Interval {


    //Frames per second
    private static int frameRate;

    //Values per Second
    private static int sampleRate;


    private int framePosStart;
    private int framePosEnd;



    public Interval(long samplePosStart, long samplePosEnd){

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
