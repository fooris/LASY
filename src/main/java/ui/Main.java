package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Load main xml as UI
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("AcceleratorUI.fxml"));
        primaryStage.setTitle("ACC");
        primaryStage.setScene(new Scene(root, 640, 400));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }

}
