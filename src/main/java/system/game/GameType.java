package system.game;

import elements.heros.*;
import shared.*;
import system.game.characters.Character;
import system.game.characters.*;

import java.util.*;

import static system.player.PlayerFaction.*;

public enum GameType {
    SINGLE_PLAYER (1),
    ONLINE_MULTIPLAYER (2),
    OFFLINE_MULTIPLAYER (1),
    DECK_READER (2);

    private final int clientCount;

    GameType(int clientCount) {
        this.clientCount = clientCount;
    }

    public boolean needsQueue() {
        return clientCount > 1;
    }

    public boolean canJoin(Controller<?> controller) {
        if (this == DECK_READER)
            return DeckPair.getInstance() != null;
        return controller.getCurrentDeck() != null;
    }

    public Game createGame(ArrayList<? extends Controller<?>> controllers, int id) {
        if (controllers.size() != clientCount)
            return null;

        Character[] characters = new Character[2];
        switch (this) {
            case ONLINE_MULTIPLAYER -> {
                characters[0] = new GamePlayer(controllers.get(0), FRIENDLY);
                characters[1] = new GamePlayer(controllers.get(1), ENEMY);
            }
            case DECK_READER -> {
                Pair<Deck, Deck> decks = DeckPair.getInstance().getDecks();
                characters[0] = new GamePlayer(controllers.get(0), FRIENDLY, decks.getFirst());
                characters[1] = new GamePlayer(controllers.get(1), ENEMY, decks.getSecond());
            }
        }

        Game game = Game.getInstance(this, characters, id);
        for (Controller<?> controller : controllers)
            controller.setGame(game);
        return game;
    }
}
