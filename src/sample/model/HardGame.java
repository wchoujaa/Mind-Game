package sample.model;

import java.util.ArrayList;

/**
 * HardGame
 * derive from Game
 * handle generation of hard game
 */
public class HardGame extends Game {

    public HardGame(int size, ArrayList<Player> players, String difficulty, String gameFormat) {
        super(size, players, difficulty, gameFormat);
    }

    public Game generate(ArrayList<String> imageList, String theme, float bombRatio, float unmatchedCardRatio) {
        ArrayList<Card> cards = new ArrayList<>();
        int totalCount = size * size;
        int bombCount = (int) Math.floor(totalCount * bombRatio);
        int regularTotalCount = totalCount - bombCount;
        int unmatchedCount = (int) Math.floor(regularTotalCount * unmatchedCardRatio);
        int regularCount = regularTotalCount - unmatchedCount;
        int speicalCount = 0;
        // if regular count is odd add a joker
        if (regularCount % 2 == 1) {
            regularCount -= 1;
            speicalCount = 1;
        }

        generate(CardType.BOMB, bombCount, imageList, theme, cards);

        for (int i = 0; i < unmatchedCount; i++) {
            generate(CardType.REGULAR, 1, imageList, theme, cards);
            newImage = false;
        }
        generate(CardType.REGULAR, regularCount, imageList, theme, cards);
        generate(CardType.SPECIAL, speicalCount, imageList, theme, cards);

        generateBoard(cards);
        return this;
    }
}
