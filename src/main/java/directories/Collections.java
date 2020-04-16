package directories;

import cli.Printable;
import game.*;
import heros.*;

import java.io.IOException;
import java.util.ArrayList;

public class Collections extends Directory {
    Collections(Directory parent, Player myPlayer) {
        super("collections", parent, myPlayer);

        for (Hero h : myPlayer.getAllHeros())
            addChild(new HeroDirectory(h, this, myPlayer));
    }

    public void addHeroDirectory (Hero hero, Player player) {
        addChild(new HeroDirectory(hero, this, player));
    }

    public ArrayList<Printable> getPrintables (ArrayList <Character> options, boolean l) throws IOException {
        ArrayList <Printable> objects = new ArrayList<>(), heros = new ArrayList<>(), cards = new ArrayList<>();
        String details = "";

        if (options.contains('m') && !options.contains('a')) {
            heros.add(getMyPlayer().getCurrentHero());
            for (Printable c : getContent())
                if (getMyPlayer().getCurrentHero().getHeroDeck().contains(c))
                    cards.add(c);
            details = "current";
        } else {
            for (Directory d : getChildren())
                heros.add(((HeroDirectory) d).getMyHero());
            cards.addAll(getContent());
            details = "all";
        }

        if (options.contains('a'))
            options.remove(options.indexOf('a'));
        if (options.contains('m'))
            options.remove(options.indexOf('m'));

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
            options.remove(options.indexOf('c'));
        if (options.contains('h'))
            options.remove(options.indexOf('h'));

        if (options.size() > 0)
            return null;
        if(l)
            getMyPlayer().log("long_list", details);
        else
            getMyPlayer().log("list", details);

        return objects;
    }
}
