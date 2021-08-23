package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controller.BaseController;
import sample.model.Game;

public class Main extends Application {

    public static Game game;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource(BaseController.menuView));
        primaryStage.setTitle("Mind Game");
        primaryStage.setScene(new Scene(root, 721 , 721));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
