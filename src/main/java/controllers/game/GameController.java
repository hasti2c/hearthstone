package controllers.game;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.ArrayList;

import gameObjects.*;
import gameObjects.Player.Player;
import gameObjects.cards.*;
import gameObjects.heros.*;
import com.google.gson.stream.*;


public class GameController implements Configable {
    private Player currentPlayer = null, defaultPlayer;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private int playerCount, gameCount;
    private String initPlayerName;
    private static ArrayList<Hero> herosList = new ArrayList<>();
    private static ArrayList<Card> cardsList = new ArrayList<>();
    //private static ArrayList<Passive> passivesList = new ArrayList<>();
    private final String defaultPath = "src/main/resources/database/defaults.json";

    public static GameController getInstance() {
        Configor<GameController> configor = null;
        try {
            configor = new Configor<>(null, "defaults", GameController.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return configor.getConfigedObject();
    }

    public static Hero getHero(String heroName) {
        for (Hero hero : herosList)
            if (hero.toString().equals(heroName))
                return hero;
        return null;
    }

    @Override
    public void initialize(GameController controller) {}

    @Override
    public String getJsonPath(GameController controller, String name) {
        return "";
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public static ArrayList<Hero> getHerosList() {
        return herosList;
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
                s = s.substring(0 , i - 1).toLowerCase() + s.substring(i, i + 1).toUpperCase() + s.substring(i + 1).toLowerCase();
        return s;
    }

    public static String toEnumCase(String s) {
        return (s.toUpperCase()).replace(' ', '_');
    }

    public static Card getCard(String name) {
        for (Card c : cardsList)
            if (c.toString().equals(name))
                return c;
        return null;
    }

    /*public static Passive getRandomPassive() {
        int n = passivesList.size();
        int i = (int) (Math.floor(Math.random() * n) % n);
        return passivesList.get(i);
    }*/

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
}
