package directories.collections;

import cli.*;
import directories.*;
import gameObjects.*;
import gameObjects.cards.*;
import gameObjects.heros.*;
import gameObjects.player.Player;

import java.util.*;

public class Collections extends Directory {
    public Collections(Directory parent, Player player) {
        super("collections", parent, player);
        config();
    }

    public void config() {
        clear();
        for (Hero h : inventory.getAllHeros())
            addHeroDirectory(h, player);
        for (Card c : inventory.getAllCards())
            addContent(c);
    }

    private void addHeroDirectory(Hero hero, Player player) {
        addChild(new HeroDirectory(hero, this, player));
    }

    public ArrayList<Printable> getPrintables(ArrayList<Character> options, boolean l) {
        ArrayList<Printable> objects = new ArrayList<>(), heros = new ArrayList<>(), cards = new ArrayList<>();
        String details = "";

        if (options.contains('m') && !options.contains('a')) {
            heros.add(inventory.getCurrentHero());
            for (Printable c : content)
                if (inventory.getCurrentHero().getCurrentDeck().getCards().contains(c))
                    cards.add(c);
            details = "current";
        } else {
            for (Directory d : children)
                heros.add(((HeroDirectory) d).getHero());
            cards.addAll(content);
            details = "all";
        }

        if (options.contains('a'))
            options.remove('a');
        if (options.contains('m'))
            options.remove('m');

        if (!options.contains('h') && !options.contains('c') || (options.contains('h') && options.contains('c'))) {
            objects.addAll(heros);
            objects.addAll(cards);
            details = "heros: " + details + " + cards: " + details;
        }
        if (options.contains('h')) {
            objects.addAll(heros);
            details = "heros: " + details;
        }
        if (options.contains('c')) {
            objects.addAll(cards);
            details = "cards: " + details;
        }

        if (options.contains('c'))
            options.remove('c');
        if (options.contains('h'))
            options.remove('h');

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
