package system.game;

import elements.cards.*;
import elements.heros.*;
import shared.*;
import system.*;
import system.game.characters.Character;
import system.game.characters.*;
import system.player.*;

import java.util.*;

public class Game {
    private final Character[] characters;
    private final int id;
    private int turn = 0;
    private final int playerCount = 2;
    private final Logger logger;
    private final GameType type;

    public Game(GameType type, Character[] characters, int id) {
        this.characters = characters;
        characters[0].setGame(this);
        characters[1].setGame(this);
        this.id = id;
        this.type = type;
        logger = new Logger("src/main/resources/logs/games/game-" + id + ".txt");
    }

    public Game(Controller<?> controller, GameType type, int id, ArrayList<HeroClass> heroClasses, ArrayList<String> json) {
        characters = new Character[2];
        characters[0] = new GamePlayer(controller, heroClasses.get(0), json.get(0), PlayerFaction.FRIENDLY, id);
        characters[1] = new GamePlayer(controller, heroClasses.get(1), json.get(1), PlayerFaction.ENEMY, id);
        characters[0].setGame(this);
        characters[1].setGame(this);
        this.id = id;
        this.type = type;
        logger = new Logger("src/main/resources/logs/games/game-" + id + ".txt");
    }

    public void startGame(ArrayList<ArrayList<Card>> cards) {
        for (int i = 0; i < characters.length && i < cards.size(); i++)
            characters[i].initialize(cards.get(i));
        getCurrentCharacter().startTurn();
    }

    public int getCurrentPlayerNumber() {
        return turn % playerCount;
    }

    public Character getCurrentCharacter() {
        return characters[getCurrentPlayerNumber()];
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

    public void updateState(ArrayList<String> jsons) {
        for (int i = 0; i < playerCount; i++)
            characters[i].updateState(jsons.get(i));
    }

    public void doEndTurn() {
        turn++;
    }

    public GameType getType() {
        return type;
    }

    public String[] getJsons() {
        String[] ret = new String[2];
        for (int i = 0; i < 2; i++)
            ret[i] = characters[i].getState().getJson(false);
        return ret;
    }
}
