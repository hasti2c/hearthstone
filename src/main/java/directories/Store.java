package directories;

import game.*;
import cards.*;

public class Store extends Directory {
    Store (Directory parent, Player myPlayer) {
        super ("store", parent, myPlayer);
        for (Card c : Hearthstone.getCardsList())
            if (!myPlayer.getAllCards().contains(c))
                addContent(c);
    }
}