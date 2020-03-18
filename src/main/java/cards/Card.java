package cards;

import cli.*;
import game.*;
import heros.*;
import directories.*;

import java.io.IOException;
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
    void setCardType (CardType cardType) { this.cardType = cardType; }

    @Override
    public String[] normalPrint () {
        String[] ret = new String[3];
        Directory d = Hearthstone.getCurrentPlayer().getCurrentDirectory();
        if ((d instanceof HeroDirectory && ((HeroDirectory) d).getMyHero().getHeroDeck().contains(this)) || (d instanceof Store && Hearthstone.getCurrentPlayer().canBuy(this))) {
            ret[0] = Console.GREEN;
            ret[2] = Console.RESET;
        }
        else if (d instanceof HeroDirectory && ((HeroDirectory) d).getMyHero().canAddCard(this) || (d instanceof Store && !Hearthstone.getCurrentPlayer().getAllCards().contains(this) && !Hearthstone.getCurrentPlayer().canBuy(this))) {
            ret[0] = Console.RED;
            ret[2] = Console.RESET;
        }
        ret[1] = toString();
        return ret;
    }

    public abstract String[][] longPrint();
}
