package directories;

import controllers.game.*;
import cli.*;
import gameObjects.*;
import gameObjects.Player.Player;
import gameObjects.cards.*;
import java.util.*;

public class Store extends Directory {
    Store(Directory parent, Player player) {
        super("store", parent, player);
        config();
    }

    public void config() {
        clear();
        for (Card c : GameController.getCardsList())
            addContent(c);
    }

    public ArrayList<Printable> getPrintables(ArrayList<Character> options, boolean l) {
        ArrayList<Printable> objects = new ArrayList<>(), buyable = new ArrayList<>(), sellable = new ArrayList<>();
        String details = "";

        for (Printable c : content) {
            if (player.canBuy((Card) c))
                buyable.add(c);
            if (player.canSell((Card) c))
                sellable.add(c);
        }

        if (options.contains('a') || options.size() == 0) {
            objects.addAll(content);
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
            options.remove('a');
        if (options.contains('s'))
            options.remove('s');
        if (options.contains('b'))
            options.remove('b');
        if (options.contains('c'))
            options.remove('c');

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