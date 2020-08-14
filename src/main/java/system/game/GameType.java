package system.game;

import elements.heros.DeckPair;
import shared.Controller;

public enum GameType {
    SINGLE_PLAYER (false),
    ONLINE_MULTIPLAYER (true),
    OFFLINE_MULTIPLAYER (false),
    DECK_READER (true);

    private final boolean needsQueue;

    GameType(boolean needsQueue) {
        this.needsQueue = needsQueue;
    }

    public boolean getNeedsQueue() {
        return needsQueue;
    }

    public boolean canJoin(Controller<?> controller) {
        if (this == DECK_READER)
            return DeckPair.getInstance() != null;
        return controller.getCurrentDeck() != null;
    }
}
