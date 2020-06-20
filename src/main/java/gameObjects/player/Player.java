package gameObjects.player;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import com.google.gson.stream.*;
import controllers.game.*;
import directories.*;
import directories.game.PlayGround;
import gameObjects.Game;
import gameObjects.heros.*;
import gameObjects.cards.*;

public class Player {
    private String username, password;
    private int balance, id, deckCap;

    private Inventory inventory;
    private Home home;
    private GameController controller;
    private Game game;
    private Directory currentDirectory;
    private FileHandler fileHandler;
    private static Player defaultPlayer;

    private Player(GameController controller, boolean isNewPlayer) {
        this.controller = controller;
        home = new Home(this);
        currentDirectory = home;
        inventory = new Inventory();
        fileHandler = new FileHandler(isNewPlayer);
    }

    private Player(GameController controller, JsonReader jsonReader) {
        this(controller, false);
        fileHandler.config(jsonReader);
    }

    private Player(GameController controller, String username, String password) {
        this(controller, true);
        this.username = username;
        this.password = password;
        id = controller.getPlayerCount();
        copyDefault();
    }

    public static Player getExistingPlayer(GameController controller, String username) throws IOException {
        return new Player(controller, new JsonReader(new FileReader("src/main/resources/database/players/" + username + ".json")));
    }

    public static Player getNewPlayer(GameController controller, String username, String password) {
        return new Player(controller, username, password);
    }

    public static Player getDefaultPlayer() {
        return defaultPlayer;
    }

    private void copyDefault() {
        balance = defaultPlayer.balance;
        deckCap = defaultPlayer.deckCap;
        inventory.copyDefault();
    }

    public static void configDefault(GameController controller, JsonReader jsonReader) {
        defaultPlayer = new Player(controller, jsonReader);
    }

    public static void updateDefault(JsonWriter jsonWriter) {
        defaultPlayer.fileHandler.updateJson(jsonWriter);
    }

    public boolean loginAttempt(String password) {
        return this.password.equals(password);
    }

    public String toString() {
        return this.username;
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

    public Directory getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(Directory currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public Home getHome() {
        return home;
    }

    public Game getNewGame() {
        Hero currentHero = inventory.getCurrentHero();
        if (currentHero == null || currentHero.getCurrentDeck() == null)
            return null;
        this.game = new Game(controller, currentHero.getCurrentDeck());
        return game;
    }

    public Game getGame() {
        return game;
    }

    public boolean canBuy(Card c) {
        return c != null && !inventory.getAllCards().contains(c) && balance >= c.getPrice();
    }

    public boolean canSell(Card c) {
        if (!inventory.getAllCards().contains(c))
            return false;
        for (Hero h : inventory.getAllHeros())
            for (Deck d : h.getDecks())
                if (d.getCards().contains(c))
                    return false;
        return inventory.getAllCards().contains(c);
    }

    public void updateJson() {
        fileHandler.updateJson();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void log(String type, String details) {
        fileHandler.log(type, details);
    }

    public boolean deletePlayer() {
        fileHandler.logDelete();
        return fileHandler.deleteFiles();
    }

    public class Inventory {
        private Hero currentHero;
        private ArrayList<Hero> allHeros = new ArrayList<>();
        private ArrayList<Deck> allDecks = new ArrayList<>();
        private ArrayList<Card> allCards = new ArrayList<>();

        private void config(JsonReader jsonReader) {
            try {
                jsonReader.beginObject();
                while (JsonToken.NAME.equals(jsonReader.peek())) {
                    Class inventoryClass = Inventory.class;
                    String fieldName = jsonReader.nextName();
                    Field field = inventoryClass.getDeclaredField(fieldName);
                    if (field.getType().equals(String.class))
                        field.set(Player.this, jsonReader.nextString());
                    else if (field.getType().equals(int.class))
                        field.set(Player.this, jsonReader.nextInt());
                    else if (field.getType().equals(Inventory.class))
                        inventory.config(jsonReader);
                }
            } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        void copyDefault() {
            Inventory defaultInventory = Player.getDefaultPlayer().getInventory();
            allHeros = new ArrayList<>();
            for (Hero h : defaultInventory.allHeros)
                addHero(new Hero(Player.this, h.getHeroClass()));
            allDecks = new ArrayList<>();
            for (Deck d : defaultInventory.allDecks)
                addDeckToAll(new Deck(d));

            if (defaultInventory.getCurrentHero() != null)
                for (Hero h : allHeros)
                    if (defaultInventory.currentHero.getHeroClass().equals(h.getHeroClass()))
                        setCurrentHero(h);

            for (Card c : defaultInventory.allCards)
                addCardToAll(c);
        }

        public ArrayList<Hero> getAllHeros() {
            return this.allHeros;
        }

        void addHero(Hero h) {
            allHeros.add(h);
        }

        public Hero getCurrentHero() {
            return this.currentHero;
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

        public void addDeckToAll(Deck deck) {
            allDecks.add(deck);
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

        public void removeDeckFromAll(Deck deck) {
            allDecks.remove(deck);
        }

        public ArrayList<Deck> getAllDecks() {
            return allDecks;
        }

        public Deck getCurrentDeck() {
            if (currentHero == null)
                return null;
            return currentHero.getCurrentDeck();
        }

        public ArrayList<Card> getAllCards() {
            return this.allCards;
        }

        public void addCardToAll(Card card) {
            allCards.add(card);
        }

        public void removeCardFromAll(Card card) {
            for (int i = 0; i < allCards.size(); i++)
                if (allCards.get(i) == card)
                    allCards.remove(i--);
        }
    }

    private class FileHandler {
        private File jsonFile, deckJsonDirectory, logFile;

        private FileHandler(boolean isNewPlayer) {
            jsonFile = new File("src/main/resources/database/players/" + username + ".json");
            deckJsonDirectory = new File("src/main/resources/database/decks/" + username);
            logFile = new File("src/main/resources/logs/players/" + username + "-" + id + ".txt");
            if (isNewPlayer) {
                try {
                    (deckJsonDirectory).mkdir();
                    (logFile).createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                logSignUp();
            }
        }

        private void config(JsonReader jsonReader) {
            try {
                jsonReader.beginObject();
                while (JsonToken.NAME.equals(jsonReader.peek())) {
                    Class playerClass = Player.class;
                    String fieldName = jsonReader.nextName();
                    Field field = playerClass.getDeclaredField(fieldName);
                    if (field.getType().equals(String.class))
                        field.set(Player.this, jsonReader.nextString());
                    else if (field.getType().equals(int.class))
                        field.set(Player.this, jsonReader.nextInt());
                    else if (field.getType().equals(Inventory.class))
                        inventory.config(jsonReader);
                }
            } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        /*private void config(JsonReader jsonReader) {
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
                        inventory.allHeros = new ArrayList<>();
                        while (!JsonToken.END_ARRAY.equals(jsonReader.peek()))
                            inventory.addHero(new Hero(Player.this, HeroClass.valueOf(jsonReader.nextString().toUpperCase())));
                        jsonReader.endArray();
                    } else if ("cardNames".equals(field)) {
                        jsonReader.beginArray();
                        inventory.allCards = new ArrayList<>();
                        while (!JsonToken.END_ARRAY.equals(jsonReader.peek())) {
                            Card c = GameController.getCard(jsonReader.nextString());
                            assert c != null;
                            inventory.addCardToAll(c);
                        }
                        jsonReader.endArray();
                    } else if ("decks".equals(field)) {
                        assert JsonToken.BEGIN_ARRAY.equals(jsonReader.peek());
                        jsonReader.beginArray();
                        while (!JsonToken.END_ARRAY.equals(jsonReader.peek())) {
                            Deck d = inventory.createDeck(jsonReader.nextString());
                            assert d != null;
                            d.config(Player.this);
                        }
                        jsonReader.endArray();
                    } else if ("currentHeroName".equals(field)) {
                        assert inventory.allHeros != null;
                        String heroName = jsonReader.nextString();
                        for (Hero h : inventory.allHeros)
                            if (heroName.equals(h.toString())) {
                                inventory.setCurrentHero(h);
                                break;
                            }
                    } else if ("currentDeckName".equals(field)) {
                        String name = jsonReader.nextString();
                        int i = 0;
                        while (name.charAt(i) != ':')
                            i++;
                        String heroName = name.substring(0, i), deckName = name.substring(i + 1);
                        assert inventory.currentHero != null && inventory.currentHero.toString().equals(heroName);
                        ArrayList<Deck> decks = inventory.currentHero.getDecks();
                        for (Deck d : decks)
                            if (d.toString().equals(deckName))
                                inventory.currentHero.setCurrentDeck(d);
                        assert inventory.currentHero.getCurrentDeck() != null;
                    }
                }
                assert JsonToken.END_OBJECT.equals(jsonReader.peek());
                jsonReader.endObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        private void updateJson() {
            try {
                JsonWriter jsonWriter = new JsonWriter(new FileWriter(jsonFile));
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
                for (Hero h : inventory.allHeros)
                    jsonWriter.value(h.toString());
                jsonWriter.endArray();

                jsonWriter.name("allCards");
                jsonWriter.beginArray();
                for (Card c : inventory.allCards)
                    jsonWriter.value(c.toString());
                jsonWriter.endArray();

                jsonWriter.name("allDecks");
                jsonWriter.beginArray();
                for (Hero h : inventory.allHeros)
                    for (Deck deck : h.getDecks()) {
                        jsonWriter.value(h + ":" + deck);
                        deck.updateJson(Player.this);
                    }
                jsonWriter.endArray();

                if (inventory.currentHero != null)
                    jsonWriter.name("currentHero").value(inventory.currentHero.toString());

                if (inventory.currentHero != null && inventory.currentHero.getCurrentDeck() != null)
                    jsonWriter.name("currentDeck").value(inventory.currentHero.toString() + ":" + inventory.currentHero.getCurrentDeck().toString());
                jsonWriter.endObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void log(String line) {
            try {
                FileWriter logWriter = new FileWriter(logFile, true);
                logWriter.write(line + "\n");
                logWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void log(String type, String details) {
            try {
                FileWriter logWriter = new FileWriter(logFile, true);
                logWriter.write(type + " " + GameController.getTime() + " " + details + "\n");
                logWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void logSignUp() {
            log("USER: " + username);
            log("PASSWORD: " + password);
            log("CREATED_AT: ", "");
            log("");
            log("signup", "");
        }

        private void logDelete() {
            log("");
            log("DELETED_AT:", "");
        }

        private boolean deleteDeckDirectory() {
            File[] files = deckJsonDirectory.listFiles();
            assert files != null;

            boolean ret = true;
            for (File f : files)
                ret &= f.delete();
            return ret && deckJsonDirectory.delete();
        }

        private boolean deleteFiles() {
            return jsonFile.delete() && deleteDeckDirectory();
        }
    }
}
