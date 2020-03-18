package game;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import heros.*;
import cards.*;

public class Game {
    private static int playerCount = 10;
    private static ArrayList <HeroClass> heroNames = new ArrayList<>();
    private static ArrayList <String> cardNames = new ArrayList<>();

    private transient static ArrayList <Hero> herosList = new ArrayList<>();
    private transient static ArrayList <Card> cardsList = new ArrayList<>();
    private transient static String defaultPath = "database/defaults/main.json";
    private transient static Gson gson = (new GsonBuilder()).setPrettyPrinting().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();

    public static Game getInstance () throws IOException {
        String json = Hearthstone.readFile(defaultPath);
        Game game = gson.fromJson(json, Game.class);
        for (HeroClass h : heroNames)
            herosList.add(new Hero(h));
        for (String s : cardNames) {
            String cardJson;
            try {
                cardJson = Hearthstone.readFile("database/cards/minion/" + s + ".json");
                cardsList.add(Hearthstone.getGson().fromJson(cardJson, Minion.class));
            } catch (FileNotFoundException e1) {
                try {
                    cardJson = Hearthstone.readFile("database/cards/spell/" + s + ".json");
                    cardsList.add(Hearthstone.getGson().fromJson(cardJson, Spell.class));
                } catch (FileNotFoundException e2) {
                    cardJson = Hearthstone.readFile("database/cards/weapon/" + s + ".json");
                    cardsList.add(Hearthstone.getGson().fromJson(cardJson, Weapon.class));
                }
            }
        }
        return game;
    }

    public static void updateJson () throws IOException {
        Hearthstone.writeFile(defaultPath, gson.toJson(Hearthstone.getGame()));
    }

    public static ArrayList <Hero> getHerosList () { return herosList; }
    public static ArrayList <Card> getCardsList () { return cardsList; }
    public static int getPlayerCount () { return playerCount; }
    public static void setPlayerCount (int p) throws IOException {
        playerCount = p;
        updateJson();
    }
}
