package gameObjects.cards;

import cli.*;
import directories.collections.*;
import directories.game.PlayGround;
import gameObjects.*;
import gameObjects.heros.*;
import directories.*;

public abstract class Card implements Printable {
    private String name, description;
    private int mana, price;
    private HeroClass heroClass;
    private RarityType rarity;
    private CardType cardType;

    public String toString() {
        return this.name;
    }

    String getDescription() {
        return this.description;
    }

    public int getMana() {
        return this.mana;
    }

    public int getPrice() {
        return this.price;
    }

    public CardType getCardType() {
        return this.cardType;
    }

    public HeroClass getHeroClass() {
        return this.heroClass;
    }

    public RarityType getRarity() {
        return this.rarity;
    }

    private int getRarityNum() {
        if (RarityType.COMMON.equals(rarity))
            return 0;
        else if (RarityType.RARE.equals(rarity))
            return 1;
        else if (RarityType.EPIC.equals(rarity))
            return 2;
        else
            return 3;
    }

    public Card clone() {
        Card c = cloneHelper();
        c.name = name;
        c.description = description;
        c.mana = mana;
        c.price = price;
        c.heroClass = heroClass;
        c.rarity = rarity;
        c.cardType = cardType;
        return c;
    }

    abstract Card cloneHelper();

    public String[] normalPrint(Player currentPlayer) {
        String[] ret = new String[3];
        Directory d = currentPlayer.getCurrentDirectory();
        if (d instanceof Store) {
            if (currentPlayer.canSell(this)) {
                ret[0] = Console.BLUE;
                ret[2] = Console.RESET;
            } else if (currentPlayer.canBuy(this)) {
                ret[0] = Console.GREEN;
                ret[2] = Console.RESET;
            } else if (!currentPlayer.getAllCards().contains(this) && !currentPlayer.canBuy(this)) {
                ret[0] = Console.RED;
                ret[2] = Console.RESET;
            }
            ret[1] = toString();
            return ret;
        } else if (d instanceof PlayGround) {
            ret[1] = toString();
            return ret;
        }

        Deck deck = null;
        if (d instanceof DeckDirectory)
            deck = ((DeckDirectory) d).getDeck();
        else if (d instanceof HeroDirectory)
            deck = ((HeroDirectory) d).getHero().getCurrentDeck();
        else if (d instanceof Collections && currentPlayer.getCurrentHero() != null)
            deck = currentPlayer.getCurrentHero().getCurrentDeck();
        int cnt = 0;
        if (deck != null)
            for (Card c : deck.getCards())
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

    public abstract String[][] longPrint(Player currentPlayer);

    public int compareTo(Card c, Deck deck) {
        if (deck.getUses(this) != deck.getUses(c))
            return deck.getUses(this) - deck.getUses(c);
        else if (getRarityNum() != c.getRarityNum())
            return getRarityNum() - c.getRarityNum();
        else if (mana > c.mana)
            return mana - c.mana;
        else if (this instanceof Minion && !(c instanceof Minion))
            return 1;
        else if (c instanceof Minion && !(this instanceof Minion))
            return -1;
        return 0;
    }
}
