package gameObjects.player;

import java.io.*;

import com.google.gson.stream.*;
import controllers.game.*;
import gameObjects.*;
import gameObjects.heros.*;
import gameObjects.cards.*;

public class Player implements Configable {
    private String username, password;
    private int balance, id;
    private Inventory inventory;
    private GameController controller;
    private Game game;
    private Writer logWriter;

    public static Player getExistingPlayer(GameController controller, String username) throws FileNotFoundException {
        controller.setInitPlayerName(username);
        Configor<Player> configor = new Configor<>(controller, username, Player.class);
        return configor.getConfigedObject();
    }

    public static Player getNewPlayer(GameController controller, String username, String password) {
        Player player = new Player();
        player.username = username;
        player.password = password;
        player.id = controller.getPlayerCount();
        player.initialize(controller);
        player.copyDefault();
        Configor.putInMap(player, username);
        return player;
    }

    @Override
    public void initialize(GameController controller) {
        this.controller = controller;
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
        Player defaultPlayer = controller.getDefaultPlayer();
        balance = defaultPlayer.balance;
        inventory = Inventory.copyDefault(defaultPlayer);
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
            jsonWriter.name("inventory").value(username + "-inventory");
            inventory.updateJson(username);

            jsonWriter.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public Game getGame() {
        return game;
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

    public void setCurrentDeck(Deck currentDeck) {
        inventory.setCurrentDeck(currentDeck);
    }

    public void deselectCurrentDeck() {
        inventory.setCurrentDeck(null);
    }

    public boolean addNewDeck(HeroClass heroClass, String name) {
        for (Deck d : inventory.getAllDecks())
            if (d.toString().equals(name))
                return false;
        Deck deck = new Deck(name, heroClass, inventory.getDeckCap());
        inventory.addDeck(deck);
        try {
            String path = "src/main/resources/database/decks/" + username + "/" + name + ".json";
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
        return c != null && !inventory.getAllCards().contains(c) && balance >= c.getPrice();
    }

    public boolean canSell(Card c) {
        if (!inventory.getAllCards().contains(c))
            return false;
        for (Deck d : inventory.getAllDecks())
            if (d.getCards().contains(c))
                return false;
        return inventory.getAllCards().contains(c);
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

    public Inventory getInventory() {
        return inventory;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
