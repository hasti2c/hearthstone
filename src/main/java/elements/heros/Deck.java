package elements.heros;

import java.io.*;
import java.util.*;
import com.google.gson.stream.*;
import shared.*;
import system.*;
import elements.cards.*;
import system.player.*;

public class Deck implements Comparable<Deck>, Configable {
    private String name;
    private String playerName;
    private ArrayList<Card> cards = new ArrayList<>();
    private ArrayList<Integer> uses = new ArrayList<>();
    private HeroClass heroClass;
    private int wins, games, maxSize;

    public Deck() {}

    public Deck(String name, HeroClass heroClass, int maxSize, String playerName) {
        this.name = name;
        this.heroClass = heroClass;
        this.maxSize = maxSize;
        this.playerName = playerName;
    }

    public Deck(ArrayList<Card> cards) {
        name = "Deck Reader";
        this.cards = cards;
        heroClass = HeroClass.MAGE;
        maxSize = 30;
    }

    @Override
    public void initialize() {
        playerName = GameData.getInstance().getInitPlayerName();
    }

    @Override
    public String getJsonPath(String name) {
        return "decks/" + GameData.getInstance().getInitPlayerName() + "/";
    }

    public void updateJson() {
        try {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter("src/main/resources/database/decks/" + playerName + "/" + name + ".json"));
            jsonWriter.setIndent("  ");

            jsonWriter.beginObject();

            jsonWriter.name("name").value(name);
            jsonWriter.name("heroClass").value(heroClass.toString());
            jsonWriter.name("playerName").value(playerName);

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
        uses = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++)
            uses.add(0);
    }

    public String toString() {
        return this.name;
    }

    public void addCard(Card c) {
        cards.add(c);
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
/*
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
    }*/

    public Deck clone() {
        Deck deck = new Deck();
        deck.name = name;
        deck.heroClass = heroClass;
        deck.maxSize = maxSize;
        deck.cards = new ArrayList<>(cards);
        deck.wins = wins;
        deck.games = games;
        deck.playerName = playerName;
        return deck;
    }

    public Deck cloneCards() {
        Deck deck = clone();
        deck.cards = getCardClones();
        return deck;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    private ArrayList<Card> getCardClones() {
        ArrayList<Card> clones = new ArrayList<>();
        for (Card card : cards)
            clones.add(card.clone());
        return clones;
    }

    public HeroClass getHeroClass() {
        return heroClass;
    }

    public Hero getHero(Inventory inventory) {
        return heroClass.getHero(inventory);
    }

    public int getWins() {
        return wins;
    }

    public int getGames() {
        return games;
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

    public void addGame() {
        games++;
    }

    public void addWin() {
        wins++;
    }

    public <C extends Card> C getCard(C card, int num) {
        int i = 0;
        for (Card c : cards)
            if (card.toString().equals(c.toString()))
                if (++i == num)
                    return (C) c;
        return null;
    }
}
