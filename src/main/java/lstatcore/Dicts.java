package lstatcore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashSet;
import java.util.Set;

class Dicts {

    private static final Set<String> CURSE_WORDS_DE;
    private static final Set<String> FILL_WORDS_DE;

    private static final Set<String> CURSE_WORDS_EN;
    private static final Set<String> FILL_WORDS_EN;



    private static final String PATH = "dicts/";


    static {
        CURSE_WORDS_DE = initSet(PATH + "cursewordsDE.txt");
        FILL_WORDS_DE = initSet(PATH + "fillwordsDE.txt");

        CURSE_WORDS_EN = initSet(PATH + "cursewordsDE.txt");
        FILL_WORDS_EN = initSet(PATH + "fillwordsEN.txt");

    }

    public static void main(String  [] a) {
        System.out.println(isFillWord("nur" , Language.DE));
    }

    static boolean isCurseWord(String word, Language l){
        switch (l){
            case DE:
                return CURSE_WORDS_DE.contains(word);
            case EN:
                return CURSE_WORDS_EN.contains(word);
            default:
                throw new RuntimeException("This can not happen!");
        }
    }

    static boolean isFillWord(String word, Language l){
        switch (l){
            case DE:
                return FILL_WORDS_DE.contains(word);
            case EN:
                return FILL_WORDS_EN.contains(word);
            default:
                throw new RuntimeException("This can not happen!");
        }
    }

    private static final Set<String> initSet(String fileName){

        Set<String> res = null;
        try(BufferedReader reader  = new BufferedReader(new FileReader(fileName))) {
            res = new HashSet<>(countLines(fileName));
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                res.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return res;
    }

    private static int countLines(String fileName) throws IOException {
        try
        (
                FileReader       input = new FileReader(fileName);
                LineNumberReader count = new LineNumberReader(input);
        )
        {
            while (count.skip(Long.MAX_VALUE) > 0) {}
            return count.getLineNumber() + 1;
        }

    }
}
