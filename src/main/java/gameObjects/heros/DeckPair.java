package gameObjects.heros;

import controllers.game.GameController;
import gameObjects.Configable;
import gameObjects.Configor;
import gameObjects.cards.Card;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class DeckPair implements Configable {
    private ArrayList<Card> friendly, enemy;
    private Deck friendlyDeck, enemyDeck;

    public DeckPair() {}

    public static DeckPair getInstance(GameController controller) {
        try {
            Configor<DeckPair> configor = new Configor<>(controller, "config", DeckPair.class);
            return configor.getConfigedObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void initialize(GameController controller) {
        friendlyDeck = new Deck(friendly);
        enemyDeck = new Deck(enemy);
        System.out.println(friendlyDeck + " " + friendlyDeck.getCards());
        System.out.println(enemyDeck + " " + enemyDeck.getCards());
    }

    @Override
    public String getJsonPath(GameController controller, String name) {
        return "decks/";
    }

    public Deck[] getDecks() {
        return new Deck[]{friendlyDeck, enemyDeck};
    }
}
