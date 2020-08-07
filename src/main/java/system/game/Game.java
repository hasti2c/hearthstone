package system.game;

import elements.cards.*;
import elements.heros.*;
import shared.*;
import system.*;
import system.player.*;

import java.util.*;

public class Game {
    private final Character[] characters = new Character[2];
    private final int id;
    private int turn = 0;
    private final int playerCount = 2;
    private final Logger logger;
    private final boolean deckReader;

    public Game(Controller<?> controller, int playerCount, int id) {
        characters[0] = new GamePlayer(controller, this, PlayerFaction.FRIENDLY);
        if (playerCount == 2)
            characters[1] = new GamePlayer(controller, this, PlayerFaction.ENEMY);
        else
            characters[1] = new NPC(controller.getCurrentHero().clone(), controller.getCurrentDeck().clone(), this, PlayerFaction.ENEMY);
        this.id = id;
        logger = new Logger("src/main/resources/logs/games/game-" + id + ".txt");
        deckReader = false;
    }

    public Game(Controller<?> controller, DeckPair deckPair, int id) {
        Deck[] decks = deckPair.getDecks();
        characters[0] = new GamePlayer(controller, this, PlayerFaction.FRIENDLY, decks[0]);
        characters[1] = new GamePlayer(controller, this, PlayerFaction.ENEMY, decks[1]);
        this.id = id;
        logger = new Logger("src/main/resources/logs/games/game-" + id + ".txt");
        deckReader = true;
    }

    public void startGame(ArrayList<Card> cards) {
        characters[0].initialize(cards);
        characters[1].initialize(cards);
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

    public void nextTurn() {
        getCurrentCharacter().endTurn();
        turn++;
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

    public void endGame() {
        int friendlyHealth = characters[0].getHero().getHealth();
        int enemyHealth = characters[1].getHero().getHealth();
        if (friendlyHealth > 0 && enemyHealth <= 0)
            characters[0].addWin();
    }

    public boolean isDeckReader() {
        return deckReader;
    }
}
