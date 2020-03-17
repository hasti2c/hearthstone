package game;

import directories.*;
import directories.Collections;
import heros.*;
import cards.*;

import java.util.*;

public class Player {
    private String name, password;
    private int balance = 50, id, deckCap = 10;
    private Hero currentHero;
    private ArrayList<Hero> allHeros = new ArrayList<>();
    private ArrayList<Card> allCards = new ArrayList<>(), deck = new ArrayList<>();
    private Home home = new Home(this);
    private Directory currentDirectory = home;

    public Player (String name, String password) {
        this.name = name;
        this.password = password;
        Hearthstone.setPlayerCount(Hearthstone.getPlayerCount() + 1);
        this.id = Hearthstone.getPlayerCount();

        Hero mage = new Hero (HeroClass.MAGE, "mage", this);
        Hero rogue = new Hero (HeroClass.ROGUE, "rogue", this);
        Hero warlock = new Hero (HeroClass.WARLOCK, "warlock", this);
        addHero(mage);
        addHero(rogue);
        addHero(warlock);

        setCurrentHero(mage);
    }


    public boolean loginAttempt (String password) { return this.password.equals(password); }

    public String toString () { return this.name; }
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

    public void setCurrentHero (Hero currentHero) {
        this.currentHero = currentHero;
        this.deck = currentHero.getHeroDeck();
    }

    void addHero (Hero h) {
        allHeros.add(h);
        for (Directory d : home.getChildren())
            if(d instanceof Collections)
                ((Collections)d).addHeroDirectory(h, this);
    }

    public void addCardToAll (Card card) {
        allCards.add(card);
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
