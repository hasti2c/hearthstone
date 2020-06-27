package gameObjects.Player;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import controllers.game.GameController;
import directories.game.PlayGround;
import gameObjects.Configable;
import gameObjects.cards.Card;
import gameObjects.heros.Deck;
import gameObjects.heros.Hero;
import gameObjects.heros.HeroClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Inventory implements Configable {
    private int deckCap;
    private Deck currentDeck;
    private ArrayList<Deck> allDecks = new ArrayList<>();
    private ArrayList<Hero> allHeros = new ArrayList<>();
    private ArrayList<Card> allCards = new ArrayList<>();

    public Inventory() {
    }

    @Override
    public void initialize(GameController controller) {}

    @Override
    public String getJsonPath(GameController controller, String name) {
        return "players/";
    }

    public void copyDefault(Player defaultPlayer) {
        Inventory currentInventory = defaultPlayer.getInventory();
        deckCap = currentInventory.getDeckCap();

        allHeros = new ArrayList<>(currentInventory.allHeros);
        for (Card c : allCards)
            addCard(c);

        allDecks = new ArrayList<>();
        for (Deck deck : currentInventory.allDecks)
            addDeck(deck.clone());
        for (Deck deck : allDecks)
            if (deck.toString().equals(currentInventory.currentDeck.toString()))
                setCurrentDeck(deck);
    }

    public int getDeckCap() {
        return this.deckCap;
    }

    public void addDeck(Deck deck) {
        allDecks.add(deck);
    }

    public boolean removeDeck(String name) {
        for (Deck d : allDecks)
            if (d.toString().equals(name)) {
                allDecks.remove(d);
                if (currentDeck == d)
                    currentDeck = null;
                return true;
            }
        return false;
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

    public ArrayList<Deck> getHeroDecks(Hero hero) {
        ArrayList<Deck> ret = new ArrayList<>();
        for (Deck deck : allDecks)
            if (deck.getHero() == hero)
                ret.add(deck);
        return ret;
    }

    public Hero getCurrentHero() {
        if (currentDeck == null)
            return null;
        return currentDeck.getHero();
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
                jsonWriter.value(c.getClass().getSimpleName() + "/" + c.toString());
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
}