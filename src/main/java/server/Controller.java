package server;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

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
    private static ArrayList<Hero> herosList = new ArrayList<>();
    private static ArrayList<Card> cardsList = new ArrayList<>();
    private final String defaultPath = "src/main/resources/database/defaults.json";

    public static Controller getInstance() {
        Configor<Controller> configor = null;
        try {
            configor = new Configor<>(null, "defaults", Controller.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return configor.getConfigedObject();
    }

    public static Card getCard(String name) {
        for (Card card : cardsList)
            if (card.toString().equals(name))
                return card;
        return null;
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

    public static ArrayList<Card> getCardsList() {
        return cardsList;
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

    public static String toProperCase(String s) {
        s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        for (int i = 1; i < s.length(); i++)
            if (s.charAt(i - 1) == ' ')
                s = s.substring(0, i - 1).toLowerCase() + s.substring(i, i + 1).toUpperCase() + s.substring(i + 1).toLowerCase();
        return s;
    }

    public static String toNonEnumCase(String s) {
        s = (toProperCase(s)).replace('_', ' ');
        for (int i = 1; i < s.length(); i++)
            if (s.charAt(i - 1) == ' ')
                s = s.substring(0, i) + s.substring(i, i + 1).toUpperCase() + s.substring(i + 1);
        return s;
    }

    public static String toEnumCase(String s) {
        return (s.toUpperCase()).replace(' ', '_');
    }

    private void updateJson() {
        try {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter(defaultPath));
            jsonWriter.setIndent("  ");

            jsonWriter.beginObject();
            jsonWriter.name("playerCount").value(playerCount);
            jsonWriter.name("gameCount").value(gameCount);

            jsonWriter.name("herosList");
            jsonWriter.beginArray();
            for (Hero h : herosList)
                jsonWriter.value(h.toString());
            jsonWriter.endArray();

            jsonWriter.name("cardsList");
            jsonWriter.beginArray();
            for (Card c : cardsList)
                jsonWriter.value(c.toString());
            jsonWriter.endArray();

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