package directories.game;

import directories.Directory;
import gameObjects.*;

public class PlayGround extends Directory {
    private final Game game;
    private GameCards minionsInGame, hand, leftInDeck;

    public PlayGround(Game game, Directory parent, Player player) {
        super("play", parent, player);
        this.game = game;
        config();
    }

    public void config() {
        clear();


        minionsInGame = new GameCards("minions in game", game.getMinionsInGame(), this, player);
        addChild(minionsInGame);

        hand = new GameCards("hand", game.getHand(), this, player);
        addChild(hand);

        leftInDeck = new GameCards("left in deck", game.getLeftInDeck(), this, player);
        addChild(leftInDeck);
    }

    public Game getGame() {
        return game;
    }
}