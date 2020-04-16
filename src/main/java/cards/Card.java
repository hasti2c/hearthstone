package cards;

import cli.*;
import game.*;
import heros.*;
import directories.*;

import java.util.ArrayList;

public abstract class Card implements Printable {
    private String name, description;
    private int mana, price;
    private HeroClass heroClass;
    private RarityType rarity;
    private CardType cardType;

    public String toString () { return this.name; }

    public String getDescription () { return this.description; }

    public int getMana () { return this.mana; }

    public int getPrice () { return this.price; }

    public CardType getCardType() { return this.cardType; }

    public HeroClass getHeroClass () { return this.heroClass; }

    public RarityType getRarity () { return this.rarity; }

    @Override
    public String[] normalPrint () {
        String[] ret = new String[3];
        Directory d = Hearthstone.getCurrentPlayer().getCurrentDirectory();
        if (d instanceof Store) {
            if (Hearthstone.getCurrentPlayer().canSell(this)) {
                ret[0] = Console.BLUE;
                ret[2] = Console.RESET;
            } else if (Hearthstone.getCurrentPlayer().canBuy(this)) {
                ret[0] = Console.GREEN;
                ret[2] = Console.RESET;
            } else if (!Hearthstone.getCurrentPlayer().getAllCards().contains(this) && !Hearthstone.getCurrentPlayer().canBuy(this)) {
                ret[0] = Console.RED;
                ret[2] = Console.RESET;
            }
            ret[1] = toString();
            return ret;
        }

        ArrayList <Card> deck = new ArrayList<>();
        if (d instanceof  HeroDirectory)
            deck = ((HeroDirectory) d).getMyHero().getHeroDeck();
        else if (d instanceof Collections)
            deck = Hearthstone.getCurrentPlayer().getCurrentHero().getHeroDeck();
        int cnt = 0;
        for (Card c : deck)
            if (c == this)
                cnt++;
        if (cnt > 0) {
            ret[0] = Console.GREEN;
            ret[1] = toString() + " (" + cnt + ")";
            ret[2] = Console.RESET;
        } else
            ret[1] = toString();
        return ret;
    }

    public abstract String[][] longPrint();
}
