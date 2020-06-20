package directories.collections;

import cli.*;
import directories.*;
import gameObjects.*;
import gameObjects.cards.*;
import gameObjects.heros.*;
import gameObjects.player.Player;

import java.util.*;

public class DeckDirectory extends Directory {
    private Deck deck;

    DeckDirectory(Deck deck, Directory parent, Player player) {
        super(deck.toString(), parent, player);
        this.deck = deck;
        config();
    }

    public void config() {
        clear();
        for (Card c : inventory.getAllCards())
            if (c.getHeroClass().equals(deck.getHero().getHeroClass()) || c.getHeroClass().equals(HeroClass.NEUTRAL))
                addContent(c);
        deck.setDirectory(this);
    }

    public Deck getDeck() {
        return deck;
    }

    public ArrayList<Printable> getPrintables(ArrayList<Character> options, boolean l) {
        ArrayList<Printable> objects = new ArrayList<>();
        ArrayList<Card> cards = deck.getCards();
        String details;
        if (options.contains('m') && options.contains('n') && !options.contains('a')) {
            for (Printable c : content) {
                assert c instanceof Card;
                if (cards.contains(c) || (!cards.contains(c) && deck.canAddCard((Card) c)))
                    objects.add(c);
            }
            details = "cards: deck + can_add";
        } else if (options.contains('m') && !options.contains('a')) {
            for (Printable c : content) {
                assert c instanceof Card;
                if (cards.contains(c))
                    objects.add(c);
            }
            details = "cards: deck";
        } else if (options.contains('n') && !options.contains('a')) {
            for (Printable c : content) {
                assert c instanceof Card;
                if (!cards.contains(c) && deck.canAddCard((Card) c))
                    objects.add(c);
            }
            details = "cards: can_add";
        } else {
            objects.addAll(content);
            details = "cards: all";
        }

        if (options.contains('a'))
            options.remove('a');
        if (options.contains('n'))
            options.remove('n');
        if (options.contains('m'))
            options.remove('m');
        if (options.contains('c'))
            options.remove('c');

        if (options.size() > 0)
            return null;
        if (l)
            player.log("long_list", details);
        else
            player.log("list", details);
        return objects;
    }

    @Override
    protected String[] normalPrint() {
        String[] ret = new String[3];
        if (player.getCurrentDirectory() instanceof HeroDirectory && deck.getHero().getCurrentDeck() == deck) {
            ret[0] = Console.GREEN;
            ret[2] = Console.RESET;
        }
        ret[1] = toString();
        return ret;
    }

    @Override
    protected String[][] longPrint() {
        String[][] ret = new String[16][3];
        for (int i = 0; i < 16; i++)
            switch (i) {
                case 0:
                    if (player.getCurrentDirectory() instanceof HeroDirectory && deck.getHero().getCurrentDeck() == deck) {
                        ret[i][0] = Console.GREEN;
                        ret[i][1] = "current deck";
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
                    ret[i][1] = "deck";
                    break;
                case 3:
                    ret[i][1] = deck.getHero().getHeroClass().toString().toLowerCase();
                case 4:
                    ret[i][1] = deck.getCards().size() + "";
            }
        return ret;
    }

    public Game getGame() {
        return null;
    }
}
