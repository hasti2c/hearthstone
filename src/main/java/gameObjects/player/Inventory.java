package gameObjects.player;

import com.google.gson.stream.*;
import controllers.game.*;
import gameObjects.*;
import gameObjects.cards.*;
import gameObjects.heros.*;
import java.io.*;
import java.util.*;

public class Inventory implements Configable {
    private int deckCap;
    private Deck currentDeck;
    private ArrayList<Deck> allDecks = new ArrayList<>();
    private ArrayList<Hero> allHeros = new ArrayList<>();
    private final ArrayList<Card> allCards = new ArrayList<>();

    public Inventory() {
    }

    @Override
    public void initialize(GameController controller) {}

    @Override
    public String getJsonPath(GameController controller, String name) {
        return "players/";
    }

    public static Inventory copyDefault(Player defaultPlayer) {
        Inventory defaultInventory = defaultPlayer.getInventory();
        Inventory inventory = new Inventory();
        inventory.deckCap = defaultInventory.getDeckCap();

        inventory.allHeros = new ArrayList<>(defaultInventory.allHeros);
        for (Card c : inventory.allCards)
            inventory.addCard(c);

        inventory.allDecks = new ArrayList<>();
        for (Deck deck : defaultInventory.allDecks)
            inventory.addDeck(deck.clone(inventory));
        for (Deck deck : inventory.allDecks)
            if (deck.toString().equals(defaultInventory.currentDeck.toString()))
                inventory.setCurrentDeck(deck);
        return defaultInventory;
    }

    public Inventory clone() {
        Inventory inventory = new Inventory();
        inventory.deckCap = deckCap;

        for (Card card : allCards)
            inventory.allCards.add(card.clone());
        for (Hero hero : allHeros)
            inventory.allHeros.add(hero.clone());
        for (Deck deck : allDecks) {
            Deck deckClone = deck.clone(inventory);
            inventory.allDecks.add(deckClone);
            if (deck == currentDeck)
                inventory.currentDeck = deckClone;
        }
        return inventory;
    }

    public int getDeckCap() {
        return this.deckCap;
    }

    public void addDeck(Deck deck) {
        allDecks.add(deck);
    }

    public boolean removeDeck(Deck deck) {
        allDecks.remove(deck);
        if (currentDeck == deck)
            currentDeck = null;
        return true;
    }

    public ArrayList<Deck> getAllDecks() {
        return allDecks;
    }

    void setCurrentDeck(Deck currentDeck) {
        this.currentDeck = currentDeck;
    }

    void deselectCurrentDeck() {
        currentDeck = null;
    }

    public Deck getCurrentDeck() {
        return currentDeck;
    }

    public ArrayList<Hero> getAllHeros() {
        return this.allHeros;
    }

    public Hero getCurrentHero() {
        if (currentDeck == null)
            return null;
        return currentDeck.getHero(this);
    }

    public ArrayList<Card> getAllCards() {
        return this.allCards;
    }

    public void addCard(Card card) {
        allCards.add(card);
    }

    public void removeCard(Card card) {
        for (int i = 0; i < allCards.size(); i++)
            if (allCards.get(i) == card)
                allCards.remove(i--);
    }

    public void updateJson(String playerName) {
        try {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter("src/main/resources/database/players/" + playerName + "-inventory.json"));
            jsonWriter.setIndent("  ");
            updateJson(jsonWriter, playerName);
            jsonWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateJson(JsonWriter jsonWriter, String playerName) {
        try {
            jsonWriter.beginObject();


            jsonWriter.name("deckCap").value(deckCap);

            jsonWriter.name("allHeros");
            jsonWriter.beginArray();
            for (Hero h : allHeros)
                jsonWriter.value(h.toString());
            jsonWriter.endArray();

            jsonWriter.name("allCards");
            jsonWriter.beginArray();
            for (Card c : allCards)
                jsonWriter.value(c.toString());
            jsonWriter.endArray();

            jsonWriter.name("allDecks");
            jsonWriter.beginArray();
            for (Deck deck : allDecks) {
                jsonWriter.value(deck.toString());
                deck.updateJson(playerName);
            }
            jsonWriter.endArray();

            if (currentDeck != null)
                jsonWriter.name("currentDeck").value(currentDeck.toString());

            jsonWriter.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Card getCard(String cardName) {
        for (Card card : allCards)
            if (card.toString().equals(cardName))
                return card;
        for (Card card : GameController.getCardsList())
            if (card.toString().equals(cardName)) {
                Card cardClone = card.clone();
                allCards.add(cardClone);
                return cardClone;
            }
        return null;
    }
}