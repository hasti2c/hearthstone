package system.game;

import elements.cards.*;
import elements.heros.*;
import shared.*;
import system.*;
import system.game.characters.Character;
import system.game.characters.*;
import system.player.*;

import java.util.*;

import static system.game.GameEndingType.*;

public class Game {
    private final Character[] characters;
    private final int id;
    private int turn = 0;
    private final int playerCount = 2;
    private final Logger logger;
    private final GameType type;
    private GameEndingType gameEndingType;
    private final GameConfigor gameConfigor;

    public Game(GameType type, Character[] characters, int id) throws NoSuchMethodException, ClassNotFoundException {
        this.characters = characters;
        characters[0].setGame(this);
        characters[1].setGame(this);
        this.id = id;
        this.type = type;
        logger = new Logger("src/main/resources/logs/games/game-" + id + ".txt");
        gameConfigor = GameConfigor.getInstance(this);
    }

    public Game(Controller<?> controller, GameType type, int id, ArrayList<HeroClass> heroClasses, ArrayList<String> json) throws NoSuchMethodException, ClassNotFoundException {
        this(type, new GamePlayer[]{new GamePlayer(controller, heroClasses.get(0), json.get(0), PlayerFaction.FRIENDLY, id), new GamePlayer(controller, heroClasses.get(1), json.get(1), PlayerFaction.ENEMY, id)}, id);
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
        if (friendlyHealth > enemyHealth)
            endGame(FRIENDLY_WIN);
        else if (enemyHealth > friendlyHealth)
            endGame(TIE);
        else
            endGame(FRIENDLY_LOSS);
    }

    public void endGame(GameEndingType gameEndingType) {
        if (gameEndingType.getWinnerIndex() >= 0)
            characters[gameEndingType.getWinnerIndex()].addWin();
        this.gameEndingType = gameEndingType;
    }

    public GameEndingType getGameEndingType() {
        return gameEndingType;
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
        String[] ret = new String[playerCount];
        for (int i = 0; i < playerCount; i++)
            ret[i] = characters[i].getState().getJson(false);
        return ret;
    }

    public String[] getJsons(int index) {
        String[] ret = new String[playerCount];
        for (int i = 0; i < playerCount; i++)
            if (i == index)
                ret[i] = characters[i].getState().getJson(false);
            else
                ret[i] = characters[i].getState().getHiddenJson(false);
        return ret;
    }

    public int indexOf(Character character) {
        for (int i = 0; i < characters.length; i++)
            if (character == characters[i])
                return i;
        return -1;
    }

    public GameConfigor getConfigor() {
        return gameConfigor;
    }
}

