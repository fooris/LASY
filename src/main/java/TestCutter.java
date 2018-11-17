import java.util.List;

public class TestCutter {

    public static double[] cut(List<Interval> fupelList, double[] oldSound) {

        int newLength = fupelList.stream()
                .map(i -> (int) (i.getTimeEnd() * SoundLoader.SAMPLE_RATE) -
                        (int) (i.getTimeStart() * SoundLoader.SAMPLE_RATE) + 1)
                .reduce(Integer::sum)
                .orElse(0);

        double[] newSound = new double[newLength];
        int newIndex = 0;

        for (Interval i : fupelList) {
            int startSample = (int) (i.getTimeStart() * SoundLoader.SAMPLE_RATE);
            int endSample = (int) (i.getTimeEnd() * SoundLoader.SAMPLE_RATE);
            for (int n = startSample; n <= endSample; n++) {
                newSound[newIndex++] = oldSound[n];
            }
        }

        return newSound;
    }


}
