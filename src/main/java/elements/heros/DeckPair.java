package elements.heros;

import controllers.game.*;
import elements.cards.*;
import system.Configable;
import system.Configor;

import java.io.*;
import java.util.*;

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
    }

    @Override
    public String getJsonPath(GameController controller, String name) {
        return "decks/";
    }

    public Deck[] getDecks() {
        return new Deck[]{friendlyDeck, enemyDeck};
    }
}
