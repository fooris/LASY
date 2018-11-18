package lstatui;


import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lstatcore.*;

import java.io.File;
import java.io.IOException;


public class Controller extends Application {
    //Own progress interface

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Load main xml as UI
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("LSTAT.fxml"));
        primaryStage.setTitle("ACC");
        primaryStage.setScene(new Scene(root, 640, 400));
        primaryStage.show();




    }


    public static void main(String[] args) {
        launch(args);
    }

    File inFile = null;

    //FXML stuff
    @FXML
    private Button btnPlay;
    @FXML
    private Button btnStats;
    @FXML
    private Button btnLoad;


    private File file;

    private String videoName ;
    private String subTitleName;
    private Language language = Language.DE;
    private Skipper skipper;


    @FXML
    public void initialize() {
        btnLoad.setOnMouseClicked( e ->{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Lecture");
            file = fileChooser.showOpenDialog(new Stage());

            System.out.println(file);

            Stat stats = null;
            try {
                stats = StatGen.getStats( "/home/fooris/Documents/repos/LSTAT/audio/split0.wav", language,25,25);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            skipper = new Skipper(stats);

            videoName = file.getAbsolutePath() ;
            subTitleName = stats.getSubTitleFile();

            Runtime.getRuntime().addShutdownHook(new Thread( () ->{
                VLCPlayer.stop();
            }));

        });








        btnStats.setOnMouseClicked( e->{
            Stage stageFindStats = new Stage();
            StackPane stackPane = new StackPane();
            stageFindStats.setScene(new Scene(stackPane , 640, 400));
            Button exit = new Button("X");
            exit.setOnMouseClicked( exite ->{
                stageFindStats.close();
            });
            StackPane.setAlignment(exit, Pos.TOP_RIGHT);
            stackPane.getChildren().add(exit);
            stageFindStats.show();

        });

        btnPlay.setOnMouseClicked( e -> {

            try {
                VLCPlayer.start(videoName , subTitleName);

                Stage findStage = new Stage();


                StackPane stackPane = new StackPane();
                findStage.setScene(new Scene(stackPane, 200, 27));




                final TextField t = new TextField();
                t.setPromptText("Search");

                Button exit = new Button("X");
                exit.setOnMouseClicked( exite ->{
                    VLCPlayer.stop();
                    System.exit(0); // Gude Nacht!
                });

                Button next = new Button("\u25B6");
                Button back = new Button("\u25C0");
                next.setOnMouseClicked( na ->{
                    if(skipper.hasNext(t.getText())){
                        try {
                            long sec = Math.max(skipper.next(t.getText()) - 1 , 0);
                            VLCPlayer.jumpTo(videoName,subTitleName, sec);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });

                back.setOnMouseClicked( na ->{
                    if(skipper.hasPrevious(t.getText())){
                        try {
                            VLCPlayer.jumpTo(videoName,subTitleName, skipper.previous(t.getText()));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });



                stackPane.getChildren().addAll(t  ,back, next);
                StackPane.setAlignment(exit, Pos.CENTER_RIGHT);
                StackPane.setAlignment(next, Pos.CENTER_RIGHT);
                StackPane.setAlignment(back, Pos.CENTER_RIGHT);
                StackPane.setMargin(next , new Insets(0,0,0,0));
                StackPane.setMargin(back , new Insets(0,27,0,0));
                StackPane.setAlignment(t, Pos.CENTER_LEFT);

                findStage.setY(0);
                findStage.setX(java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 200);
                findStage.setTitle("Search");
                findStage.setAlwaysOnTop(true);
                findStage.show();

                Stage stage = (Stage) btnPlay.getScene().getWindow();
                stage.setAlwaysOnTop(true);
                stage.close();

            } catch (IOException e1) {
               e1.printStackTrace();
            }


        });





    }


}
