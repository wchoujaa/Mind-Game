package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import sample.model.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

import static sample.Main.game;

/**
 * ScoreBoardController
 * setup score board view
 * check if player is winner display winner
 * check if player is all time highScore display Congratulation view
 */
public class ScoreBoardController extends BaseController implements Initializable {

    public Label playerWinScore;
    public Label playerWinText;
    public VBox highScorePrompt;
    public TextField scoreBoardNameInput;
    public Button saveHighScoreButton;
    private Player winner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        highScorePrompt.setVisible(false);
        winner = game.getWinner();
        // set the winner name and score to the screen
        playerWinScore.setText(Integer.toString(winner.getScore()));
        playerWinText.setText(winner.getName());

        // check if the player has best score to show congratulation view
        boolean isBestScore = false;

        ArrayList<Player> scores = null;
        // fetch high score from file score board
        try {
            scores = readHighScore();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // if player is best score set flag to true
        if (scores != null) {
            isBestScore = hasBestScore(winner, scores);
        }

        if (isBestScore) {
            showHighScore();
        }
    }

    private void showHighScore() {
        highScorePrompt.setVisible(true);
    }

    private boolean hasBestScore(Player player, ArrayList<Player> scores) {
        /*Function that will return if there is a new highScore or not */
        int playerScore = player.getScore(); // we get the score of the player
        boolean isBestScore = true; // init the boolean to true
        for (Player p : scores) { // go through our highScore
            if (p.getScore() > playerScore) { // if there is a value in the highScore that is bigger that what we
                isBestScore = false;              // have , it means the player did not beat the highScore
                break; // we stop iterating because we dont need to
            }
        }
        return isBestScore;
    }

    public void onExitPressed(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Scene scene = source.getScene();
        changeView(scene, menuView);
    }

    public void onSavePressed(ActionEvent actionEvent) {
        // save the winner to the highScore score board
        winner.setName(scoreBoardNameInput.getText());
        saveScore(winner, game.getDifficulty(), game.getGameFormat());
        // disable save score button
        saveHighScoreButton.setDisable(true);
        // show high score on screen
        showPopup(highScoreView, "highScore");
    }


}
