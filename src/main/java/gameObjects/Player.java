package gameObjects;

import java.io.*;
import java.util.*;
import com.google.gson.stream.*;
import controllers.game.*;
import directories.*;
import directories.game.PlayGround;
import gameObjects.heros.*;
import gameObjects.cards.*;

public class Player {
    private String username, password;
    private int balance, id, deckCap;

    private Hero currentHero;
    private ArrayList<Deck> allDecks = new ArrayList<>();
    private ArrayList<Hero> allHeros = new ArrayList<>();
    private ArrayList<Card> allCards = new ArrayList<>();
    private Home home = new Home(this);
    private Directory currentDirectory;
    private Writer logWriter;
    private static Player defaultPlayer;

    public Player(JsonReader jsonReader) {
        config(jsonReader);
    }

    public Player(String username) throws IOException {
        config(new JsonReader(new FileReader("src/main/resources/database/players/" + username + ".json")));
        home = new Home(this);
        currentDirectory = home;
        logWriter = new FileWriter(this.getLogPath());
    }

    public Player(GameController game, String username, String password) {
        this.username = username;
        this.password = password;
        id = game.getPlayerCount();
        balance = defaultPlayer.balance;

        allHeros = new ArrayList<>();
        for (Hero h : defaultPlayer.allHeros)
            addHero(new Hero(this, h.getHeroClass()));

        if (defaultPlayer.getCurrentHero() != null)
            for (Hero h : allHeros)
                if (defaultPlayer.currentHero.getHeroClass().equals(h.getHeroClass()))
                    setCurrentHero(h);

        for (Card c : defaultPlayer.allCards)
            addCardToAll(c);
        home = new Home(this);
        currentDirectory = home;
        try {
            logWriter = new FileWriter(getLogPath(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void config(JsonReader jsonReader) {
        try {
            jsonReader.beginObject();
            while (JsonToken.NAME.equals(jsonReader.peek())) {
                String field = jsonReader.nextName();
                if ("username".equals(field))
                    username = jsonReader.nextString();
                else if ("password".equals(field))
                    password = jsonReader.nextString();
                else if ("id".equals(field))
                    id = jsonReader.nextInt();
                else if ("balance".equals(field))
                    balance = jsonReader.nextInt();
                else if ("deckCap".equals(field))
                    deckCap = jsonReader.nextInt();
                else if ("heroNames".equals(field)) {
                    jsonReader.beginArray();
                    allHeros = new ArrayList<>();
                    while (!JsonToken.END_ARRAY.equals(jsonReader.peek()))
                        addHero(new Hero(this, HeroClass.valueOf(jsonReader.nextString().toUpperCase())));
                    jsonReader.endArray();
                } else if ("cardNames".equals(field)) {
                    jsonReader.beginArray();
                    allCards = new ArrayList<>();
                    while (!JsonToken.END_ARRAY.equals(jsonReader.peek())) {
                        Card c = GameController.getCard(jsonReader.nextString());
                        assert c != null;
                        addCardToAll(c);
                    }
                    jsonReader.endArray();
                } else if ("decks".equals(field)) {
                    assert JsonToken.BEGIN_ARRAY.equals(jsonReader.peek());
                    jsonReader.beginArray();
                    while (!JsonToken.END_ARRAY.equals(jsonReader.peek())) {
                        Deck d = createDeck(jsonReader.nextString());
                        assert d != null;
                        d.config(this);
                    }
                    jsonReader.endArray();
                } else if ("currentHeroName".equals(field)) {
                    assert allHeros != null;
                    String heroName = jsonReader.nextString();
                    for (Hero h : allHeros)
                        if (heroName.equals(h.toString())) {
                            setCurrentHero(h);
                            break;
                        }
                } else if ("currentDeckName".equals(field)) {
                    String name = jsonReader.nextString();
                    int i = 0;
                    while (name.charAt(i) != ':')
                        i++;
                    String heroName = name.substring(0, i), deckName = name.substring(i + 1);
                    assert currentHero != null && currentHero.toString().equals(heroName);
                    ArrayList<Deck> decks = currentHero.getDecks();
                    for (Deck d : decks)
                        if (d.toString().equals(deckName))
                            currentHero.setCurrentDeck(d);
                    assert currentHero.getCurrentDeck() != null;
                }
            }
            assert JsonToken.END_OBJECT.equals(jsonReader.peek());
            jsonReader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void configDefault(JsonReader jsonReader) {
        defaultPlayer = new Player(jsonReader);
    }

    private Deck createDeck(String name) {
        int i = 0;
        while (name.charAt(i) != ':')
            i++;
        String heroName = name.substring(0, i), deckName = name.substring(i + 1);
        for (Hero h : allHeros)
            if (h.toString().equals(heroName)) {
                return h.getNewDeck(deckName);
            }
        return null;
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

            jsonWriter.name("heroNames");
            jsonWriter.beginArray();
            for (Hero h : allHeros)
                jsonWriter.value(h.toString());
            jsonWriter.endArray();

            jsonWriter.name("cardNames");
            jsonWriter.beginArray();
            for (Card c : allCards)
                jsonWriter.value(c.toString());
            jsonWriter.endArray();

            jsonWriter.name("decks");
            jsonWriter.beginArray();
            for (Hero h : allHeros)
                for (Deck deck : h.getDecks()) {
                    jsonWriter.value(h + ":" + deck);
                    deck.updateJson(this);
                }
            jsonWriter.endArray();

            if (currentHero != null)
                jsonWriter.name("currentHeroName").value(currentHero.toString());

            if (currentHero != null && currentHero.getCurrentDeck() != null)
                jsonWriter.name("currentDeckName").value(currentHero.toString() + ":" + currentHero.getCurrentDeck().toString());
            jsonWriter.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateDefault(JsonWriter jsonWriter) {
        defaultPlayer.updateJson(jsonWriter);
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

    public Game getGame() {
        if (!home.hasPlayGround())
            return null;
        Directory pg = home.getChildren().get(0);
        assert pg instanceof PlayGround;
        return ((PlayGround) pg).getGame();
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

    public Hero getCurrentHero() {
        return this.currentHero;
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

    public String getLogPath() {
        return "src/main/resources/logs/" + username + "-" + id + ".txt";
    }

    public void setCurrentHero(Hero currentHero) {
        this.currentHero = currentHero;
        home.createPlayGround();
    }

    public void deselectCurrentHero() {
        currentHero = null;
        if (home.getChildren().get(0) instanceof PlayGround)
            home.getChildren().remove(0);
    }

    private void addHero(Hero h) {
        allHeros.add(h);
    }

    public void addCardToAll(Card card) {
        allCards.add(card);
    }

    public void addDeckToAll(Deck deck) {
        allDecks.add(deck);
    }

    public void removeCardFromAll(Card card) {
        for (int i = 0; i < allCards.size(); i++)
            if (allCards.get(i) == card)
                allCards.remove(i--);
    }

    public boolean canBuy(Card c) {
        return c != null && !allCards.contains(c) && balance >= c.getPrice();
    }

    public boolean canSell(Card c) {
        if (!allCards.contains(c))
            return false;
        for (Hero h : allHeros)
            for (Deck d : h.getDecks())
                if (d.getCards().contains(c))
                    return false;
        return allCards.contains(c);
    }

    public void removeDeckFromAll(Deck deck) {
        allDecks.remove(deck);
    }
}
