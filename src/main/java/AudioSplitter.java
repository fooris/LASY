import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AudioSplitter {

    public static List<String> split(String filename, double[] samples, int byteLength) throws UnsupportedAudioFileException {

        List<String> outPaths = new LinkedList<>();

        int samplesPerSplit = (int) ((byteLength - 44) * (1.0 / 2));
        int numSplits = samples.length / samplesPerSplit;
        if (samples.length % samplesPerSplit != 0) numSplits++;

        for (int i = 0; i < numSplits; i++) {
            double[] split = Arrays.copyOfRange(
                    samples,
                    i * samplesPerSplit,
                    Integer.min((i + 1) * samplesPerSplit, samples.length)
            );
            SoundIO.save(filename + "_" + i + ".wav", split);
            outPaths.add(filename + "_" + i + ".wav");
        }

        return outPaths;
    }
}
