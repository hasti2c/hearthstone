package system;

import controllers.game.*;
import elements.heros.*;
import graphics.directories.playground.PlayGround;
import system.player.GamePlayer;
import system.player.PlayerFaction;

import java.io.*;

public class Game {
    private final GameController controller;
    private final GamePlayer[] gamePlayers = new GamePlayer[2];
    private int id, turn = 0;
    private final int playerCount = 2;
    private Logger logger;
    private Timer timer;
    private int time = 60;
    private PlayGround playGround;

    public Game(GameController controller) {
        this.controller = controller;
        gamePlayers[0] = new GamePlayer(controller, this, PlayerFaction.FRIENDLY);
        gamePlayers[1] = new GamePlayer(controller, this, PlayerFaction.ENEMY);
        timer = new Timer(this);
    }

    public Game(GameController controller, DeckPair deckPair) {
        this.controller = controller;
        Deck[] decks = deckPair.getDecks();
        gamePlayers[0] = new GamePlayer(controller, this, PlayerFaction.FRIENDLY, decks[0]);
        gamePlayers[1] = new GamePlayer(controller, this, PlayerFaction.ENEMY, decks[1]);
    }

    public void startGame() {
        id = controller.getGameCount() + 1;
        logger = new Logger("src/main/resources/logs/games/game-" + id + ".txt");
        controller.setGameCount(id);
        gamePlayers[0].initialize();
        gamePlayers[1].initialize();
        gamePlayers[0].getInventory().getCurrentDeck().addGame();
        timer.start();
        getCurrentPlayer().startTurn();
    }

    public int getCurrentPlayerNumber() {
        return turn % playerCount;
    }

    public GamePlayer getCurrentPlayer() {
        return gamePlayers[getCurrentPlayerNumber()];
    }

    public GamePlayer getOtherPlayer() {
        return gamePlayers[playerCount - 1 - getCurrentPlayerNumber()];
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

    public void setPlayGround(PlayGround playGround) {
        this.playGround = playGround;
    }

    public void nextTurn() {
        getCurrentPlayer().endTurn();
        turn++;
        time = 60;
        getCurrentPlayer().startTurn();
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public String getGameEvents() {
        return logger.getEvents();
    }

    public boolean isFinished() {
        int myHealth = gamePlayers[0].getHero().getHealth();
        int opponentHealth = gamePlayers[1].getHero().getHealth();
        return myHealth <= 0 || opponentHealth <= 0;
    }

    public void logStartGame() {
        logger.log("GAME_ID: " + id);
        logger.log("STARTED_AT: ", "");
        logger.log("");
        logger.log("p1: " + gamePlayers[0]);
        logger.log("p1_hero: " + gamePlayers[0].getHero());
        logger.log("p1_deck: " + gamePlayers[0].getInventory().getCurrentDeck());
        logger.log("");
        logger.log("p2: " + gamePlayers[1]);
        logger.log("p2_hero: " + gamePlayers[1].getHero());
        logger.log("p2_deck: " + gamePlayers[1].getInventory().getCurrentDeck());
        logger.log("");
    }

    public void logEndGame() {
        logger.log("");
        logger.log("ENDED_AT: ", "");
    }

    public void log(String type, String details) {
        logger.log("p" + (getCurrentPlayerNumber() + 1) + ":" + type, details);
    }

    public void nextSecond() {
        time--;
        if (time <= 0)
            nextTurn();
        playGround.updateTime();
    }

    public int getTime() {
        return time;
    }

    public void endGame() {
        int friendlyHealth = gamePlayers[0].getHero().getHealth();
        int enemyHealth = gamePlayers[1].getHero().getHealth();
        if (friendlyHealth > 0 && enemyHealth <= 0)
            gamePlayers[0].getInventory().getCurrentDeck().addWin();
    }
}
