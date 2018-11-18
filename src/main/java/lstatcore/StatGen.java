package lstatcore;

import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;
import com.google.cloud.speech.v1p1beta1.WordInfo;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class StatGen {





    public static Stat getStats(String filePath, Language language, double origLength, double newLength ) throws IOException {

        String[] array = filePath.split("/");
        String name = array[array.length-1];



        if(Config.OFFLINE){
            //Already loaded in previosu runs!


            File[] files = new File("stats").listFiles();
            String jsonFile = null;
            for(File file : files){
                if(file.getName().matches(name + ".json")){
                    jsonFile = "stats/"+ name+".json";
                    break;
                }
            }
            if(jsonFile == null)
                throw new RuntimeException("json does not exisist!");

            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(jsonFile));
            Stat res= gson.fromJson(reader, Stat.class);
            System.out.println(res.getWords());
            return res;

        }else{
            Stat res = new Stat(language,origLength, newLength);
            SubTitleBuilder stb = new SubTitleBuilder("stats/" + name + ".srt" );
            List<SpeechRecognitionResult> srl = SpeechToText.asyncRecognizeGcs("gs://lstat/"+ name , language);
            for (SpeechRecognitionResult sr : srl) {
                SpeechRecognitionAlternative alternative = sr.getAlternativesList().get(0);
                for (WordInfo wi : sr.getAlternativesList().get(0).getWordsList()) {
                    res.addWord(wi);
                    stb.addWord(wi);
                }
            }

            res.setSubTitleFile( stb.close());
            res.save("stats/"+ name + ".json");
            return res;
        }



    }


}
