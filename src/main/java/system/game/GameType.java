package system.game;

import elements.heros.*;
import shared.*;
import system.game.characters.Character;
import system.game.characters.*;

import java.util.Arrays;

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

    public boolean isMultiPlayer() {
        return clientCount > 1;
    }

    public boolean canJoin(Controller<?> controller) {
        if (this == DECK_READER)
            return DeckPair.getInstance() != null;
        return controller.getCurrentDeck() != null;
    }

    public Game createGame(int id, Controller<?>... controllers) {
        if (controllers.length != clientCount)
            return null;

        Character[] characters = new Character[2];
        switch (this) {
            case SINGLE_PLAYER -> {
                characters[0] = new GamePlayer(controllers[0], FRIENDLY);
                characters[1] = new NPC(controllers[0], ENEMY);
            }
            case ONLINE_MULTIPLAYER -> {
                characters[0] = new GamePlayer(controllers[0], FRIENDLY);
                characters[1] = new GamePlayer(controllers[1], ENEMY);
            }
            case OFFLINE_MULTIPLAYER -> {
                characters[0] = new GamePlayer(controllers[0], FRIENDLY);
                characters[1] = new GamePlayer(controllers[0], ENEMY);
            }
            case DECK_READER -> {
                Pair<Deck, Deck> decks = DeckPair.getInstance().getDecks();
                characters[0] = new GamePlayer(controllers[0], FRIENDLY, decks.getFirst());
                characters[1] = new GamePlayer(controllers[1], ENEMY, decks.getSecond());
            }
        }

        Game game = new Game(this, characters, id);
        for (Controller<?> controller : controllers)
            controller.setGame(game);
        System.out.println("game created: " + game + " " + Arrays.toString(game.getCharacters()));
        return game;
    }
}
