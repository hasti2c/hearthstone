package directories.game;

import cli.*;
import directories.Directory;
import directories.collections.HeroDirectory;
import gameObjects.*;
import gameObjects.cards.*;
import gameObjects.heros.*;
import java.util.*;

public class PlayGround extends Directory {
    private Game game;
    private GameCards inGame, hand, leftInDeck;

    public PlayGround(Game game, Directory parent, Player player) {
        super("play", parent, player);
        this.game = game;
        config();
    }

    public void config() {
        clear();


        inGame = new GameCards("cards in game", game.getInGame(), this, player);
        addChild(inGame);

        hand = new GameCards("hand", game.getHand(), this, player);
        addChild(hand);

        leftInDeck = new GameCards("left in deck", game.getLeftInDeck(), this, player);
        addChild(leftInDeck);
    }

    public Game getGame() {
        return game;
    }
}