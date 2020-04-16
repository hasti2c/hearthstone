package directories;

import java.io.IOException;
import java.util.*;

import cards.Card;
import game.*;
import heros.*;
import cli.*;

public class HeroDirectory extends Directory {
    private Hero myHero;

    HeroDirectory (Hero myHero, Directory parent, Player myPlayer) {
        super (myHero.toString(), parent, myPlayer);
        this.myHero = myHero;
    }

    public Hero getMyHero () { return myHero; }

    @Override
    public String[] normalPrint () {
        String[] ret = new String[3];
        if (getMyPlayer().getCurrentDirectory() instanceof Collections && myHero == getMyPlayer().getCurrentHero()) {
            ret[0] = Console.GREEN;
            ret[2] = Console.RESET;
        }
        ret[1] = toString();
        return ret;
    }

    public String[][] longPrint () {
        String[][] ret = new String[12][3];
        for (int i = 0; i < 12; i++)
            switch (i) {
                case 1:
                    if (getMyPlayer().getCurrentDirectory() instanceof Collections && myHero == getMyPlayer().getCurrentHero()) {
                        ret[i][0] = Console.GREEN;
                        ret[i][1] = "current hero";
                        ret[i][2] = Console.RESET;
                    } else
                        ret[i][1] = "";
                    break;
                case 3:
                        ret[i][0] = Console.LIGHT_PINK;
                        ret[i][1] = toString();
                        ret[i][2] = Console.RESET;
                    break;
                case 4:
                    ret[i][1] = "hero";
                    break;
                case 8:
                    ret[i][1] = myHero.getHealth() + "";
            }
        return ret;
    }

    public ArrayList <Printable> getPrintables (ArrayList <Character> options, boolean l) throws IOException {
        ArrayList <Printable> objects = new ArrayList<>();
        String details;
        if (options.contains('m') && options.contains('n') && !options.contains('a')) {
            for (Printable c : getContent()) {
                assert c instanceof Card;
                if (myHero.getHeroDeck().contains(c) || (!myHero.getHeroDeck().contains(c) && myHero.canAddCard((Card) c)))
                    objects.add(c);
            }
            details = "cards: deck + can_add";
        } else if (options.contains('m') && !options.contains('a')) {
            for (Printable c : getContent()) {
                assert c instanceof Card ;
                if (myHero.getHeroDeck().contains(c))
                    objects.add(c);
            }
            details = "cards: deck";
        } else if (options.contains('n') && !options.contains('a')) {
            for (Printable c : getContent()) {
                assert c instanceof Card;
                if (!myHero.getHeroDeck().contains(c) && myHero.canAddCard((Card) c))
                    objects.add(c);
            }
            details = "cards: can_add";
        } else {
            objects.addAll(getContent());
            details = "cards: all";
        }

        if (options.contains('a'))
            options.remove(options.indexOf('a'));
        if (options.contains('n'))
            options.remove(options.indexOf('n'));
        if (options.contains('m'))
            options.remove(options.indexOf('m'));
        if (options.contains('c'))
            options.remove(options.indexOf('c'));

        if (options.size() > 0)
            return null;
        if(l)
            getMyPlayer().log("long_list", details);
        return objects;
    }
}
