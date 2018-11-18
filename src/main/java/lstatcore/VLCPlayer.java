package lstatcore;


import java.io.IOException;

public class VLCPlayer {

    private static final String PATH_VLC = "vlc";

    private static Process instance = null;



    public static void start(String video, String subtitle) throws IOException {
        if(instance != null) {
            instance.destroy();
        }
        instance = new ProcessBuilder(PATH_VLC, video, "--sub-file=" + subtitle, "--no-video-title").start();
    }

    public static void jumpTo(String video, String subtitle, long sec) throws IOException {
        if(instance != null) {
            instance.destroy();
        }
        instance = new ProcessBuilder(PATH_VLC, video, " --sub-file=" + subtitle, "--start-time=" + sec, "--no-video-title").start();
    }


    public static void stop(){
        if(instance != null) {
            instance.destroy();
        }
    }

}
