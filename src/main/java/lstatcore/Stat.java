package lstatcore;

import com.google.cloud.speech.v1p1beta1.WordInfo;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Stat {



    private int words;

    private double origLength;
    private double newLength;

    private String subTitleFile;

    private int curseWordCount;
    private int fillWordCount;
    private int emCount;
    Language language;

    private Map<String, List<Long>> secondToWordList = new HashMap<>();


    Stat(Language language, double origLength, double newLength) {
        this.language = language;
        this.origLength = origLength;
        this.newLength = newLength;
    }


    void addWord(WordInfo wi) {
        words++;
        String word = wi.getWord();
        long startSec = wi.getStartTime().getSeconds();
        List<Long> res = secondToWordList.get(word);
        if (res == null) {
            res = new LinkedList<Long>();
            secondToWordList.put(word,res );
        }
        res.add(startSec);


        if (Dicts.isCurseWord(word, language)) {
            curseWordCount++;
        }
        if (Dicts.isFillWord(word, language)) {
            fillWordCount++;
        }
    }

    void setSubTitleFile(String subTitleFile){
        this.subTitleFile = subTitleFile;
    }

    public String getSubTitleFile(){
        return subTitleFile;
    }


    public List<Long> getTimesOfWord(String word) {
        return secondToWordList.get(word);
    }

    public double getWordsPerMinute() {
        return words / origLength;
    }

    public double getCursesPerMinute() {
        return curseWordCount / origLength;
    }

    public double getFillWordsPerMinute() {
        return fillWordCount / origLength;
    }

    public int getWords() {
        return words;
    }

    public int getCurseWordCount() {
        return curseWordCount;
    }

    public int getFillWordCount() {
        return fillWordCount;
    }

    public int getEmCount(){
        return emCount;
    }

    public void save(String outputFile){

        try (FileWriter fw = new FileWriter(outputFile); ){

            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(new Gson().toJson(this));

            bw.close();

        }catch (IOException x){
            System.err.println("FAILED saving " + outputFile);
        }


    }
}
