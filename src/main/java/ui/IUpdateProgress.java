package ui;

public interface IUpdateProgress {
    public void updateProgress(double progress);

    public void updateState(String state);

    public void done();
}
