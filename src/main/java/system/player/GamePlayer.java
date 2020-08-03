package system.player;

import server.*;
import system.*;
import elements.cards.*;
import elements.heros.*;

public class GamePlayer extends Character {
    private final String name;
    private final Inventory inventory;

    public GamePlayer(ServerController controller, Game game, PlayerFaction playerFaction) {
        super(controller.getCurrentHero().clone(), controller.getCurrentDeck(), game, playerFaction);
        Player player = controller.getCurrentPlayer();
        name = player.toString();
        inventory = player.getInventory();
    }

    public GamePlayer(ServerController controller, Game game, PlayerFaction playerFaction, Deck deck) {
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
