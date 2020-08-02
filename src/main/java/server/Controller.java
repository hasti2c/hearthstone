package server;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import shared.GameData;
import system.*;
import elements.cards.*;
import elements.heros.*;
import com.google.gson.stream.*;
import system.player.Player;

public class Controller implements Configable {
    private Player currentPlayer = null, defaultPlayer;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private int playerCount, gameCount;
    private String initPlayerName;

    public Controller() {
        GameData.getInstance();
    }

    public static Controller getInstance() {
        Configor<Controller> configor = null;
        try {
            configor = new Configor<>(null, "defaults", Controller.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return configor.getConfigedObject();
    }

    @Override
    public void initialize(Controller controller) {}

    @Override
    public String getJsonPath(Controller controller, String name) {
        return "";
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
        updateJson();
    }

    public int getGameCount() {
        return gameCount;
    }

    public void setGameCount(int gameCount) {
        this.gameCount = gameCount;
        updateJson();
    }

    public static String getTime() {
        return dtf.format(LocalDateTime.now());
    }

    public static String readFile(String path) throws FileNotFoundException {
        String ret = "";
        try {
            Reader reader = new FileReader(path);
            int data;
            data = reader.read();
            while (data != -1) {
                ret = ret.concat((char) data + "");
                data = reader.read();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void updateJson() {
        try {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter("src/main/resources/database/defaults.json"));
            jsonWriter.setIndent("  ");

            jsonWriter.beginObject();
            jsonWriter.name("playerCount").value(playerCount);
            jsonWriter.name("gameCount").value(gameCount);

            jsonWriter.name("defaultPlayer").value("-def-");

            jsonWriter.endObject();
            jsonWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getInitPlayerName() {
        return initPlayerName;
    }

    public void setInitPlayerName(String initPlayerName) {
        this.initPlayerName = initPlayerName;
    }

    public Player getDefaultPlayer() {
        return defaultPlayer;
    }

    public Hero getCurrentHero() {
        if (currentPlayer == null || currentPlayer.getInventory().getCurrentHero() == null)
            return null;
        return currentPlayer.getInventory().getCurrentHero();
    }

    public Deck getCurrentDeck() {
        if (currentPlayer == null || currentPlayer.getInventory().getCurrentDeck() == null)
            return null;
        return currentPlayer.getInventory().getCurrentDeck();
    }
}
