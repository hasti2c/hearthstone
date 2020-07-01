package gameObjects.heros;

import java.io.*;
import java.util.*;
import com.google.gson.stream.*;
import controllers.game.*;
import gameObjects.*;
import gameObjects.Player.Inventory;
import gameObjects.cards.*;

public class Deck implements Comparable<Deck>, Configable {
    private String name;
    private ArrayList<Card> cards = new ArrayList<>();
    private ArrayList<Integer> uses = new ArrayList<>();
    private HeroClass heroClass;
    private int wins, games, maxSize;

    public Deck() {}

    public Deck(String name, HeroClass heroClass, int maxSize) {
        this.name = name;
        this.heroClass = heroClass;
        this.maxSize = maxSize;
    }

    public Deck(ArrayList<Card> cards) {
        name = "Deck Reader";
        this.cards = cards;
        heroClass = HeroClass.MAGE;
        maxSize = 15;
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
                jsonWriter.value(c.toString());
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

    public boolean addCard(Card c) {
        if (!canAddCard(c))
            return false;
        cards.add(c);
        uses.add(0);
        resetStats();
        return true;
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

    public Deck clone(Inventory inventory) {
        Deck deck = new Deck();
        deck.name = name;
        deck.heroClass = heroClass;
        deck.maxSize = maxSize;
        for (Card card : cards)
            deck.addCard(inventory.getCard(card.toString()));
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

    public boolean move(HeroClass heroClass) {
        for (Card c : cards)
            if (HeroClass.NEUTRAL != c.getHeroClass() && heroClass != c.getHeroClass())
                return false;
        this.heroClass = heroClass;
        return true;
    }

    public void setName(String name) {
        this.name = name;
    }
}
