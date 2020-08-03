package system.player;

import java.io.*;

import com.google.gson.stream.*;
import server.*;
import elements.heros.*;
import elements.cards.*;
import shared.GameData;
import system.*;

public class Player implements Configable {
    private String username, password;
    private int balance, id;
    private Inventory inventory;
    private Game game;
    private Logger logger;

    public static Player getExistingPlayer(String username) throws FileNotFoundException {
        GameData.getInstance().setInitPlayerName(username);
        Configor<Player> configor = new Configor<>(username, Player.class);
        return configor.getConfigedObject();
    }

    public static Player getNewPlayer(ServerController controller, String username, String password) {
        Player player = new Player();
        player.username = username;
        player.password = password;
        player.id = controller.getPlayerCount();
        player.initialize();
        player.copyDefault();
        Configor.putInMap(player, username);
        return player;
    }

    @Override
    public void initialize() {
        logger = new Logger("src/main/resources/logs/players/" + username + "-" + id + ".txt");
    }

    @Override
    public String getJsonPath(String name) {
        return "players/";
    }

    private void copyDefault() {
        Player defaultPlayer = GameData.getInstance().getDefaultPlayer();
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
        Deck deck = new Deck(name, heroClass, inventory.getDeckCap(), username);
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

    public Logger getLogger() {
        return logger;
    }

    public void logSignup() {
        logger.log("USER: " + username);
        logger.log("PASSWORD: " + password);
        logger.log("CREATED_AT: ", "");
        logger.log("");
        logger.log("signup", "");
    }
}
