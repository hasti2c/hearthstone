package directories;

import cli.*;
import gameObjects.*;
import gameObjects.heros.*;
import java.util.*;

public class Stats extends Directory {
    Stats(Directory parent, Player player) {
        super("stats", parent, player);
        config();
    }

    public void config() {
        clear();
        content.addAll(player.getAllDecks());
    }

    @Override
    public void addContent(Printable d) {
        ArrayList<Deck> tmpContent = new ArrayList<>();
        for (Printable p : content) {
            assert p instanceof Deck;
            tmpContent.add((Deck) p);
        }
        assert d instanceof Deck;
        java.util.Collections.sort(tmpContent);
        content = new ArrayList<>();
        for (int i = 0; i < tmpContent.size() && i < 10; i++)
            content.add(tmpContent.get(i));
    }

    public Game getGame() {
        return null;
    }
}
