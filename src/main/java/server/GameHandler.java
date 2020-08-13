package server;

import elements.cards.*;
import elements.heros.*;
import system.game.Character;
import system.game.*;
import system.player.*;

import java.util.*;

import static commands.types.ServerCommandType.*;

public class GameHandler {
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final ArrayList<Boolean> readyToStart = new ArrayList<>();
    private Game game;

    GameHandler (ClientHandler first, ClientHandler second) {
        clients.add(first);
        clients.add(second);
        first.setGameHandler(this);
        second.setGameHandler(this);
        readyToStart.add(false);
        readyToStart.add(false);
    }

    public void createGame() {
        GamePlayer firstPlayer = new GamePlayer(clients.get(0), PlayerFaction.FRIENDLY);
        GamePlayer secondPlayer = new GamePlayer(clients.get(1), PlayerFaction.ENEMY);
        game = Game.getInstance(new Character[]{firstPlayer, secondPlayer}, 2, false);

        for (ClientHandler client : clients) {
            client.setGame(game);
            ((ServerCommandRunner) client.getRunner()).update(JOIN_GAME, true, false);
        }
    }

    public boolean startGame(ClientHandler client, ArrayList<Card> cards) {
        int index = clients.indexOf(client);
        if (index < 0)
            return false;
        readyToStart.set(index, true);
        if (isReady()) {
            game.startGame(cards);
            ((ServerCommandRunner) clients.get(0).getRunner()).update(START_GAME, true);
            for (ClientHandler c : clients)
                c.getCurrentPlayer().getLogger().log("start_game", "game id: " + game.getId());
            game.logStartGame();
            return true;
        }
        return false;
    }

    private boolean isReady() {
        for (Boolean bool : readyToStart)
            if (!bool)
                return false;
        return true;
    }

    public ClientHandler getOpponent(ClientHandler client) {
        int index = clients.indexOf(client);
        if (index < 0)
            return null;
        return clients.get(1 - index);
    }

    public HeroClass[] getHeroClasses() {
        HeroClass[] ret = new HeroClass[2];
        for (int i = 0; i < 2; i++)
            ret[i] = game.getCharacters()[i].getHero().getHeroClass();
        return ret;
    }

    public String[] getJsons() {
        String[] ret = new String[2];
        for (int i = 0; i < 2; i++)
            ret[i] = game.getCharacters()[i].getState().getJson(false);
        return ret;
    }
}
