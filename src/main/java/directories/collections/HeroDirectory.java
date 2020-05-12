package directories.collections;

import java.util.*;
import directories.*;
import gameObjects.*;
import gameObjects.heros.*;
import cli.*;

public class HeroDirectory extends Directory {
    private Hero hero;

    HeroDirectory(Hero hero, Directory parent, Player player) {
        super(hero.toString(), parent, player);
        this.hero = hero;
        config();
    }

    public void config() {
        clear();
        for (Deck d : hero.getDecks())
            addChild(new DeckDirectory(d, this, player));
        hero.setDirectory(this);
    }

    public Hero getHero() {
        return hero;
    }

    @Override
    public String[] normalPrint(Player currentPlayer) {
        String[] ret = new String[3];
        if (currentPlayer.getCurrentDirectory() instanceof Collections && hero == currentPlayer.getCurrentHero()) {
            ret[0] = Console.GREEN;
            ret[2] = Console.RESET;
        }
        ret[1] = toString();
        return ret;
    }

    @Override
    public String[][] longPrint(Player currentPlayer) {
        String[][] ret = new String[16][3];
        for (int i = 0; i < 16; i++)
            switch (i) {
                case 0:
                    if (currentPlayer.getCurrentDirectory() instanceof Collections && hero == currentPlayer.getCurrentHero()) {
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
                    ret[i][1] = hero.getDecks().size() + "";
                case 7:
                    ret[i][1] = hero.getHealth() + "";
            }
        return ret;
    }

    public ArrayList<Printable> getPrintables(ArrayList<Character> options, boolean l) {
        ArrayList<Printable> objects = new ArrayList<>();
        String details;
        if (options.contains('m') && !options.contains('a')) {
            for (Directory d : children) {
                assert d instanceof DeckDirectory;
                Deck deck = ((DeckDirectory) d).getDeck();
                if (deck.getHero().getCurrentDeck() == deck)
                    objects.add(d);
            }
            details = "decks: current";
        } else {
            objects.addAll(children);
            details = "decks: all";
        }

        if (options.contains('a'))
            options.remove('a');
        if (options.contains('m'))
            options.remove('m');
        if (options.contains('d'))
            options.remove('d');

        if (options.size() > 0)
            return null;
        if (l)
            player.log("long_list", details);
        else
            player.log("list", details);
        return objects;
    }

    public Game getGame() {
        return null;
    }
}
