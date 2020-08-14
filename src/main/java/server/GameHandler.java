package server;

import elements.cards.*;
import elements.heros.*;
import system.game.*;

import java.util.*;

import static commands.types.ServerCommandType.*;

public class GameHandler {
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final ArrayList<ArrayList<Card>> readyToStart = new ArrayList<>();
    private Game game;

    GameHandler (ClientHandler first, ClientHandler second) {
        clients.add(first);
        clients.add(second);
        first.setGameHandler(this);
        second.setGameHandler(this);
        readyToStart.add(null);
        readyToStart.add(null);
    }

    public boolean createGame(GameType gameType, int id) {
        game = gameType.createGame(id, clients.get(0), clients.get(1));
        return game == null;
    }

    public boolean startGame(ClientHandler client, ArrayList<Card> cards) {
        int index = clients.indexOf(client);
        if (index < 0)
            return false;
        readyToStart.set(index, cards);
        if (isReady()) {
            game.startGame(readyToStart);
            for (ClientHandler c : clients) {
                ((ServerCommandRunner) c.getRunner()).update(START_GAME, true, false);
                c.getCurrentPlayer().getLogger().log("start_game", "game id: " + game.getId());
            }
            game.logStartGame();
            return true;
        }
        return false;
    }

    private boolean isReady() {
        for (ArrayList<Card> cards : readyToStart)
            if (cards == null)
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

    public boolean isMyTurn(ClientHandler client) {
        int index = clients.indexOf(client);
        if (index < 0)
            return false;
        return game.getCharacters()[index].isMyTurn();
    }

    public int indexOf(ClientHandler client) {
        return clients.indexOf(client);
    }

    public Game getGame() {
        return game;
    }
}
