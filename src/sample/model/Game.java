package sample.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import sample.controller.GameController;

import java.io.File;
import java.util.*;

import static sample.controller.GameController.*;
import static sample.model.GameFactory.PlayerName.COMPUTER;

/**
 * Game
 * generate a board of randomly placed cards
 * contain game logic
 * change player turn
 */
public class Game {

    protected final int size;
    private final Map<String, Card> cards; // dictionary of all card
    private final ArrayList<Player> players; // list of all players
    private int playerIndex; // index to keep track of current player
    private Player currentPlayer; // current player
    private Card previous;  // Card reference to store in memory last card played
    private Card current; // Card reference of current card played
    private GameController controller; // controller of the game view
    protected boolean newImage = false; // flag used to generate card pair
    private String absolutePath = "";
    private GameState gameState = GameState.PLAYING; // game state
    private String difficulty;
    private String gameFormat;



    public Game(int size, ArrayList<Player> players, String difficulty, String gameFormat) {
        this.cards = new HashMap<>();
        this.players = players;
        this.playerIndex = 0;
        this.currentPlayer = players.get(playerIndex);
        this.size = size;
        this.difficulty = difficulty;
        this.gameFormat = gameFormat;
     }

    public Game generate(ArrayList<String> imageList, String theme, float bombRatio) {

        ArrayList<Card> cards = new ArrayList<>(); // ArrayList containing the cards
        int totalCount = size * size; // the size of the board
        int bombCount = (int) Math.floor(totalCount * bombRatio); // calcul the number of bomb
        int regularCount = totalCount - bombCount;
        int specialCount = 0;
        // if regular count is odd add a joker
        if (regularCount % 2 == 1) {
            regularCount -= 1;
            specialCount = 1;
        }

        generate(CardType.BOMB, bombCount, imageList, theme, cards);
        generate(CardType.REGULAR, regularCount, imageList, theme, cards);
        generate(CardType.SPECIAL, specialCount, imageList, theme, cards);

        generateBoard(cards);
        return this; // return the GAME
    }

    /**
     * Method that generate a random board of cards
     *
     * @param cardsList
     */
    protected void generateBoard(ArrayList<Card> cardsList) {
        int i;
        int j;
        Random random = new Random();
        // while there a cards to be generated
        while (cardsList.size() != 0) {
            // generate a new cell
            i = random.nextInt(size);
            j = random.nextInt(size);
            String key = i + "" + j;
            // check if not exist
            if (cards.containsKey(key)) continue;
            // get first card
            Card card = cardsList.remove(0);
            // assign cell coordinate
            card.setCell(i, j);
            cards.put(key, card);
        }
    }



    protected void generate(CardType type, int count, ArrayList<String> imageList, String theme, ArrayList<Card> cards) {
        /*Generate the right number of card
         The type will be : BOMB, REGULAR,SPECIAL */
        for (int i = 0; i < count; i++) {
            String path = getPathFor(type, imageList, theme);
            cards.add(new Card(path, type));
        }
    }


    protected String getPathFor(CardType type, ArrayList<String> imageList, String theme) {
        String imagePath;

        switch (type) {
            case BOMB:
                imagePath = bomb;
                break;
            case SPECIAL:
                imagePath = special;
                break;
            case REGULAR:
            default:
                // defaut get random picture from theme
                if (!newImage) {
                    Random random = new Random();
                    int shuffle = random.nextInt(imageList.size());
                    absolutePath = imageList.get(shuffle);
                    newImage = true;
                } else {
                    newImage = false;
                }
                imagePath = base + "/" + theme + "/" + new File(absolutePath).getName();
                break;
        }
        return imagePath;
    }

    /**
     * Method called on each Game turn usually after a Player has clicked on a card
     *
     * @param card the card chosen by the player
     */
    public void onGameStep(Card card) {
        // step the game logic to next step
        if (card.isHidden() && gameState != GameState.FREEZE) {
            showCard(card);
            controller.changeImage(card);
            checkGame(card);
        }
        // if the game is finisehd
        if (gameState == GameState.PLAYING && isEndGame()) {
            Timeline task = new Timeline(new KeyFrame(javafx.util.Duration.millis(501), (event2) -> {
                controller.changeView(controller.getScene(), scoreBoardView);
            }));
            task.setCycleCount(1);
            task.play();
        }
        // if it is computer turn restart the loop
        if (currentPlayer.getName().equals(COMPUTER.toString()) && gameState != GameState.FREEZE && !isEndGame()) {
            computerTurn();
        }
    }

    /**
     * check wether or not the game must be ended
     *
     * @return
     */
    private boolean isEndGame() {
        // check if all regular cards are visible
        // if one is hidden return false
        boolean allVisible = true;
        Object[] cardsArray = cards.values().toArray();
        for (Object c : cardsArray) {
            Card card = (Card) c;
            if (card.isHidden() && ((Card) c).getType() == CardType.REGULAR) {
                allVisible = false;
                break;
            }
        }
        return allVisible;
    }

    /**
     * Check the game condition depending on the new card played
     * - handle all the case scenario depending on the card type
     * - update the UI
     *
     * @param card
     */
    private void checkGame(Card card) {
        current = card;

        if (previous == null) {
            // 1st case : 1 card return
            fistCard();
        } else if (previous.getPath().equals(current.getPath())) {
            // 2nd case : match
            match();
        } else {
            // 3rd case : mismatch
            // set timeout that set the card to hidden after delay
            mismatch();
        }

        controller.updateUI();

    }

    /**
     * when the card chosen by the player is different than the first card
     */
    private void mismatch() {

        switch (current.getType()) {
            case BOMB:  // we have a mismatch but its a BOMB CARD
                bombCase(); // call the function bombCase()

                previous.setHidden(true); // we show the image

                controller.changeImage(previous);
                previous = null;
                break;
            case SPECIAL:  // we clicked on a special card
                specialCase(); // call the function

                break;
            default:  // meaning its a mistake from us so we need to show both card and freeze the game
                gameState = GameState.FREEZE;  // we change the state of the game to FREEZE

                // Timeline will allow us to freeze the game for half a second
                // its in fact an animation sequence consisting of keyframe
                Timeline task = new Timeline(new KeyFrame(javafx.util.Duration.millis(501), (event2) -> {
                    gameState = GameState.FREEZE;
                    current.setHidden(true);
                    previous.setHidden(true);
                    nextPlayer();
                    controller.changeImage(current);
                    controller.changeImage(previous);
                    previous = null;
                    gameState = GameState.PLAYING;
                }));
                task.setCycleCount(1); // we set the cycle count to 1 -> meaning one animation

                task.play();
                break;
        }
    }

    /**
     * when the card chosen by the player is the same as the first card
     */
    private void match() {
        previous = null;
        getCurrentPlayer().addScore(1);
    }

    /**
     * when the card is the first card chosen by the player
     */
    private void fistCard() {
        switch (current.getType()) {
            case BOMB:
                bombCase();
                break;
            case SPECIAL:
                specialCase();
                break;
            default:
                previous = current;
                break;
        }
    }

    /**
     * when the card chosen is a bomb
     */
    private void bombCase() {
        currentPlayer.setScore(currentPlayer.getScore() - 1); // the player loose a point
        gameState = GameState.FREEZE; // we change the state to FREEZE
        // we use an animation timeline that will give the impression of a FREEZE
        Timeline task = new Timeline(new KeyFrame(javafx.util.Duration.millis(501), (event2) -> {
            gameState = GameState.FREEZE;
            current.setHidden(true);
            controller.changeImage(current);
            gameState = GameState.PLAYING;
            nextPlayer();
        }));
        task.setCycleCount(1);
        task.play();
    }

    /**
     * when the card chosen is a special card
     */
    private void specialCase() {
        cards.forEach((s, card) -> { // its an iteration like a for loop
            boolean value = card.isHidden();
            card.setHidden(false); // put it to false --> show all the card
            controller.changeImage(card);
            gameState = GameState.FREEZE;
            // we do another animation (freeze)
            Timeline task = new Timeline(new KeyFrame(javafx.util.Duration.millis(1501), (event2) -> {
                gameState = GameState.FREEZE;
                card.setHidden(value);
                controller.changeImage(card);
                gameState = GameState.PLAYING;
            }));
            task.setCycleCount(1);
            task.play();
        });

        // timer used for animation
        Timeline task1 = new Timeline(new KeyFrame(javafx.util.Duration.millis(1701), (event2) -> {
            if (currentPlayer.getName().equals(COMPUTER.toString())) {
                computerTurn();
            }
        }));
        task1.setCycleCount(1);
        task1.play();
    }

    private void showCard(Card card) {
        card.setHidden(false);
    }

    /**
     * switch player turn
     */
    private void nextPlayer() {
        playerIndex = (playerIndex + 1) % players.size();
        currentPlayer = players.get(playerIndex);
        controller.updateUI();

        if (currentPlayer.getName().equals(COMPUTER.toString())) {
            computerTurn();
        }
    }

    /**
     * when the next player is computer
     * - choose two random card
     * - check if they are hidden
     * - step game forward
     */
    private void computerTurn() {
        // freeze the game
        gameState = GameState.FREEZE;

        Timeline task = new Timeline(new KeyFrame(javafx.util.Duration.millis(701), (event2) -> {
            int i;
            int j;
            Random random = new Random();
            boolean found = false;
            Card card = getCard(0, 0); // the card that will be choosen by the computer
            // search for a hidden card
            while (!found) {
                // generate a card
                i = random.nextInt(size);
                j = random.nextInt(size);
                card = getCard(i, j);
                if (card.isHidden()) {
                    found = true;
                }
            }
            gameState = GameState.PLAYING;
            onGameStep(card);


        }));
        task.setCycleCount(1);
        task.play();
    }


    public void setController(GameController controller) {
        this.controller = controller;
    }

    public ArrayList<Player> getPlayer() {
        return players;
    }

    public Player getWinner() {
        /*Compare the score between the player */
        Player player = players.get(0);
        if (players.size() > 1) {
            Player player2 = players.get(1);
            if (player2.getScore() > player.getScore()) {
                player = player2;
            }
        }
        return player;
    }


    public int size() {
        return size;
    }

    public Card getCard(int i, int j) {
        String key = i + "" + j;
        return cards.get(key);
    }

    public Player getCurrentPlayer() {
        return players.get(playerIndex);
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getGameFormat() {
        return gameFormat;
    }

}