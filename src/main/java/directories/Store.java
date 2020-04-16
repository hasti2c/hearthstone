package directories;

import cli.*;
import game.*;
import cards.*;
import java.util.*;
import java.io.IOException;

public class Store extends Directory {
    Store (Directory parent, Player myPlayer) {
        super ("store", parent, myPlayer);
        for (Card c : Game.getCardsList())
                addContent(c);
    }

    public ArrayList <Printable> getPrintables (ArrayList <Character> options, boolean l) throws IOException {
        ArrayList <Printable> objects = new ArrayList<>(), buyable = new ArrayList<>(), sellable = new ArrayList<>();
        String details = "";

        for (Printable c : getContent()) {
            if (getMyPlayer().canBuy((Card) c))
                buyable.add(c);
            if (getMyPlayer().canSell((Card) c))
                sellable.add(c);
        }

        if (options.contains('a') || options.size() == 0) {
            objects.addAll(getContent());
            details = "cards: all";
        } else if (options.contains('b') && options.contains('s')) {
            objects.addAll(buyable);
            objects.addAll(sellable);
            details = "cards: buy + sell";
        } else if (options.contains('b')) {
            objects.addAll(buyable);
            details = "cards: buy";
        } else if (options.contains('s')) {
            objects.addAll(sellable);
            details = "cards: sell";
        }

        if (options.contains('a'))
            options.remove(options.indexOf('a'));
        if (options.contains('s'))
            options.remove(options.indexOf('s'));
        if (options.contains('b'))
            options.remove(options.indexOf('b'));
        if (options.contains('c'))
            options.remove(options.indexOf('c'));

        if(l)
            getMyPlayer().log("long_list", details);
        else
            getMyPlayer().log("list", details);
        return objects;
    }
}
