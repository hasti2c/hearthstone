package gameObjects.heros;

import java.io.*;
import java.util.*;
import com.google.gson.stream.*;
import directories.*;
import directories.collections.*;
import directories.collections.Collections;
import directories.game.PlayGround;
import gameObjects.*;
import cli.*;
import cli.Console;

public class Hero implements Printable {
    private ArrayList<Deck> decks = new ArrayList<>();
    private Deck currentDeck;
    private int health = 30;
    private final String name;
    private final HeroClass heroClass;
    private Player player;
    private HeroDirectory directory;

    //TODO default hero deck
    public Hero(Player player, HeroClass heroClass) {
        this.player = player;
        this.heroClass = heroClass;
        this.name = heroClass.toString().toLowerCase();
        if (heroClass == HeroClass.WARLOCK)
            this.health = 35;
    }

    public int getHealth() {
        return health;
    }

    public String toString() {
        return this.name;
    }

    public HeroClass getHeroClass() {
        return this.heroClass;
    }

    public Hero clone() {
        Hero h = new Hero(player, heroClass);
        for (Deck d : decks)
            h.addDeck(d.clone(h));
//        for (Card c : heroDeck)
//            h.addCard(c.clone());
        h.health = health;
        return h;
    }

    public Deck getDeck(String deckName) {
        for (Deck d : decks)
            if (d.toString().equals(deckName))
                return d;
        return null;
    }

    public Deck getNewDeck(String deckName) {
        Deck d = new Deck(this, deckName);
        addDeck(d);
        return d;
    }

    public ArrayList<Deck> getDecks() {
        return decks;
    }

    public void setCurrentDeck(Deck currentDeck) {
        this.currentDeck = currentDeck;
        Player p = player;
        if (p != null && p.getCurrentHero() == this)
            p.getHome().createPlayGround();
    }

    public Player getPlayer() { return player; }

    public void deselectCurrentDeck() {
        currentDeck = null;
        assert player != null;
        Directory home = player.getHome();
        if (home.getChildren().get(0) instanceof PlayGround)
            home.getChildren().remove(0);
    }

    public Deck getCurrentDeck() {
        return currentDeck;
    }

    public String[] normalPrint(Player currentPlayer) {
        String[] ret = new String[3];
        if (currentPlayer.getCurrentDirectory() instanceof Collections && this == currentPlayer.getCurrentHero()) {
            ret[0] = Console.GREEN;
            ret[2] = Console.RESET;
        }
        ret[1] = toString();
        return ret;
    }

    public String[][] longPrint(Player currentPlayer) {
        String[][] ret = new String[16][3];
        for (int i = 0; i < 16; i++)
            switch (i) {
                case 0:
                    if (currentPlayer.getCurrentDirectory() instanceof Collections && this == currentPlayer.getCurrentHero()) {
                        ret[i][0] = Console.GREEN;
                        ret[i][1] = "current hero";
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
                    ret[i][1] = "hero";
                    break;
                case 4:
                    ret[i][1] = decks.size() + "";
                    break;
                case 7:
                    ret[i][1] = health + "";
            }
        return ret;
    }

    private void addDeck(Deck deck) {
        player.addDeckToAll(deck);
        decks.add(deck);
    }

    public boolean addNewDeck(String name) {
        for (Deck d : decks)
            if (d.toString().equals(name))
                return false;
        Deck deck = new Deck(this, name);
        addDeck(deck);
        try {
            assert player != null;
            String path = "src/main/resources/database/decks/" + player + "/" + this.name + "-" + name + ".json";
            (new File(path)).createNewFile();
            JsonWriter jsonWriter = new JsonWriter(new FileWriter(path));
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            jsonWriter.endObject();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean removeDeck(String name) {
        for (Deck d : decks)
            if (d.toString().equals(name)) {
                decks.remove(d);
                player.removeDeckFromAll(d);
                if (currentDeck == d)
                    currentDeck = null;
                return true;
            }
        return false;
    }

    public void setDirectory(HeroDirectory directory) {
        this.directory = directory;
    }
}