package ui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Load main xml as UI
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("AcceleratorUI.fxml"));
        primaryStage.setTitle("LASY");
        primaryStage.setScene(new Scene(root, 640, 440));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }

}
