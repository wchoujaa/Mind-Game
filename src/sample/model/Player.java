package sample.model;

/**
 * Player
 * encapsulate player data:
 * - @int score : the current score during the game
 * - @String name : the name of the player
 */
public class Player  implements java.io.Serializable {
    private int score = 0;
    private String name;

    public Player(String name) {
        this.name = name;
    }

    public void addScore(int i) {
        score += i;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int i) {
        this.score = i;
    }

    public void setName(String name) {
        this.name = name;
    }
}
