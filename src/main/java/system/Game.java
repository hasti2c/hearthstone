package system;

import controllers.game.*;
import elements.heros.*;
import graphics.directories.playground.PlayGround;
import system.player.Character;
import system.player.GamePlayer;
import system.player.NPC;
import system.player.PlayerFaction;

import java.io.*;

public class Game {
    private final GameController controller;
    private final Character[] characters = new Character[2];
    private int id, turn = 0;
    private final int playerCount = 2;
    private Logger logger;
    private Timer timer;
    private int time = 60;
    private PlayGround playGround;

    public Game(GameController controller, int playerCount) {
        this.controller = controller;
        characters[0] = new GamePlayer(controller, this, PlayerFaction.FRIENDLY);
        if (playerCount == 2)
            characters[1] = new GamePlayer(controller, this, PlayerFaction.ENEMY);
        else
            characters[1] = new NPC(controller.getCurrentHero().clone(), controller.getCurrentDeck().clone(), this, PlayerFaction.ENEMY);
        timer = new Timer(this);
    }

    public Game(GameController controller, DeckPair deckPair) {
        this.controller = controller;
        Deck[] decks = deckPair.getDecks();
        characters[0] = new GamePlayer(controller, this, PlayerFaction.FRIENDLY, decks[0]);
        characters[1] = new GamePlayer(controller, this, PlayerFaction.ENEMY, decks[1]);
    }

    public void startGame() {
        id = controller.getGameCount() + 1;
        logger = new Logger("src/main/resources/logs/games/game-" + id + ".txt");
        controller.setGameCount(id);
        characters[0].initialize();
        characters[1].initialize();
        timer.start();
        getCurrentCharacter().startTurn();
    }

    public int getCurrentPlayerNumber() {
        return turn % playerCount;
    }

    public Character getCurrentCharacter() {
        return characters[getCurrentPlayerNumber()];
    }

    public Character getOtherCharacter() {
        return characters[playerCount - 1 - getCurrentPlayerNumber()];
    }

    public Character[] getCharacters() {
        return characters;
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
        getCurrentCharacter().endTurn();
        turn++;
        time = 60;
        getCurrentCharacter().startTurn();
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public String getGameEvents() {
        return logger.getEvents();
    }

    public boolean isFinished() {
        int myHealth = characters[0].getHero().getHealth();
        int opponentHealth = characters[1].getHero().getHealth();
        return myHealth <= 0 || opponentHealth <= 0;
    }

    public void logStartGame() {
        logger.log("GAME_ID: " + id);
        logger.log("STARTED_AT: ", "");
        logger.log("");
        logger.log("p1: " + characters[0]);
        logger.log("p1_hero: " + characters[0].getHero());
        logger.log("p1_deck: " + characters[0].getDeck());
        logger.log("");
        logger.log("p2: " + characters[1]);
        logger.log("p2_hero: " + characters[1].getHero());
        logger.log("p2_deck: " + characters[1].getDeck());
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
        int friendlyHealth = characters[0].getHero().getHealth();
        int enemyHealth = characters[1].getHero().getHealth();
        if (friendlyHealth > 0 && enemyHealth <= 0)
            characters[0].addWin();
    }
}
