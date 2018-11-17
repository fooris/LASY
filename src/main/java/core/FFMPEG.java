package core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class FFMPEG {
    private static final String FFMPEG_PATH = "/usr/bin/ffmpeg";
    private static String tmpDir = System.getProperty("java.io.tmpdir");

    public static String convertToAudioAndGetPath(String inputFilePath) throws IOException {
        String outFilePath = tmpDir + "/audio.wav";
        String ffmpegCommand = FFMPEG_PATH + " -i " + " " + inputFilePath + " -ac 1 -ar 16000 -y " + outFilePath;
        System.out.println(ffmpegCommand);
        Process p = Runtime.getRuntime().exec(ffmpegCommand);
        try {
            p.waitFor();
        } catch (InterruptedException e) {
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

    static void cut(String inputFilePath, String outputFilePath, List<Interval> il) throws IOException {
        int segnum  = 0;
        double timePos = 0;

        String tmpFilePrefix = "segment";
        String extention = ".mp4";

        String ffmpegCmd = FFMPEG_PATH;
        ffmpegCmd += " -y -nostdin -i " + inputFilePath;

        BufferedWriter fileWrite = new BufferedWriter(new FileWriter("/tmp/segments.txt"));
        System.out.println();
        for (Interval i: il) {
            float start = (float) i.getTimeStart();
            float end = (float) i.getTimeEnd();
            String tmpOutFilePath = tmpDir + "/" + tmpFilePrefix + String.format("%05d", segnum) + extention;
            segnum ++;
            ffmpegCmd += " -c copy -ss " + start + " -to " + end + " " + tmpOutFilePath;
            fileWrite.write("file "+tmpOutFilePath + "\n");
        }
        fileWrite.close();
        System.out.println(ffmpegCmd);
        Process p = Runtime.getRuntime().exec(ffmpegCmd);

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        /*
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));
                */

        String s = null;
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }

        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("done");
        ffmpegCmd = FFMPEG_PATH + " -y -f concat -safe 0 -i /tmp/segments.txt";
        ffmpegCmd += " -c copy " + outputFilePath;
        System.out.println(ffmpegCmd);
        p = Runtime.getRuntime().exec(ffmpegCmd);

        stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        /*
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));
                */

        s = null;
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i<segnum; i++)
            Files.delete(Paths.get(tmpDir + "/" + tmpFilePrefix + String.format("%05d", i) + extention));
    }
}
