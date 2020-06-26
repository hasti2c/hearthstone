package gameObjects;

import java.io.*;
import java.util.*;

import cli.Printable;
import com.google.gson.stream.*;
import controllers.game.*;
import directories.*;
import directories.game.PlayGround;
import gameObjects.heros.*;
import gameObjects.cards.*;

public class Player implements Configable {
    private String username, password;
    private int balance, id, deckCap;
    private Deck currentDeck;
    private ArrayList<Deck> allDecks = new ArrayList<>();
    private ArrayList<Hero> allHeros = new ArrayList<>();
    private final ArrayList<Card> allCards = new ArrayList<>();
    private Home home = new Home(this);
    private GameController controller;
    private Game game;
    private Directory currentDirectory;
    private Writer logWriter;

    private static Player defaultPlayer;

    public static Player getExistingPlayer(GameController controller, String username) throws FileNotFoundException {
        controller.setInitPlayerName(username);
        Configor<Player> configor = new Configor<>(controller, username, Player.class);
        return configor.getConfigedObject();
    }

    public static Player getNewPlayer(GameController controller, String username, String password) {
        Player player = new Player();
        player.username = username;
        player.password = password;
        player.controller = controller;
        player.id = controller.getPlayerCount();
        player.copyDefault();
        Configor.putInMap(player, username);
        return player;
    }

    public static void configDefaultPlayer(GameController controller, JsonReader jsonReader) {
        Configor<Player> configor = new Configor<>(controller, "-def-", Player.class, jsonReader);
        defaultPlayer = configor.getConfigedObject();
        defaultPlayer.controller = controller;
    }

    @Override
    public void initialize(GameController controller) {
        this.controller = controller;
        home = new Home(this);
        currentDirectory = home;
        try {
            logWriter = new FileWriter(this.getLogPath(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getJsonPath(GameController controller, String name) {
        return "players/";
    }

    private void copyDefault() {
        balance = defaultPlayer.balance;
        deckCap = defaultPlayer.deckCap;

        allHeros = new ArrayList<>(defaultPlayer.allHeros);
        allDecks = new ArrayList<>();
        for (Deck d : defaultPlayer.allDecks)
            addDeck(d.clone());

        if (defaultPlayer.getCurrentDeck() != null)
            for (Deck d : allDecks)
                if (defaultPlayer.currentDeck.toString().equals(d.toString()))
                    setCurrentDeck(d);

        for (Card c : defaultPlayer.allCards)
            addCard(c);
    }

    public void updateJson() {
        try {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter(getJsonPath()));
            jsonWriter.setIndent("  ");
            updateJson(jsonWriter);
            jsonWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateJson(JsonWriter jsonWriter) {
        try {
            jsonWriter.beginObject();

            if (username != null)
                jsonWriter.name("username").value(username);
            if (password != null)
                jsonWriter.name("password").value(password);
            if (id != 0)
                jsonWriter.name("id").value(id);

            jsonWriter.name("balance").value(balance);
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
                    deck.updateJson(this);
                }
            jsonWriter.endArray();

            jsonWriter.name("currentDeck").value(currentDeck.toString());
            jsonWriter.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateDefault(JsonWriter jsonWriter) {
        defaultPlayer.updateJson(jsonWriter);
    }

    public void log(String line) {
        try {
            logWriter.write(line + "\n");
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String type, String details) {
        try {
            logWriter.write(type + " " + GameController.getTime() + " " + details + "\n");
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loginAttempt(String password) {
        return this.password.equals(password);
    }

    public String toString() {
        return this.username;
    }

    public Home getHome() {
        return home;
    }

    public Game getNewGame() {
        if (currentDeck == null)
            return null;
        this.game = new Game(controller);
        return game;
    }

    public Game getGame() {
        return game;
    }

    public Directory getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(Directory currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public ArrayList<Hero> getAllHeros() {
        return this.allHeros;
    }

    public ArrayList<Card> getAllCards() {
        return this.allCards;
    }

    public int getDeckCap() {
        return this.deckCap;
    }

    public int getBalance() {
        return this.balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getJsonPath() {
        return "src/main/resources/database/players/" + username + ".json";
    }

    public String getDeckJsonPath() {
        return "src/main/resources/database/decks/" + username;
    }

    public String getLogPath() {
        return "src/main/resources/logs/players/" + username + "-" + id + ".txt";
    }

    private void addHero(Hero h) {
        allHeros.add(h);
    }

    public void addCard(Card card) {
        allCards.add(card);
    }

    public void addDeck(Deck deck) {
        allDecks.add(deck);
    }

    public void removeCard(Card card) {
        for (int i = 0; i < allCards.size(); i++)
            if (allCards.get(i) == card)
                allCards.remove(i--);
    }

    public boolean addNewDeck(HeroClass heroClass, String name) {
        for (Deck d : allDecks)
            if (d.toString().equals(name))
                return false;
        Deck deck = Deck.getNewDeck(name, heroClass, deckCap);
        addDeck(deck);
        try {
            String path = "src/main/resources/database/decks/" + username + "/" + ".json";
            (new File(path)).createNewFile();
            JsonWriter jsonWriter = new JsonWriter(new FileWriter(path));
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            jsonWriter.endObject();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean canBuy(Card c) {
        return c != null && !allCards.contains(c) && balance >= c.getPrice();
    }

    public boolean canSell(Card c) {
        if (!allCards.contains(c))
            return false;
        for (Deck d : allDecks)
            if (d.getCards().contains(c))
                return false;
        return allCards.contains(c);
    }

    public ArrayList<Deck> getAllDecks() {
        return allDecks;
    }

    public ArrayList<Deck> getHeroDecks(Hero hero) {
        ArrayList<Deck> ret = new ArrayList<>();
        for (Deck deck : allDecks)
            if (deck.getHero() == hero)
                ret.add(deck);
        return ret;
    }

    public void setCurrentDeck(Deck currentDeck) {
        this.currentDeck = currentDeck;
        home.createPlayGround();
    }

    public void deselectCurrentDeck() {
        currentDeck = null;
        if (home.getChildren().get(0) instanceof PlayGround)
            home.getChildren().remove(0);
    }

    public Deck getCurrentDeck() {
        return currentDeck;
    }

    public Hero getCurrentHero() {
        if (currentDeck == null)
            return null;
        return currentDeck.getHero();
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

    public boolean deleteDeckDirectory() {
        File dir = new File(getDeckJsonPath());
        File[] files = dir.listFiles();
        assert files != null;

        boolean ret = true;
        for (File f : files)
            ret &= f.delete();
        return ret && dir.delete();
    }
}
