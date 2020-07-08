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
        inventory.setCurrentDeck(deck.clone());
        randomDraw = false;
    }

    public String toString() {
        return name;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void startTurn() {
        if (getMyTurnNumber() != 1)
            for (int i = 0; i < drawCap; i++)
                draw();
        heroPowerCount = 0;

        for (Minion minion : minionsInGame)
            minion.setHasAttacked(false);
        hero.setHasAttacked(false);

        mana = Math.min(getMyTurnNumber(), 10);
        if (passive != null)
            mana += passive.getTurnManaPromotion(getMyTurnNumber());
    }

    public void endTurn() {
        doCardAction("doActionOnEndTurn");
        if (passive != null)
            passive.doEndTurnAction(minionsInGame);
        for (Minion minion : getMinionsInGame())
            minion.setAsleep(false);
    }

    @Override
    protected void playCardHelper(Card card) {
        inventory.getCurrentDeck().addUse(card);
    }
}
