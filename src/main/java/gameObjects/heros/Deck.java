package gameObjects.heros;

import java.io.*;
import java.util.*;
import com.google.gson.stream.*;
import cli.*;
import cli.Console;
import controllers.game.*;
import directories.*;
import directories.collections.*;
import gameObjects.*;
import gameObjects.Player.Player;
import gameObjects.cards.*;

public class Deck implements Printable, Comparable<Deck>, Configable {
    private String name;
    private final ArrayList<Card> cards = new ArrayList<>();
    private final ArrayList<Integer> uses = new ArrayList<>();
    private HeroClass heroClass;
    private int wins, games, maxSize;

    public Deck() {}

    public static Deck getNewDeck(String name, HeroClass heroClass, int maxSize) {
        Deck deck = new Deck();
        deck.name = name;
        deck.heroClass = heroClass;
        deck.maxSize = maxSize;
        return deck;
    }

    @Override
    public void initialize(GameController controller) {}

    @Override
    public String getJsonPath(GameController controller, String name) {
        return "decks/" + controller.getInitPlayerName() + "/";
    }

    public void updateJson(String playerName) {
        try {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter("src/main/resources/database/decks/" + playerName + "/" + name + ".json"));
            jsonWriter.setIndent("  ");

            jsonWriter.beginObject();

            jsonWriter.name("name").value(name);
            jsonWriter.name("heroClass").value(heroClass.toString());

            jsonWriter.name("cards");
            jsonWriter.beginArray();
            for (Card c : cards)
                jsonWriter.value(c.getClass().getSimpleName() + "/" + c.toString());
            jsonWriter.endArray();

            jsonWriter.name("uses");
            jsonWriter.beginArray();
            for (Integer n : uses)
                jsonWriter.value(n);
            jsonWriter.endArray();

            jsonWriter.name("wins").value(wins);
            jsonWriter.name("games").value(games);
            jsonWriter.name("maxSize").value(maxSize);

            jsonWriter.endObject();
            jsonWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetStats() {
        wins = 0;
        games = 0;
        for (int i = 0; i < uses.size(); i++)
            uses.set(i, 0);
    }

    public String toString() {
        return this.name;
    }

    public void addCard(Card c) {
        cards.add(c);
        uses.add(0);
        resetStats();
    }

    public boolean removeCard(Card card) {
        for (int i = 0; i < cards.size(); i++)
            if (cards.get(i) == card) {
                cards.remove(i);
                resetStats();
                return true;
            }
        return false;
    }

    public boolean canAddCard(Card card) {
        int cnt = 0;
        for (Card c : cards)
            if (c == card)
                cnt++;
        return cnt <= 1 && (card.getHeroClass() == heroClass || card.getHeroClass() == HeroClass.NEUTRAL) && cards.size() < maxSize;
    }

    public Deck clone() {
        Deck deck = new Deck();
        deck.heroClass = heroClass;
        for (Card c : cards)
            deck.addCard(c.clone());
        deck.wins = wins;
        deck.games = games;
        return deck;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public HeroClass getHeroClass() {
        return heroClass;
    }

    public Hero getHero() {
        return heroClass.getHero();
    }

    public int getWins() {
        return wins;
    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public int getUses(Card c) {
        int i = cards.indexOf(c);
        assert i >= 0;
        return uses.get(i);
    }

    public void addUse(Card c) {
        for (int i = 0; i < cards.size(); i++)
            if (cards.get(i) == c) {
                uses.set(i, uses.get(i) + 1);
                return;
            }

    }

    public int getWinPercentage() {
        if (games == 0)
            return 0;
        return (int) (((double) wins / (double) games) * 100);
    }

    public int getPriceAverage() {
        if (cards.size() == 0)
            return 0;
        int sum = 0;
        for (Card c : cards)
            sum += c.getPrice();
        return sum / cards.size();
    }

    public Card getBestCard() {
        if (cards.size() == 0)
            return null;
        Card c = cards.get(0);
        for (int i = 1; i < cards.size(); i++)
            if (cards.get(i).compareTo(c, this) > 0)
                c = cards.get(i);
        return c;
    }

    @Override
    public String[] normalPrint(Player currentPlayer) {
        String[] ret = new String[3];
        Directory d = currentPlayer.getCurrentDirectory();
        if (d instanceof HeroDirectory && currentPlayer.getInventory().getCurrentDeck() == this) {
            ret[0] = Console.GREEN;
            ret[2] = Console.RESET;
        }
        ret[1] = toString();
        return ret;
    }

    @Override
    public String[][] longPrint(Player currentPlayer) {
        String[][] ret = new String[16][3];
        Directory d = currentPlayer.getCurrentDirectory();
        for (int i = 0; i < 16; i++)
            switch (i) {
                case 0:
                    if (d instanceof HeroDirectory && currentPlayer.getInventory().getCurrentDeck() == this) {
                        ret[i][0] = Console.GREEN;
                        ret[i][1] = "current deck";
                        ret[i][2] = Console.RESET;
                    } else
                        ret[i][1] = "";
                    break;
                case 1:
                    ret[i][0] = Console.LIGHT_PINK;
                    ret[i][1] = toString();
                    ret[i][2] = Console.RESET;
                    break;
                case 2:
                    ret[i][1] = "deck";
                    break;
                case 3:
                    ret[i][1] = heroClass.toString().toLowerCase();
                    break;
                case 4:
                    ret[i][1] = cards.size() + "";
                    break;
                case 11:
                    if (d instanceof Stats)
                        ret[i][1] = getWinPercentage() + "%";
                    break;
                case 12:
                    if (d instanceof Stats)
                        ret[i][1] = wins + "";
                    break;
                case 13:
                    if (d instanceof Stats)
                        ret[i][1] = games + "";
                    break;
                case 14:
                    if (d instanceof Stats)
                        ret[i][1] = getPriceAverage() + "";
                    break;
                case 15:
                    if (d instanceof Stats && cards.size() != 0)
                        ret[i][1] = getBestCard().toString();
                    break;
            }
        return ret;
    }

    @Override
    public int compareTo(Deck d) {
        if (getWinPercentage() != d.getWinPercentage())
            return getWinPercentage() - d.getWinPercentage();
        else if (wins != d.wins)
            return wins - d.wins;
        else if (games != d.games)
            return games - d.games;
        else if (getPriceAverage() != d.getPriceAverage())
            return getPriceAverage() - d.getPriceAverage();
        return 0;
    }

    public boolean move(HeroClass heroClass, String newName) {
        for (Card c : cards)
            if (HeroClass.NEUTRAL != c.getHeroClass() && heroClass != c.getHeroClass())
                return false;
        this.heroClass = heroClass;
        name = newName;
        return true;
    }
}
