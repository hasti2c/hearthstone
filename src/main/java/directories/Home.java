package directories;

import directories.collections.*;
import directories.game.PlayGround;
import gameObjects.*;

public class Home extends Directory {
    public Home(Player player) {
        super("~", null, player);
    }

    public void config() {
        clear();
        addChild(new Collections(this, player));
        addChild(new Store(this, player));
        addChild(new Stats(this, player));
        createPlayGround();
    }

    public boolean createPlayGround() {
        Game game = player.getNewGame();
        if (game == null)
            return false;
        PlayGround pg = new PlayGround(game, this, player);
        if (hasPlayGround())
            children.set(0, pg);
        else
            children.add(0, pg);
        return true;
    }

    public boolean hasPlayGround() {
        return children.size() > 0 && children.get(0) instanceof PlayGround;
    }

    public Game getGame() {
        return null;
    }
}
