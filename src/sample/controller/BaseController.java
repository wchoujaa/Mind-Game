package sample.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.model.Player;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Base controller handle
 */
public class BaseController {
    public static final String gameView = "/view/game.fxml";
    public static final String scoreBoardView = "/view/scoreBoard.fxml";
    public static final String menuView = "/view/menu.fxml";
    public static final String aboutView = "/view/about.fxml";
    public static final String themeView = "/view/theme.fxml";
    public static final String highScoreView = "/view/highScore.fxml";
    public static final String highScoreFile = "highScore.csv";




    public void changeView(Scene scene, String view) {
        /*Change the view */
        Node root = null;
        try {
            root = FXMLLoader.load(Class.class.getResource(view)); // load the new view
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (root != null) { // check if the root is null
            scene.setRoot((Parent) root);
        }
    }

    /**
     * display modal on top of screen
     *
     * @param viewPath view path from the constant defined in file header
     * @param title
     */
    public void showPopup(String viewPath, String title) {
        Parent view = null;
        // get the popup fxml file and convert it to Javafx node view
        try {
            view = FXMLLoader.load(BaseController.class.getResource(viewPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // setup popup stage
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        // setup popup stage layout
        VBox layout = new VBox(10);
        // add specific fxml view depending on the parameter
        layout.getChildren().add(view);
        Scene scene = new Scene(layout, 511, 551);
        window.setScene(scene);
        // show popup on screen
        window.showAndWait();
    }

    /**
     * Read high score from csv file
     * - read file
     * - parse each line
     * - save line in pairs of (player_name, player_score)
     *
     * @return high score in pair of (player_name, player_score)
     * @throws IOException
     */
    public ArrayList<Player> readHighScore() throws IOException {
        ArrayList<Player> scores = new ArrayList<>(); // create an arrayList of Pair<String , int>
        Player e = null;
        try {

            InputStream streamFile = BaseController.class.getClassLoader().getResourceAsStream(highScoreFile);

            ObjectInputStream in = new ObjectInputStream(streamFile );

            while ((e = (Player) in.readObject()) != null) {
                scores.add(e);
            }

            in.close();
            streamFile.close();
        } catch (EOFException f){
            System.out.println("Reach end of the file");
            return scores;
        } catch (IOException | ClassNotFoundException i) {
            System.out.println("Class not found");
            i.printStackTrace();
            return scores;
        }
//        // read the file
//        InputStream inputStream = BaseController.class.getClassLoader().getResourceAsStream(highScoreFile);
//        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
//        BufferedReader reader = new BufferedReader(streamReader);
//        // Read score from file line by line
//        for (String line; (line = reader.readLine()) != null; ) {
//            // split by coma
//            String[] splited = line.split(",");
//            scores.add(splited);
//        }
        return scores;
    }

    /**
     * save score in csv format
     * - format score
     * - write score to file
     *
     * @param winner     the player who won the game
     * @param difficulty the difficulty of the game
     * @param gameFormat the format of the game
     */
    public void saveScore(Player winner, String difficulty, String gameFormat) {
        // format score string to csv string
        String highScore = winner.getName() + "," + winner.getScore() + "," + difficulty + "," + gameFormat + "\n";

        try {
            URL url = BaseController.class.getClassLoader().getResource(highScoreFile);

           // ObjectInputStream in = new ObjectInputStream(out);
            FileOutputStream fileOut = new FileOutputStream(url.getPath(), true);
            ObjectOutputStream out = new ObjectOutputStream(fileOut );
            out.writeObject(winner);
            out.close();
            fileOut.close();
            System.out.println(String.format("Serialized data is saved in %s", highScoreFile));
        } catch (IOException i) {
            i.printStackTrace();
        } finally {

        }

/*        URL path = BaseController.class.getClassLoader().getResource(highScoreFile);
        // try write score to highScoreFile
        try {
            Files.write(Paths.get(path.toURI()), highScore.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }*/
    }
}
