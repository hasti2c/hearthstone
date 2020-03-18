package cards;

import game.Hearthstone;
import heros.*;
import directories.*;
import cli.*;
import java.util.*;

public class Spell extends Card {
    public String[][] longPrint () {
        String[][] ret = new String[12][3];
        Directory d = Hearthstone.getCurrentPlayer().getCurrentDirectory();
        for (int i = 0; i < 12; i++)
            switch (i) {
                case 1:
                    if (d instanceof HeroDirectory && ((HeroDirectory) d).getMyHero().getHeroDeck().contains(this)) {
                        ret[i][0] = Console.GREEN;
                        ret[i][1] = "in deck";
                        ret[i][2] = Console.RESET;
                    } else if (d instanceof HeroDirectory) {
                        ret[i][0] = Console.RED;
                        ret[i][1] = "not in deck";
                        ret[i][2] = Console.RESET;
                    }
                    break;
                case 2:
                    if (d instanceof Store && Hearthstone.getCurrentPlayer().getAllCards().contains(this)) {
                        ret[i][0] = Console.BLUE;
                        ret[i][1] = "owned";
                        ret[i][2] = Console.RESET;
                    } else if (d instanceof Store && Hearthstone.getCurrentPlayer().canBuy(this)) {
                        ret[i][0] = Console.GREEN;
                        ret[i][1] = "can buy";
                        ret[i][2] = Console.RESET;
                    } else if (d instanceof Store) {
                        ret[i][0] = Console.RED;
                        ret[i][1] = "can't buy";
                        ret[i][2] = Console.RESET;
                    }
                    break;
                case 3:
                    ret[i][0] = Console.LIGHT_PINK;
                    ret[i][1] = toString();
                    ret[i][2] = Console.RESET;
                    break;
                case 4:
                    ret[i][1] = "spell card";
                    break;
                case 6:
                    if (d instanceof Store)
                        ret[i][1] = getPrice() + "";
                    break;
                case 7:
                    ret[i][1] = getRarity().toString().toLowerCase();
                    break;
                case 9:
                    ret[i][1] = getMana() + "";
                    break;
                case 11:
                    ret[i][1] = getDescription();
            }
        return ret;
    }
}
