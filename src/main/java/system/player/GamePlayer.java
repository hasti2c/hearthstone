package system.player;

import controllers.game.*;
import elements.Element;
import elements.abilities.targets.Attackable;
import elements.Playable;
import system.Game;
import graphics.directories.playground.GamePlayerGraphics;
import elements.cards.*;
import elements.heros.*;
import javafx.scene.Node;
import javafx.util.Pair;

import java.lang.reflect.*;
import java.util.*;

public class GamePlayer extends Character {
    private final String name;
    private final Inventory inventory;

    public GamePlayer(GameController controller, Game game, PlayerFaction playerFaction) {
        super(controller.getCurrentHero().clone(), controller.getCurrentDeck(), game, playerFaction);
        Player player = controller.getCurrentPlayer();
        name = player.toString();
        inventory = player.getInventory();
    }

    public GamePlayer(GameController controller, Game game, PlayerFaction playerFaction, Deck deck) {
        super(deck.getHero(controller.getCurrentPlayer().getInventory()), deck, game, playerFaction);
        Player player = controller.getCurrentPlayer();
        name = player.toString();
        inventory = player.getInventory();
        randomDraw = false;
    }

    public String toString() {
        return name;
    }

    public Inventory getInventory() {
        return inventory;
    }

    protected void initializeHelper() {
        deck.addGame();
    }

    protected void startTurnHelper() {}

    protected void playCardHelper(Card card) {
        inventory.getCurrentDeck().addUse(card);
    }

    public void addWin() {
        deck.addWin();
    }
}
