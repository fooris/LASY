import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FFMPEG {
    private static final String FFMPEG_PATH = "/usr/bin/ffmpeg";

    public static String convertToAudioAndGetPath(String inputFilePath) {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String outFilePath = tmpDir + "/audio.wav";
        String ffmpegCommand = FFMPEG_PATH + " -i " + " " + inputFilePath + " -ac 1 -ar 16000 -y " + outFilePath;
        System.out.println(ffmpegCommand);
        try {
            Runtime.getRuntime().exec(ffmpegCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outFilePath;
    }

    //TODO: fix scanner
    public static List<Interval> getSilence(String inputFilePath, double db, double minLegth) {
        String super1337awkCommand = "awk '$4 == \"silence_end:\" { print $5 \" \" $8 }'";
        String ffmpegCommand = FFMPEG_PATH + " -i " + " " + inputFilePath + " -af silencedetect=n=" + db + "dB:d=" + minLegth + " -f null -";

        String cmd = ffmpegCommand + " 2>&1 >/dev/null | " + super1337awkCommand;
        System.out.println(ffmpegCommand);
        List<Interval> il = new ArrayList<>();
        try {
            Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream());
            System.out.println(s.next());
            if (s.hasNext())
                do {
                    for (int i = 0; i < 2; i++) {
                        Interval ival;
                        double start;
                        double end;
                        end = s.nextDouble();
                        start = end - s.nextDouble();
                        ival = new Interval(start, end);
                        il.add(ival);
                    }
                } while (s.hasNext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return il;
    }

    /*
    static void cut(String path, IntervalHandler ih) {
        for (Interval i: ih.iterator())
    }
    */
}
