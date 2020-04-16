package directories;

import game.Player;

public class Home extends Directory {
    public Home (Player myPlayer) {
        super ("~", null, myPlayer);
        addChild(new PlayGround(this, myPlayer));
        addChild(new Collections(this, myPlayer));
        addChild(new Store(this, myPlayer));
    }
}
