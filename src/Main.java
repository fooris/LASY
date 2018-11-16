import java.io.IOException;

public class Main {


    public static void main(String args[]) {

        FFMPEG.convertToAudioAndGetPath(args[0]);
        System.out.println(args[0] );
    }

}
