package sample.model;

import sample.controller.GameController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sample.model.GameFactory.PlayerName.PLAYER1;

/**
 * GameFactory
 * implement Factory Design pattern
 * setup a Game Object according to specified parameter:
 *  - @param difficulty;
 *  - @param opponent;
 *  - @param size;
 *  - @param theme;
 *  search for images in images resources folder
 */
public class GameFactory {

    public enum PlayerName {
        PLAYER1(),
        PLAYER2(),
        COMPUTER()
    }

    public static final String[] difficulty = new String[]{"easy", "medium", "hard"};
    public static final String[] size = new String[]{"small", "medium", "big"};
    public static final float[] bombProbability = new float[]{0.0f, 0.1f, 0.2f};
    public static final float[] unmatchedProbability = new float[]{0.0f, 0.1f, 0.2f};


    public static class Options {
        String difficulty;
        PlayerName opponent;
        String size;
        String theme;

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        public void setOpponent(PlayerName opponent) {
            this.opponent = opponent;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }
    }

    public Game getGame(Options options) {
        // set default size to 5 if no size
        String size = (options.size != null) ? options.size : "small";
        // set default difficulty to medium
        String difficulty = (options.difficulty != null) ? options.difficulty : "medium";
        // set default opponent to solo
        PlayerName opponent = (options.opponent != null) ? options.opponent : PLAYER1;
        // set default theme to flower
        String theme = (options.theme != null) ? options.theme : "flower";

        ArrayList<String> imageList = getThemeImages(theme);
        ArrayList<Player> players = getPlayers(opponent);
        int boardSize = getSize(size);
        float bombRatio = getRatio(difficulty, bombProbability);
        float unmatchedCardRatio = getRatio(difficulty, unmatchedProbability);
        // instantiate new Game with specified options
        if (difficulty.equals("easy")) {
            return new Game(boardSize, players, difficulty, size).generate(imageList, theme, bombRatio);
        } else {
            return new HardGame(boardSize, players, difficulty, size ).generate(imageList, theme, bombRatio, unmatchedCardRatio);
        }
    }

    private float getRatio(String difficulty, float[] probability) {
        float ratio = 0;
        // set bomb probability depending on difficulty, the harder, the greater the probability
        switch (difficulty) {
            case "hard":
                ratio = probability[2];
                break;
            case "medium":
                ratio = probability[1];
                break;
            case "easy":
                ratio = probability[0];
                break;
        }
        return ratio;
    }

    private int getSize(String size) {
        int boardSize = 0;
        switch (size) {
            case "small":
                boardSize = 3;
                break;
            case "medium":
                boardSize = 5;
                break;
            case "big":
                boardSize = 7;
                break;
        }
        return boardSize;
    }

    private ArrayList<Player> getPlayers(PlayerName opponent) {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player(PLAYER1.toString()));
        if (opponent != PLAYER1) {
            players.add(new Player(opponent.toString()));
        }
        return players;
    }

    private ArrayList<String> getThemeImages(String theme) {
        // get image folder
        String folder = GameController.base + "/" + theme;
        URL path = getClass().getResource(folder);
        File file = new File(path.getPath());
        // instantiate new folder path list
        ArrayList<String> imageList = new ArrayList();

        try (Stream<Path> walk = Files.walk(Paths.get(file.getAbsolutePath()))) {
            // search all image file in folder
            List<String> result = walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(".jpg") || f.endsWith(".png") || f.endsWith(".bmp")).collect(Collectors.toList());

            imageList.addAll(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageList;
    }
}
