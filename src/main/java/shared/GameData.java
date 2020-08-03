package shared;

import elements.cards.*;
import elements.heros.*;
import server.*;
import system.*;

import java.io.*;
import java.util.*;

public class GameData implements Configable {
    private static GameData gameData;
    private final ArrayList<Hero> herosList = new ArrayList<>();
    private final ArrayList<Card> cardsList = new ArrayList<>();

    @Override
    public void initialize(ServerController controller) {

    }

    @Override
    public String getJsonPath(ServerController controller, String name) {
        return "";
    }

    public static GameData getInstance() {
        if (gameData == null) {
            Configor<GameData> configor = null;
            try {
                configor = new Configor<>(null, "gameData", GameData.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            gameData = configor.getConfigedObject();
        }
        return gameData;
    }

    public ArrayList<Card> getCardsList() {
        return cardsList;
    }

    public Card getCard(String name) {
        for (Card card : cardsList)
            if (card.toString().equals(name))
                return card;
        return null;
    }

    public String toProperCase(String s) {
        s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        for (int i = 1; i < s.length(); i++)
            if (s.charAt(i - 1) == ' ')
                s = s.substring(0, i - 1).toLowerCase() + s.substring(i, i + 1).toUpperCase() + s.substring(i + 1).toLowerCase();
        return s;
    }

    public String toNonEnumCase(String s) {
        s = (toProperCase(s)).replace('_', ' ');
        for (int i = 1; i < s.length(); i++)
            if (s.charAt(i - 1) == ' ')
                s = s.substring(0, i) + s.substring(i, i + 1).toUpperCase() + s.substring(i + 1);
        return s;
    }

    public String toEnumCase(String s) {
        return (s.toUpperCase()).replace(' ', '_');
    }
}
