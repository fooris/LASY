package core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FFMPEG {
    private static final String FFMPEG_PATH = "/usr/bin/ffmpeg";
    private static String tmpDir = System.getProperty("java.io.tmpdir");

    public static String convertToAudioAndGetPath(String inputFilePath) throws IOException {
        String outFilePath = tmpDir + "/audio.wav";
        String ffmpegCommand = FFMPEG_PATH + " -nostdin -i " + " " + inputFilePath + " -ac 1 -ar 16000 -y " + outFilePath;
        System.out.println(ffmpegCommand);
        Process p = Runtime.getRuntime().exec(ffmpegCommand);
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return outFilePath;
    }

    /**
     * The final conversion method
     *
     * @param inputFilePath  The file that should be shortend
     * @param outputFilePath The output file
     * @param keepSequence   the parts in the file that we want to keep
     * @param reencodeVideo  worse on performance, but fixes some bugs in the output file
     * @param speedFactor    how fast should the video be accelerated? (0 or 1) for no acceleration
     * @throws IOException
     */
    public static void finalize(String inputFilePath, String outputFilePath, List<Interval> keepSequence, boolean reencodeVideo, float speedFactor) throws IOException {
        // make sure ffmpeg can finalize videos (problems with small sizes)
        keepSequence = keepSequence.stream()
                .filter(i -> (i.getTimeEnd() - i.getTimeStart() > 0.1))
                .collect(Collectors.toList());

        int segnum = 0;
        double timePos = 0;

        String tmpFilePrefix = "segment";
        String extention = ".mp4";

        String ffmpegCmd = FFMPEG_PATH;
        ffmpegCmd += " -y -nostdin -i " + inputFilePath;

        BufferedWriter fileWrite = new BufferedWriter(new FileWriter("/tmp/segments.txt"));
        System.out.println();
        for (Interval i : keepSequence) {
            float start = (float) i.getTimeStart();
            float end = (float) i.getTimeEnd();
            String tmpOutFilePath = tmpDir + "/" + tmpFilePrefix + String.format("%05d", segnum) + extention;
            segnum++;
            ffmpegCmd += " -c copy -ss " + start + " -to " + end + " " + tmpOutFilePath;
            fileWrite.write("file " + tmpOutFilePath + "\n");
        }
        fileWrite.close();
        System.out.println(ffmpegCmd);
        Process p = Runtime.getRuntime().exec(ffmpegCmd);

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

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
        ffmpegCmd = FFMPEG_PATH + " -nostdin -y -f concat -safe 0 -i /tmp/segments.txt ";

        boolean accelerate = !(speedFactor == 0 || speedFactor == 1);

        if (!accelerate)
            ffmpegCmd += (reencodeVideo ? "" : "-c:v copy") + " -c:a copy ";
        else
            ffmpegCmd += "-filter_complex [0:v]setpts=" + 1 / speedFactor + "*PTS[v];[0:a]atempo=" + speedFactor + "[a] -map [v] -map [a] ";

        ffmpegCmd += outputFilePath;
        System.out.println(ffmpegCmd);
        p = Runtime.getRuntime().exec(ffmpegCmd);

        stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        s = null;
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // cleanup
        for (int i = 0; i < segnum; i++) {
            Files.delete(Paths.get(tmpDir + "/" + tmpFilePrefix + String.format("%05d", i) + extention));
        }
        Files.delete(Paths.get(tmpDir + "/" + "segments.txt"));
    }
}