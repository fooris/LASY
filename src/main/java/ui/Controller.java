package ui;

import core.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;

public class Controller {
    LectureMaker lm;

    //Own progress interface
    IUpdateProgress updater = new IUpdateProgress() {
        @Override
        public void updateProgress(double progress) {
            progressBar.setProgress(progress);
        }

        @Override
        public void updateState(String state) {
            lblState.setText(state);
        }

        @Override
        public void done() {

        }
    };


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
    private Slider sldRelativeSilenceThreshold;
    @FXML
    private Slider sldMinGapLength;
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

//        sldAccelerationRate.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("Acceleration changed");
//            updateStats();
//        });
//
//        sldMinGapLength.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("MinGapLength changed");
//            updateStats();
//        });
//
//        sldRelativeSilenceThreshold.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("Relative silence threshold changed");
//            updateStats();
//        });

    }




    @FXML
    private void playerPreview(ActionEvent event) {
        copyConfigValues();

        Media media = null;
        try {
            media = new Media("file://" + lm.genPreview());
        } catch (IOException e) {
            showAltert("Exception", e.getMessage());
            e.printStackTrace();
        }
        updateStats();
        MediaPlayer player = new MediaPlayer( media );

        videoView.setMediaPlayer(player);
    }

    @FXML
    private void playerPlay(ActionEvent event) {
        //This assigns a play/stop functionality to the play button
//        if (mp==null) {return;}
//        if (isPlaying) {
//            mp.pause();
//        } else {
//            mp.play();
//        }
//        isPlaying = !isPlaying;

        //Instead of pausing, just replay the file:
        MediaPlayer mp = videoView.getMediaPlayer();
        if(mp==null) {
            showAltert("Message", "Please select a input file first");
            return;
        }

        //Restart video
        mp.seek(new Duration(0));
        mp.play();

    }

    private void copyConfigValues() {
        if (lm == null) {
            showAltert("Message", "Please select a input file first");
            return;
        }
        lm.setInvert(chkFunMode.isSelected());
        lm.setMinCutLength(sldMinGapLength.getValue());
        lm.setThreshold(sldRelativeSilenceThreshold.getValue()*0.01);
        lm.setSpeedUpFactor((float) sldAccelerationRate.getValue());
        lm.setReencodeVideo(chkReencode.isSelected());
    }

    //Call on refresh button
    @FXML
    private void refreshButton(ActionEvent event) {
        if (lm!=null) {
            try {
                copyConfigValues();
                lm.applyParams();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(lm.getVideoLength()+" - "+lm.getFinalVideoLength());
            lblOriginalLength.setText(secondsToString((int) lm.getVideoLength()));
            lblAcceleratedLength.setText(secondsToString((int) lm.getFinalVideoLength()));
        }
    }

    private void updateStats() {
        if (lm!=null) {
            lblOriginalLength.setText(secondsToString((int) lm.getVideoLength()));
            lblAcceleratedLength.setText(secondsToString((int) lm.getFinalVideoLength()));
        }
    }

    @FXML
    private void handleVideoOpen(ActionEvent event) {
//        try {
            //Open file
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open File");
            File file = chooser.showOpenDialog(new Stage());
            if (file==null) {
                showAltert("Message", "Please select a input file first");
                return;}
            lm = new LectureMaker(file.getAbsolutePath());
            updateStats();


            //Store into label
            lblInfile.setText(file.getAbsolutePath());

    }

    @FXML
    private void handleGenerate(ActionEvent event) {
        try {
            lm.genFinal();
        } catch (IOException e) {
            showAltert("Exception", e.getMessage());
            e.printStackTrace();
        }
    }

    //Helper
    private static String secondsToString(int seconds) {
        LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);
        return timeOfDay.toString();
    }

    private static void showAltert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
    }

}
