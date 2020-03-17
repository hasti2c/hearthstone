package heros;

import cards.*;
import cli.*;
import directories.Collections;
import game.*;
import directories.*;

import java.lang.reflect.Array;
import java.util.*;

public class Hero implements Printable {
    private ArrayList<Card> heroDeck = new ArrayList<>();
    private int health = 30;
    private final String name;
    private final HeroClass heroClass;
    private Player myPlayer;

    public Hero (HeroClass heroClass, String name, Player myPlayer) {
        this.name = name;
        this.heroClass = heroClass;
        this.myPlayer = myPlayer;
        if (heroClass == HeroClass.WARLOCK)
            this.health = 35;
    }

    public ArrayList<Card> getHeroDeck () { return heroDeck; }
    void setHeroDeck (ArrayList<Card> cards) { heroDeck = cards; }
    public int getHealth () { return health; }
    void setHealth (int health) { this.health = health; }
    public String toString () { return this.name; }
    public HeroClass getHeroClass () { return this.heroClass; }
    public void addCard (Card card) { heroDeck.add(card); }

    public boolean canAddCard (Card card) {
        int cnt = 0;
        for (Card c : heroDeck)
            if (c == card)
                cnt++;
        return cnt <= 1 && (card.getHeroClass() == this.heroClass || card.getHeroClass() == HeroClass.NEUTRAL) && this.heroDeck.size() < this.myPlayer.getDeckCap();
    }

    @Override
    public String[] normalPrint () {
        String[] ret = new String[3];
        if (myPlayer.getCurrentDirectory() instanceof Collections && this == myPlayer.getCurrentHero()) {
            ret[0] = Console.GREEN;
            ret[2] = Console.RESET;
        }
        ret[1] = toString();
        return ret;
    }

    public String[][] longPrint () {
        String[][] ret = new String[12][3];
        for (int i = 0; i < 12; i++)
            switch (i) {
                case 0:
                    if (myPlayer.getCurrentDirectory() instanceof Collections && this == myPlayer.getCurrentHero()) {
                        ret[i][0] = Console.GREEN;
                        ret[i][1] = "current hero";
                        ret[i][2] = Console.RESET;
                    } else
                        ret[i][1] = "";
                    break;
                case 3:
                    ret[i][0] = Console.LIGHT_PINK;
                    ret[i][1] = toString();
                    ret[i][2] = Console.RESET;
                    break;
                case 4:
                    ret[i][1] = "hero";
                    break;
                case 8:
                    ret[i][1] = health + "";
            }
        return ret;
    }
}
