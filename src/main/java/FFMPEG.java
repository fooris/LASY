import java.io.IOException;

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
}
