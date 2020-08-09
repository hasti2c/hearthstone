package system.game;

import shared.*;
import elements.cards.*;
import elements.heros.*;
import system.player.*;

public class GamePlayer extends Character {
    private final String name;
    private final Inventory inventory;

    public GamePlayer(Controller<?> controller, PlayerFaction playerFaction) {
        super(controller.getCurrentHero().clone(), controller.getCurrentDeck(), playerFaction);
        Player player = controller.getCurrentPlayer();
        name = player.toString();
        inventory = player.getInventory();
    }

    public GamePlayer(Controller<?> controller, PlayerFaction playerFaction, Deck deck) {
        super(deck.getHero(controller.getCurrentPlayer().getInventory()), deck, playerFaction);
        Player player = controller.getCurrentPlayer();
        name = player.toString();
        inventory = player.getInventory();
        randomDraw = false;
    }

    public String toString() {
        return name;
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
