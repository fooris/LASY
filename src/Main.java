import java.io.IOException;

public class Main {

    private static final String FFMPEG_PATH = "/usr/bin/ffmpeg";

    public static void main(String args[]) {

        convertToAudioAndGetPath(args[0]);
        System.out.println(args[0] );
    }

    static String convertToAudioAndGetPath(String inputFilePath) {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String outFilePath = tmpDir + "/audio.wav";
        String ffmpegCommand = FFMPEG_PATH + " -i " + " " + inputFilePath + " -sample_rate 16000 -y " + outFilePath;
        System.out.println(ffmpegCommand);
        try {
            Runtime.getRuntime().exec(ffmpegCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outFilePath;
    }
}
