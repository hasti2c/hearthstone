package controllers.game;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.ArrayList;

import gameObjects.cards.*;
import gameObjects.heros.*;
import com.google.gson.*;
import com.google.gson.stream.*;
import gameObjects.player.Player;


public class GameController {
    private Player currentPlayer = null;
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private int playerCount, gameCount;
    private static ArrayList<Hero> herosList = new ArrayList<>();
    private static ArrayList<Card> cardsList = new ArrayList<>();
    private static ArrayList<Passive> passivesList = new ArrayList<>();
    private String defaultPath = "src/main/resources/database/defaults.json";

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    static ArrayList<Hero> getHerosList() {
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

    public static void writeFile(String path, String text) {
        try {
            Writer writer = new FileWriter(path);
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    static Card getNewCard(String name) {
        Gson gson = new Gson();
        try {
            String cardJson = readFile("src/main/resources/database/cards/quest and reward/" + name + ".json");
            return gson.fromJson(cardJson, QuestAndReward.class);
        } catch (FileNotFoundException e1) {
            try {
                String cardJson = readFile("src/main/resources/database/cards/minion/" + name + ".json");
                return gson.fromJson(cardJson, Minion.class);
            } catch (FileNotFoundException e2) {
                try {
                    String cardJson = readFile("src/main/resources/database/cards/spell/" + name + ".json");
                    return gson.fromJson(cardJson, Spell.class);
                } catch (FileNotFoundException e3) {
                    try {
                        String cardJson = readFile("src/main/resources/database/cards/weapon/" + name + ".json");
                        return gson.fromJson(cardJson, Weapon.class);
                    } catch (FileNotFoundException e4) {
                        return null;
                    }
                }
            }
        }
    }

    public static Card getCard(String name) {
        for (Card c : cardsList)
            if (c.toString().equals(name))
                return c;
        return null;
    }

    public static Passive getRandomPassive() {
        int n = passivesList.size();
        int i = (int) (Math.floor(Math.random() * n) % n);
        return passivesList.get(i);
    }

    void configGame() {
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(defaultPath));
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                JsonToken next = jsonReader.peek();
                if (JsonToken.NAME.equals(next)) {
                    String field = jsonReader.nextName();
                    if ("playerCount".equals(field))
                        playerCount = jsonReader.nextInt();
                    else if ("gameCount".equals(field))
                        gameCount = jsonReader.nextInt();
                    else if ("heroNames".equals(field)) {
                        jsonReader.beginArray();
                        while (!JsonToken.END_ARRAY.equals(jsonReader.peek()))
                            herosList.add(new Hero(null, HeroClass.valueOf(jsonReader.nextString().toUpperCase())));
                        jsonReader.endArray();
                    } else if ("cardNames".equals(field)) {
                        jsonReader.beginArray();
                        while (!JsonToken.END_ARRAY.equals(jsonReader.peek())) {
                            Card c = getNewCard(jsonReader.nextString());
                            assert c != null;
                            cardsList.add(c);
                        }
                        jsonReader.endArray();
                    } else if ("passiveNames".equals(field)) {
                        jsonReader.beginArray();
                        while (!JsonToken.END_ARRAY.equals(jsonReader.peek()))
                            passivesList.add(new Passive(jsonReader.nextString()));
                        jsonReader.endArray();
                    }
                } else {
                    assert JsonToken.BEGIN_OBJECT.equals(next);
                    Player.configDefault(this, jsonReader);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateJson() {
        try {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter(defaultPath));
            jsonWriter.setIndent("  ");

            jsonWriter.beginObject();
            jsonWriter.name("playerCount").value(playerCount);
            jsonWriter.name("gameCount").value(gameCount);

            jsonWriter.name("heroNames");
            jsonWriter.beginArray();
            for (Hero h : herosList)
                jsonWriter.value(h.toString());
            jsonWriter.endArray();

            jsonWriter.name("cardNames");
            jsonWriter.beginArray();
            for (Card c : cardsList)
                jsonWriter.value(c.toString());
            jsonWriter.endArray();

            jsonWriter.name("passiveNames");
            jsonWriter.beginArray();
            for (Passive p : passivesList)
                jsonWriter.value(p.toString());
            jsonWriter.endArray();

            jsonWriter.name("defaultPlayer");
            Player.updateDefault(jsonWriter);

            jsonWriter.endObject();
            jsonWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String s, String s1) {
    }
}
