package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.util.Pair;
import sample.model.Player;
import sun.misc.ClassLoaderUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ResourceBundle;


/**
 * HighScoreController
 * setup highScore view
 * read score from file and display them
 */
public class HighScoreController extends BaseController implements Initializable {

    public ListView scoreListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // get score from file
        ArrayList<Player> scores = new ArrayList<>();
        ArrayList<String> scoreFormatted = new ArrayList<>();
        try { // try catch because we may have an error
            scores = readHighScore();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // format the score
        for (Player p : scores) { // loop the pair containing the name + score
            String line = String.format("%s %s", p.getName(), p.getScore());


            scoreFormatted.add(line);
        }

        ObservableList<String> items = FXCollections.observableArrayList(scoreFormatted);
        // add all score in UI
        scoreListView.setItems(items);
    }
}
