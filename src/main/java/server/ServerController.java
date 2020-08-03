package server;

import com.google.gson.stream.JsonWriter;
import commands.types.ServerCommandType;
import elements.heros.Deck;
import elements.heros.Hero;
import shared.Controller;
import shared.GameData;
import system.Configable;
import system.Configor;
import system.player.Player;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ServerController extends Controller<ServerCommandType> implements Configable {
    private Player currentPlayer = null;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private int playerCount, gameCount;
    private String initPlayerName;

    private ServerController() {
        GameData.getInstance();
    }

    public static ServerController getInstance() {
        Configor<ServerController> configor = null;
        try {
            configor = new Configor<>("defaults", ServerController.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return configor.getConfigedObject();
    }

    @Override
    public void initialize() {}

    @Override
    public String getJsonPath(String name) {
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


    @Override
    public ArrayList<?> getObjectsList(String name) {
        return switch (name) {
            case "Deck": yield currentPlayer.getInventory().getAllDecks();
            case "Card": yield GameData.getInstance().getCardsList();
            case "Attackable|mine": yield currentPlayer.getGame().getCharacters()[0].getAttackables();
            case "Attackable|opponent": yield currentPlayer.getGame().getCharacters()[1].getAttackables();
            default: yield new ArrayList<>();
        };
    }
}