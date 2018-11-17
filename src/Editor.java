import java.util.List;

public abstract class Editor {

    private List<Interval> intervalHandler;
    private String path;


    public Editor(String path, List<Interval> fupleList){
        this.intervalHandler = fupleList;
        this.path = path;
    }

    public abstract String edit();
}
