package directories;

import java.util.*;
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
                case 0:
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
}
