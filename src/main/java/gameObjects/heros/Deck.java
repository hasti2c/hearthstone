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
import gameObjects.cards.*;

public class Deck implements Printable, Comparable<Deck> {
    private String name;
    private ArrayList<Card> cards = new ArrayList<>();
    private ArrayList<Integer> uses = new ArrayList<>();
    private Hero hero;
    private int wins, games;
    private DeckDirectory directory;

    public Deck(Hero hero, String name) {
        this.hero = hero;
        this.name = name;
    }

    public Deck(Deck deck) {
        name = deck.name;
        hero = deck.hero.clone();
        wins = deck.wins;
        games = deck.games;

        cards = new ArrayList<>();
        for (Card c : deck.cards)
            cards.add(c.clone());
        uses = new ArrayList<>(deck.uses);
    }

    public void config(Player player) {
        try {
            JsonReader jsonReader = new JsonReader(new FileReader("src/main/resources/database/decks/" + player + "/" + hero + "-" + name + ".json"));
            assert JsonToken.BEGIN_OBJECT.equals(jsonReader.peek());
            jsonReader.beginObject();
            while (!JsonToken.END_OBJECT.equals(jsonReader.peek())) {
                assert JsonToken.NAME.equals(jsonReader.peek());
                String field = jsonReader.nextName();
                if ("cardNames".equals(field)) {
                    assert JsonToken.BEGIN_ARRAY.equals(jsonReader.peek());
                    jsonReader.beginArray();
                    while (!JsonToken.END_ARRAY.equals(jsonReader.peek()))
                        cards.add(GameController.getCard(jsonReader.nextString()));
                    jsonReader.endArray();
                } else if ("uses".equals(field)) {
                    assert JsonToken.BEGIN_ARRAY.equals(jsonReader.peek());
                    jsonReader.beginArray();
                    while (!JsonToken.END_ARRAY.equals(jsonReader.peek())) {
                        assert JsonToken.NUMBER.equals(jsonReader.peek());
                        uses.add(jsonReader.nextInt());
                    }
                    jsonReader.endArray();
                } else if ("wins".equals(field)) {
                    assert JsonToken.NUMBER.equals(jsonReader.peek());
                    wins = jsonReader.nextInt();
                } else if ("games".equals(field)) {
                    assert JsonToken.NUMBER.equals(jsonReader.peek());
                    games = jsonReader.nextInt();
                }
            }
            jsonReader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateJson(Player player) {
        try {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter("src/main/resources/database/decks/" + player + "/" + hero + "-" + name + ".json"));
            jsonWriter.setIndent("  ");

            jsonWriter.beginObject();

            jsonWriter.name("cardNames");
            jsonWriter.beginArray();
            for (Card c : cards)
                jsonWriter.value(c.toString());
            jsonWriter.endArray();

            jsonWriter.name("uses");
            jsonWriter.beginArray();
            for (Integer n : uses)
                jsonWriter.value(n);
            jsonWriter.endArray();

            jsonWriter.name("wins").value(wins);
            jsonWriter.name("games").value(games);

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
        return cnt <= 1 && (card.getHeroClass() == hero.getHeroClass() || card.getHeroClass() == HeroClass.NEUTRAL) && cards.size() < hero.getPlayer().getDeckCap();
    }

    Deck clone(Hero hero) {
        Deck d = new Deck(hero, name);
        for (Card c : cards)
            d.addCard(c.clone());
        d.wins = wins;
        d.games = games;
        return d;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public Hero getHero() {
        return hero;
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

    public void setDirectory(DeckDirectory directory) {
        this.directory = directory;
    }

    public String[] normalPrint(Player currentPlayer) {
        String[] ret = new String[3];
        Directory d = currentPlayer.getCurrentDirectory();
        if (d instanceof HeroDirectory && ((HeroDirectory) d).getHero().getCurrentDeck() == this) {
            ret[0] = Console.GREEN;
            ret[2] = Console.RESET;
        }
        ret[1] = toString();
        return ret;
    }

    public String[][] longPrint(Player currentPlayer) {
        String[][] ret = new String[16][3];
        Directory d = currentPlayer.getCurrentDirectory();
        for (int i = 0; i < 16; i++)
            switch (i) {
                case 0:
                    if (d instanceof HeroDirectory && ((HeroDirectory) d).getHero().getCurrentDeck() == this) {
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
                    ret[i][1] = hero.getHeroClass().toString().toLowerCase();
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

    public boolean move(Hero hero, String newName) {
        for (Deck d : hero.getDecks())
            if (d != this && d.toString().equals(newName))
                return false;

        for (Card c : cards)
            if (HeroClass.NEUTRAL != c.getHeroClass() && hero.getHeroClass() != c.getHeroClass())
                return false;

        this.hero.getDecks().remove(this);
        this.hero = hero;
        hero.getDecks().add(this);
        name = newName;
        return true;
    }
}
