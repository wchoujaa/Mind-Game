package sample.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import sample.Main;
import sample.model.Card;
import sample.model.Game;
import sample.model.GameState;
import sample.model.Player;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static sample.Main.game;

/**
 * GameController
 * controller of the Game view
 * handle card click event
 * setup game view
 * handle exit, about button, highscore event
 */
public class GameController extends BaseController implements Initializable   {

    public AnchorPane imageContainer;

    // static final because we will need these variable in other class + their value does not change
    public static final String base = "/images";
    public static final String interrogation = "/images/interogation.PNG";
    public static final String bomb = "/images/bomb.PNG";
    public static final String special = "/images/joker.PNG";

    public Label currentPlayerText;
    public Label player1Text;
    public Label player2Text;
    public Label player1LabelScore;
    public Label player2LabelScore;

    private GridPane gridPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        gridPane = new GridPane();
        // set spacing between cells
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        // inject dependency
        game.setController(this);
        // get players list from game object
        ArrayList<Player> players = game.getPlayer();
        player1Text.setText(players.get(0).getName());
        // setup the view ; adding player name to corresponding player screen label
        if (players.size() > 1) {
            player2Text.setText(players.get(1).getName());
        } else {
            player2Text.setVisible(false);
            player2LabelScore.setVisible(false);
        }

        int size = game.size();
        double containerWidth = imageContainer.getPrefWidth();
        double imageWidth = containerWidth / size;
        // setup the view ; adding imageView to gridpane
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // retrieve Game card from game grid
                Card card = game.getCard(i, j);
                // Instanciate new Imageview
                ImageView imageView = new ImageView();
                imageView.setOnMouseClicked(event -> cardClick(card, event));
                imageView.setImage(new Image(card.getPath()));
                imageView.setFitHeight(imageWidth);
                imageView.setFitWidth(imageWidth);
                gridPane.add(imageView, i, j, 1, 1);
            }
        }
        // add the gridpane to the container
        imageContainer.getChildren().add(gridPane);
        Player currentPlayer = game.getCurrentPlayer();
        currentPlayerText.setText(currentPlayer.getName());
    }

    private void cardClick(Card card, MouseEvent event) {
        // the card is clicked
        game.onGameStep(card);
        updateUI();
    }

    public void updateUI() {
        /*Update the UI by changing the name of the player that has to play
         + their score */
        currentPlayerText.setText(game.getCurrentPlayer().getName());
        ArrayList<Player> players = game.getPlayer();
        player1LabelScore.setText(Integer.toString(players.get(0).getScore()));
        if (players.size() > 1) { // meaning we have more than 1 player
            player2LabelScore.setText(Integer.toString(players.get(1).getScore()));
        }
    }

    public void changeImage(Card card) {
        // change the image when its clicked we need to show it
        ImageView imageView = (ImageView) getNodeByRowColumnIndex(card);
        imageView.setImage(new Image(card.getPath()));
    }


    private Node getNodeByRowColumnIndex(Card card) {
        /*Function that will allow us to get the node given a card as parameter.
        This function will help us for the ChangeImage Function.*/
        int row = card.getRow();
        int column = card.getColumn();
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }


    public void onExitPressed(ActionEvent actionEvent) {
        changeView(getScene(), menuView); // we change the view when the button exit is pressed
    }

    public Scene getScene() {
        return imageContainer.getScene(); // simple getter for the scene
    }

    public void onAboutPressed(ActionEvent actionEvent) {
        showPopup(aboutView, "about"); // we change the view when the button about is pressed
    }

    public void onHighScorePressed(ActionEvent actionEvent) {
        showPopup(highScoreView, "highScore"); // we change the view when the button highScore is pressed
    }
}
