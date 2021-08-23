package sample.model;



import static sample.controller.GameController.interrogation;

/**
 * Card
 * a card is represented by
 * - @int row @int column : coordinates in the grid
 * - @CardType type : type of card
 * - @String path : file path in resources folder
 * - @Boolean hidden : flag set at true if the card is flipped
 */
public class Card {

    private final String path;
    private boolean isHidden = true;
    private final CardType type;
    private int row;
    private int column;

    public Card(String path, CardType type) {
        this.path = path;
        this.type = type;
    }


    public CardType getType() {
        return type;
    }

    public void setHidden(boolean b) {
        this.isHidden = b;
    }

    public int getRow() {
        return row;
    }


    public int getColumn() {
        return column;
    }


    public String getPath() {
        /*Get the path of the card*/
        String imagePath;
        if (isHidden) { // if true -> meaning that the user still see and interrogation point
            imagePath = interrogation;
        } else {
            imagePath = path; // the card is clicked on
        }
        return imagePath;    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setCell(int i, int j) {
        column = i;
        row = j;
    }
}
