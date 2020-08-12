package elements.heros;

import elements.cards.*;
import system.configor.*;

import java.io.*;
import java.util.*;

public class DeckPair implements Configable {
    private ArrayList<String> friendly, enemy;
    private Deck friendlyDeck, enemyDeck;

    public DeckPair() {}

    public static DeckPair getInstance() {
        try {
            Configor<DeckPair> configor = new Configor<>("config", DeckPair.class);
            return configor.getConfigedObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void initialize(String initPlayerName) {
        ArrayList<Card> friendlyCards = new ArrayList<>();
        for (String s : friendly)
            friendlyCards.add(Card.getCard(s));
        friendlyDeck = new Deck(friendlyCards);

        ArrayList<Card> enemyCards = new ArrayList<>();
        for (String s : enemy)
            enemyCards.add(Card.getCard(s));
        enemyDeck = new Deck(enemyCards);
    }

    @Override
    public String getName() {
        return "deckPair";
    }

    @Override
    public String getJsonPath(String name, String initPlayerNamee) {
        return "decks/";
    }

    public Deck[] getDecks() {
        return new Deck[]{friendlyDeck, enemyDeck};
    }
}
