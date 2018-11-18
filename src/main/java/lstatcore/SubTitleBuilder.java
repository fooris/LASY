package lstatcore;

import com.google.cloud.speech.v1p1beta1.WordInfo;
import com.google.protobuf.Duration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SubTitleBuilder {


    public static final int WORDS_PER_LINE = 5;

    private  BufferedWriter bw;

    private long currStart;
    private long currEnd;

    private int lineCount = 1;

    StringBuilder sb = new StringBuilder();
    private String[] buff = new String[WORDS_PER_LINE];
    private int posInBuff;

    private String outputFileName;



    SubTitleBuilder(String outputFileName) throws IOException {
        FileWriter fw = new FileWriter(outputFileName);
        bw = new BufferedWriter(fw);
        this.outputFileName = outputFileName;
    }

    //return
    private long toMS(long sec, int nanoSec){
        return sec * 1000 + nanoSec/1000000;
    }

    private void flushBuffer() throws IOException {
        bw.write(String.valueOf(lineCount));
        bw.write(System.lineSeparator());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss,SSS");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        bw.write(timeFormat.format(new Date(currStart)));
        bw.write(" --> ");
        bw.write(timeFormat.format(new Date(currEnd)));
        bw.write(System.lineSeparator());

        bw.write(sb.toString());
        bw.write(System.lineSeparator());
        sb.setLength(0);

        for(int i = 0; i < posInBuff; i++){
            sb.append(buff[i]);
            sb.append(" ");
        }
        bw.write(sb.toString());
        bw.write(System.lineSeparator());
        bw.write(System.lineSeparator());

        lineCount++;
    }

    public void addWord(WordInfo word) throws IOException {
        if(posInBuff == 0){
            Duration d = word.getStartTime();
            currStart = toMS(d.getSeconds(),d.getNanos());
        }

        buff[posInBuff] = word.getWord();

        Duration d = word.getEndTime();
        currEnd = toMS(d.getSeconds(),d.getNanos());

        if(posInBuff == WORDS_PER_LINE - 1){
            flushBuffer();
        }

        posInBuff++;
        posInBuff %= WORDS_PER_LINE;
        // bw.write(content);
    }

    public String close() throws IOException {
        flushBuffer();
        bw.close();
        return outputFileName;
    }

}
