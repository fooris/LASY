package lstatcore;

import java.util.ListIterator;

//This code is so beautiful to watch <3


public class Skipper {

    Stat stat;
    ListIterator<Long> it;
    String last = null;
    public Skipper(Stat stat){
        this.stat = stat;
    }

    public boolean hasNext(String s){
        if(last == null || !last.equals(s)) {
            it = stat.getTimesOfWord(s).listIterator();
        }
        last = s;
        return it.hasNext();
    }

    public boolean hasPrevious(String s){
        if(last == null || !last.equals(s)) {
            it = stat.getTimesOfWord(s).listIterator();
        }
        last = s;
        return it.hasPrevious();
    }

    public long next(String s){
        if(last == null || !last.equals(s)){
            it = stat.getTimesOfWord(s).listIterator();
        }
        last = s;
        return it.next();
    }

    public long previous(String s){
        if(last == null || !last.equals(s)){
            it = stat.getTimesOfWord(s).listIterator();
        }
        last = s;
        return it.previous();
    }



}
