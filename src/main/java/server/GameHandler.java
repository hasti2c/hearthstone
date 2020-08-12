package server;

import commands.*;
import elements.heros.*;
import system.game.Character;
import system.game.*;
import system.player.*;

import java.util.*;

import static commands.types.ClientCommandType.INIT_GAME;

public class GameHandler {
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private Game game;

    GameHandler (ClientHandler first, ClientHandler second) {
        clients.add(first);
        clients.add(second);
        first.setOpponent(second);
        second.setOpponent(first);
        createGame();
    }

    private void createGame() {
        GamePlayer firstPlayer = new GamePlayer(clients.get(0), PlayerFaction.FRIENDLY);
        GamePlayer secondPlayer = new GamePlayer(clients.get(1), PlayerFaction.ENEMY);
        game = Game.getInstance(new Character[]{firstPlayer, secondPlayer}, 2, false);

        HeroClass[] heroClasses = new HeroClass[]{firstPlayer.getHero().getHeroClass(), secondPlayer.getHero().getHeroClass()};
        String[] json = new String[]{firstPlayer.getState().getJson(false), secondPlayer.getState().getJson(false)};
        for (ClientHandler client : clients) {
            client.setGame(game);
            client.respond(new Command<>(INIT_GAME, game.getId(), heroClasses[0], heroClasses[1], json[0], json[1]));
        }
    }
}
