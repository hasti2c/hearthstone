package heros;

import cards.*;
import cli.*;
import directories.Collections;
import game.*;

import java.util.*;

public class Hero implements Printable {
    private ArrayList<Card> heroDeck = new ArrayList<>();
    private int health = 30;
    private final String name;
    private final HeroClass heroClass;

    public Hero (HeroClass heroClass) {
        this.heroClass = heroClass;
        this.name = heroClass.toString().toLowerCase();
        if (heroClass == HeroClass.WARLOCK)
            this.health = 35;
    }

    public ArrayList<Card> getHeroDeck () { return heroDeck; }

    public int getHealth () { return health; }

    public String toString () { return this.name; }

    public HeroClass getHeroClass () { return this.heroClass; }

    public void addCard (Card card) {
        heroDeck.add(card);
        List<String> deckNames = Hearthstone.getCurrentPlayer().getHeroDeck(this);
        deckNames.add(card.toString());
    }

    public void createDeck (List<String> cardNames) {
        heroDeck = new ArrayList<>();
        for (Card c : Game.getCardsList())
            if (cardNames.contains(c.toString()))
                heroDeck.add(c);
    }

    public boolean canAddCard (Card card) {
        int cnt = 0;
        for (Card c : heroDeck)
            if (c == card)
                cnt++;
        return cnt <= 1 && (card.getHeroClass() == this.heroClass || card.getHeroClass() == HeroClass.NEUTRAL) && this.heroDeck.size() < Hearthstone.getCurrentPlayer().getDeckCap();
    }

    @Override
    public String[] normalPrint () {
        String[] ret = new String[3];
        if (Hearthstone.getCurrentPlayer().getCurrentDirectory() instanceof Collections && this == Hearthstone.getCurrentPlayer().getCurrentHero()) {
            ret[0] = Console.GREEN;
            ret[2] = Console.RESET;
        }
        ret[1] = toString();
        return ret;
    }

    public String[][] longPrint () {
        Player myPlayer = Hearthstone.getCurrentPlayer();
        String[][] ret = new String[12][3];
        for (int i = 0; i < 12; i++)
            switch (i) {
                case 1:
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
