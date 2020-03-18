package game;

import directories.*;
import directories.Collections;
import heros.*;
import cards.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Player {
    private static String defaultPath = "database/defaults/player.json";

    private String username, password;
    private int balance, id, deckCap = 15;
    private String currentHeroName;
    private ArrayList<String> heroNames;
    private ArrayList <ArrayList <String>> heroDecks;
    private ArrayList<String> cardNames;

    private transient Hero currentHero;
    private transient ArrayList<Hero> allHeros;
    private transient ArrayList<Card> allCards, deck;
    private transient Home home;
    private transient Directory currentDirectory = home;


    public Player () throws IOException {
        Game.setPlayerCount(Game.getPlayerCount() + 1);
        id = Game.getPlayerCount();
        balance = 50;
        currentHeroName = null;
        currentHero = null;
        heroNames = new ArrayList<>();
        heroDecks = new ArrayList<>();
        cardNames = new ArrayList<>();
        deck = new ArrayList<>();
        allHeros = new ArrayList<>();
        allCards = new ArrayList<>();
        home = new Home(this);
    }

    public static Player getInstance (String path) throws IOException {
        String json = Hearthstone.readFile(path);
        Player newPlayer = Hearthstone.getGson().fromJson(json, Player.class);
        for (Hero h : Game.getHerosList()) {
            if (h.toString().equals(newPlayer.currentHeroName))
                newPlayer.currentHero = h;
            if (newPlayer.heroNames.contains(h.toString())) {
                newPlayer.addHero(h);
                for (ArrayList<String> deck : newPlayer.heroDecks)
                    if (deck.get(0).equals(h.toString()))
                        h.createDeck(deck.subList(1, deck.size()));
            }
            newPlayer.home = new Home(newPlayer);
            newPlayer.currentDirectory = newPlayer.home;
        }
        for (Card c : Game.getCardsList())
            if (newPlayer.cardNames.contains(c.toString()))
                newPlayer.addCardToAll(c);
        return newPlayer;
    }

    public static Player getInstance (String username, String password) throws IOException {
        Player def = getInstance(defaultPath);
        def.username = username;
        def.password = password;
        return def;
    }

    public void updateJson () throws IOException {
        Hearthstone.writeFile("database/players/" + this.toString() + ".json", Hearthstone.getGson().toJson(this, Player.class));
    }


    public boolean loginAttempt (String password) { return this.password.equals(password); }
    public String toString () { return this.username; }
    public Home getHome () { return home; }
    public Directory getCurrentDirectory () { return currentDirectory; }
    public void setCurrentDirectory (Directory currentDirectory) { this.currentDirectory = currentDirectory; }
    public ArrayList<Hero> getAllHeros () { return this.allHeros; }
    public ArrayList<Card> getAllCards () { return this.allCards; }
    public ArrayList<Card> getDeck () { return this.deck; }
    public Hero getCurrentHero () { return this.currentHero; }
    public int getDeckCap () { return this.deckCap; }
    public int getBalance () { return this.balance; }
    public void setBalance (int balance) { this.balance = balance; }

    public List<String> getHeroDeck (Hero h) {
        for (ArrayList<String> deck : heroDecks)
            if (deck.get(0).equals(h.toString()))
                return deck.subList(1, deck.size());
        return null;
    }

    public void setCurrentHero (Hero currentHero) {
        this.currentHero = currentHero;
        this.deck = currentHero.getHeroDeck();
        this.currentHeroName = currentHero.toString();
    }

    void addHero (Hero h) {
        allHeros.add(h);
        if (!heroNames.contains(h.toString()))
            heroNames.add(h.toString());
        ArrayList<String> deck = new ArrayList<>();
        deck.add(h.toString());
        if (getHeroDeck(h) == null)
            heroDecks.add(deck);
        for (Directory d : home.getChildren())
            if(d instanceof Collections)
                ((Collections)d).addHeroDirectory(h, this);
    }

    public void addCardToAll (Card card) {
        allCards.add(card);
        if (!cardNames.contains(card.toString()))
            cardNames.add(card.toString());
        for (Directory d1 : home.getChildren())
            if (d1 instanceof Collections) {
                for (Directory d2 : d1.getChildren())
                    if (d2 instanceof HeroDirectory && (((HeroDirectory) d2).getMyHero().getHeroClass() == card.getHeroClass() || card.getHeroClass() == HeroClass.NEUTRAL))
                        d2.addContent(card);
            } else if (d1 instanceof Store)
                d1.removeContent(card);
    }

    public void removeCardFromAll (Card card) {
        for (int i = 0; i < allCards.size(); i++)
            if (allCards.get(i) == card)
                allCards.remove(i--);
        for (int i = 0; i < cardNames.size(); i++)
            if (cardNames.get(i).equals(card.toString()))
                cardNames.remove(i--);
        for (Directory d1 : home.getChildren())
            if (d1 instanceof Collections) {
                for (Directory d2 : d1.getChildren())
                    if (d2 instanceof HeroDirectory)
                        while (d2.removeContent(card)) { }
            } else if (d1 instanceof Store)
                d1.addContent(card);
    }

    public boolean canBuy (Card c) {
        return c != null && !allCards.contains(c) && balance >= c.getPrice();
    }

    public boolean canSell (Card c) {
        for (Hero h : allHeros)
            if (h.getHeroDeck().contains(c))
                return false;
        return true;
    }
}
