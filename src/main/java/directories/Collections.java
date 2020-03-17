package directories;

import game.*;
import heros.*;
import cards.*;

public class Collections extends Directory {
    Collections (Directory parent, Player myPlayer) {
        super("collections", parent, myPlayer);

        for (Hero h : myPlayer.getAllHeros())
            addChild(new HeroDirectory(h, this, myPlayer));
    }

    public void addHeroDirectory (Hero hero, Player player) {
        addChild(new HeroDirectory(hero, this, player));
    }
}
