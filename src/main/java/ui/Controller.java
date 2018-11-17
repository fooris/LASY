package ui;

import core.AudioIO;
import core.CutStatistics;
import core.FFMPEG;
import core.SilenceDetector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;

public class Controller {
    //Own progress interface
    IUpdateProgress updater = new IUpdateProgress() {
        @Override
        public void updateProgress(double progress) {
            progressBar.setProgress(progress);
        }
    };

    //Silence detector
    SilenceDetector silenceDetector = null;
    File inFile = null;
    boolean isPlaying = false;

   //FXML stuff
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label lblState;
    @FXML
    private Label lblOriginalLength;
    @FXML
    private Label lblAcceleratedLength;
    @FXML
    private Label lblInfile;
    @FXML
    private Label lblTest;
    @FXML
    private Slider sldRelativeSilenceThreshold;
    @FXML
    private Slider sldMaxGapLength;
    @FXML
    private Slider sldAccelerationRate;
    @FXML
    private CheckBox chkReencode;
    @FXML
    private CheckBox chkFunMode;
    @FXML
    private Pane mediaPane;
    @FXML
    private MediaView videoView;

    public void initialize() {
        chkReencode.selectedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(sldAccelerationRate.getValue());
            if (chkReencode.isSelected()==false) {
                sldAccelerationRate.setValue(1.0);
                sldAccelerationRate.setDisable(true);
            } else {
                sldAccelerationRate.setDisable(false);
            }
        });
    }

    @FXML
    private void onAccelerationSliderChanged(ActionEvent event) {

    }

    @FXML
    private void playerPreview(ActionEvent event) {
        Media media = new Media("file:///home/administrator/SideProjects/test2.mp4");
        MediaPlayer player = new MediaPlayer( media );

        videoView.setMediaPlayer(player);
    }

    @FXML
    private void playerPlay(ActionEvent event) {
        MediaPlayer mp = videoView.getMediaPlayer();
        if (mp==null) {return;}
        if (isPlaying) {
            mp.pause();
        } else {
            mp.play();
        }
        isPlaying = !isPlaying;
    }


    @FXML
    private void handleVideoOpen(ActionEvent event) { //TODO error popups
//        try {
//            //Open file
//            FileChooser chooser = new FileChooser();
//            chooser.setTitle("Open File");
//            File file = chooser.showOpenDialog(new Stage());
//            if (file==null) {return;}
//
//            System.out.println(file);
//
//            //Store into label
//            lblInfile.setText(file.getAbsolutePath());
//
//            //Get options
//            double gapLength = sldMaxGapLength.getValue();
//            double relativeSilence = sldRelativeSilenceThreshold.getValue();
//            System.out.println("GapLength: "+gapLength+" relativeSilence: "+relativeSilence*0.01);
//
//            //Feed into core
//            String audioPath = null;
//            try {
//                audioPath = FFMPEG.convertToAudioAndGetPath(file.getAbsolutePath());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            double[] samples = AudioIO.load(audioPath);
//            SilenceDetector sl = new SilenceDetector(samples, relativeSilence*0.01, gapLength);
//            sl.detectSilence();
//
//            //Calculate lengths
//            int lengthOriginal = (int)samples.length/AudioIO.SAMPLE_RATE;
//            int lengthSecondsCut = (int)CutStatistics.getSecondsCut(sl.getCutSequence());
//            int lengthAccelerated = lengthOriginal - lengthSecondsCut;
//            lblOriginalLength.setText(secondsToString(lengthOriginal));
//            lblAcceleratedLength.setText(secondsToString(lengthAccelerated));
//
//            //If all successful, save sl
//            silenceDetector = sl;
//            inFile = file;
//
//        } catch (UnsupportedAudioFileException e) {
//            e.printStackTrace();
//        }
    }

    @FXML
    private void handleGenerate(ActionEvent event) {
        System.out.println("Generate");
        if (silenceDetector==null) {
            return; //TODO Error message
        }
        if (inFile==null) {
            return; //TODO Error message
        }

        //Open file:
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save File");
        File file = chooser.showSaveDialog(new Stage());
        if (file==null) {return;}

        System.out.println(file);

        //Feed to core
        //try {
            //TODO @Basti: Use IUpdateProgress update, which can be used to update progress
            boolean reEncode = true;
            float speedUpValue = 0.0f;
            //FFMPEG.cut(inFile.getAbsolutePath(), file.getAbsolutePath(), silenceDetector.getCutSequence(), reEncode, speedUpValue);
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

    }

    //Helper
    private static String secondsToString(int seconds) {
        LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);
        return timeOfDay.toString();
    }

}
