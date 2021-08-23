package sample.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import sample.model.GameFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static sample.Main.game;
 import static sample.model.GameFactory.*;

/**
 * MenuController
 * setup Menu View
 * display menu options
 * handle menu controls event such as size choice, difficulty choice, opponent choice, theme choice
 * handle exit, about button, highscore event
 */
public class MenuController extends BaseController implements Initializable {
    public Button aboutButton;
    public Button playButton;

    public ChoiceBox sizeChoiceBox;
    public ChoiceBox difficultyChoiceBox;
    public ChoiceBox opponentChoiceBox;
    public ChoiceBox themeChoiceBox;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*This function will be called a soon as the MenuView is loaded */

        // Init all the choicebox with the value that we want
        sizeChoiceBox.setItems(FXCollections.observableArrayList(
                size)
        );

        difficultyChoiceBox.setItems(FXCollections.observableArrayList(
                difficulty)
        );



        opponentChoiceBox.setItems(FXCollections.observableArrayList(
                "Solo","2 Players","Vs Computer"
                )
        );

        String[] themes;

        // get all default theme and user saved theme
        themes = getThemeAll();

        themeChoiceBox.setItems(FXCollections.observableArrayList(
                themes)
        );
    }

    private String[] getThemeAll() {
        /*Function that will go trough our path where our theme are
         Each file is a theme. */
        ArrayList<String> themes = new ArrayList<>(); // Init our ArrayList of themes
        URL path = getClass().getResource(GameController.base); // get the path
        File[] files = new File(path.getPath()).listFiles();  // create a File array
        for (File file : files != null ? files : new File[0]) { // iterate on it with a condition
            if (file.isDirectory()) { // if its a directory we add it because its a theme
                themes.add(file.getName());
            }
        }
        return themes.toArray(new String[0]);
    }

    public void playClicked(ActionEvent actionEvent) {

        // retrieve all settings from all settings menu controls
        // retrieve opponent choice
        int opponentIndex = opponentChoiceBox.getSelectionModel().getSelectedIndex();
        if (opponentIndex == -1) {
            opponentIndex = 0;
        }
        PlayerName opponent = PlayerName.values()[opponentIndex];
        // retrieve difficulty choice
        String difficulty = (String) difficultyChoiceBox.getSelectionModel().getSelectedItem();
        // retrieve size choice
        String sizeChoice = (String) sizeChoiceBox.getSelectionModel().getSelectedItem();
        // retrieve theme choice
        String themeChoice = (String) themeChoiceBox.getSelectionModel().getSelectedItem();
        // setup game factory
        GameFactory gameFactory = new GameFactory();
        // store all options in Option object
        GameFactory.Options options = new GameFactory.Options();
        options.setDifficulty(difficulty);
        options.setSize(sizeChoice);
        options.setTheme(themeChoice);
        options.setOpponent(opponent);
        // instantiate new Game
        game = gameFactory.getGame(options);
        // change view to game view
        Node source = (Node) actionEvent.getSource();
        Scene scene = source.getScene();
        changeView(scene, gameView);
    }

    public void onLoadPressed(ActionEvent actionEvent) {
        /*Function that will be called if the user want to add some theme */
        Node source = (Node) actionEvent.getSource();
        Scene scene = source.getScene();
        changeView(scene, themeView);
    }

    public void onAboutPressed(ActionEvent actionEvent) {
        showPopup(aboutView, "about");

    }

    public void onHighScorePressed(ActionEvent actionEvent) {
        showPopup(highScoreView, "highScore");
    }
}
