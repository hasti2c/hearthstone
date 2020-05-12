package directories.game;

import directories.*;
import gameObjects.Game;
import gameObjects.Player;
import gameObjects.cards.Card;

import java.util.ArrayList;

public class GameCards extends Directory {
    private ArrayList<Card> cards;

    GameCards(String name, ArrayList<Card> cards, Directory parent, Player player) {
        super(name, parent, player);
        this.cards = cards;
        config();
    }

    public void config() {
        clear();
        for (Card c : cards)
            addContent(c);
    }

    public Game getGame() {
        return parent.getGame();
    }
}
