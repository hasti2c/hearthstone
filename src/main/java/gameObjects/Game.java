package gameObjects;

import controllers.game.GameController;
import gameObjects.Player.GamePlayer;
import gameObjects.Player.PlayerFaction;
import gameObjects.cards.*;
import gameObjects.heros.Deck;
import gameObjects.heros.DeckPair;

import java.io.FileWriter;
import java.io.IOException;

public class Game {
    private GameController controller;
    private GamePlayer[] gamePlayers = new GamePlayer[2];
    private int id, turn = 0;
    private final int playerCount = 2;
    private FileWriter logWriter;
    private String gameEvents = "";
    private Passive passive;

    public Game(GameController controller) {
        this.controller = controller;
        gamePlayers[0] = new GamePlayer(controller, this, PlayerFaction.FRIENDLY);
        gamePlayers[1] = new GamePlayer(controller, this, PlayerFaction.ENEMY);
    }

    public Game(GameController controller, DeckPair deckPair) {
        this.controller = controller;
        Deck[] decks = deckPair.getDecks();
        gamePlayers[0] = new GamePlayer(controller, this, PlayerFaction.FRIENDLY, decks[0]);
        gamePlayers[1] = new GamePlayer(controller, this, PlayerFaction.ENEMY, decks[1]);
    }

    public void startGame() {
        id = controller.getGameCount() + 1;
        try {
            logWriter = new FileWriter(getLogPath(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller.setGameCount(id);
        getCurrentPlayer().startTurn();
    }

    public GamePlayer getCurrentPlayer() {
        return gamePlayers[turn % playerCount];
    }

    public GamePlayer[] getGamePlayers() {
        return gamePlayers;
    }

    public int getId() {
        return id;
    }

    public int getTurn() {
        return turn;
    }

    public void nextTurn() {
        turn++;
        getCurrentPlayer().startTurn();
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public String getGameEvents() {
        return gameEvents;
    }

    public String getLogPath() {
        return "src/main/resources/logs/games/game-" + id + ".txt";
    }

    public void log(String line) {
        try {
            logWriter.write(line + "\n");
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String type, String details) {
        try {
            if (!"STARTED_AT: ".equals(type) && !"ENDED_AT: ".equals(details))
                gameEvents += type + " " + details + "\n";
            logWriter.write(type + " " + GameController.getTime() + " " + details + "\n");
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
