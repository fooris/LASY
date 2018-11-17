public abstract class Editor {

    private IntervalHandler intervalHandler;
    private String path;


    public Editor(String path, IntervalHandler fupleList){
        this.intervalHandler = fupleList;
        this.path = path;
    }

    public abstract String edit();
}
