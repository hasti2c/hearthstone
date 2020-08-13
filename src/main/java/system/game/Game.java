package system.game;

import elements.cards.*;
import elements.heros.*;
import shared.*;
import system.*;
import system.player.*;

import java.util.*;

public class Game {
    private final Character[] characters = new Character[2];
    private int id;
    private int turn = 0;
    private final int playerCount = 2;
    private Logger logger;
    private boolean deckReader;

    public static Game getInstance(Character[] characters, int id, boolean deckReader) {
        Game game = new Game();
        game.characters[0] = characters[0];
        game.characters[1] = characters[1];
        characters[0].setGame(game);
        characters[1].setGame(game);
        game.id = id;
        game.deckReader = deckReader;
        game.logger = new Logger("src/main/resources/logs/games/game-" + id + ".txt");
        return game;
    }

    public static Game getInstance(Controller<?> controller, int playerCount, int id) {
        Character[] characters = new Character[2];
        characters[0] = new GamePlayer(controller, PlayerFaction.FRIENDLY);
        if (playerCount == 2)
            characters[1] = new GamePlayer(controller, PlayerFaction.ENEMY);
        else
            characters[1] = new NPC(controller.getCurrentHero().clone(), controller.getCurrentDeck().clone(), PlayerFaction.ENEMY);
        return getInstance(characters, id, false);
    }

    public static Game getInstance(Controller<?> controller, DeckPair deckPair, int id) {
        Deck[] decks = deckPair.getDecks();
        Character[] characters = new Character[2];
        characters[0] = new GamePlayer(controller, PlayerFaction.FRIENDLY, decks[0]);
        characters[1] = new GamePlayer(controller, PlayerFaction.ENEMY, decks[1]);
        return getInstance(characters, id, true);
    }

    public static Game getInstance(Controller<?> controller, int playerCount, int id, ArrayList<HeroClass> heroClasses, ArrayList<String> json) {
        Character[] characters = new Character[2];
        characters[0] = new GamePlayer(controller, heroClasses.get(0), json.get(0), PlayerFaction.FRIENDLY, id);
        characters[1] = new GamePlayer(controller, heroClasses.get(1), json.get(1), PlayerFaction.ENEMY, id);
        return getInstance(characters, id, false);
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

    public void updateState(String[] jsons) {
        for (int i = 0; i < playerCount; i++)
            characters[i].updateState(jsons[i]);
    }
}
