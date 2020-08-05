package system.player;

import client.graphics.directories.playground.targets.DiscoverGraphics;
import client.graphics.directories.playground.targets.SelectionEventHandler;
import client.graphics.directories.playground.targets.TargetEventHandler;
import elements.Element;
import elements.Playable;
import elements.abilities.Ability;
import elements.abilities.targets.Targetable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import shared.Controller;
import system.*;
import elements.cards.*;
import elements.heros.*;

import java.net.http.HttpResponse;
import java.util.ArrayList;

public class GamePlayer extends Character {
    private final String name;
    private final Inventory inventory;

    public GamePlayer(Controller<?> controller, Game game, PlayerFaction playerFaction) {
        super(controller.getCurrentHero().clone(), controller.getCurrentDeck(), game, playerFaction);
        Player player = controller.getCurrentPlayer();
        name = player.toString();
        inventory = player.getInventory();
    }

    public GamePlayer(Controller<?> controller, Game game, PlayerFaction playerFaction, Deck deck) {
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
