package shared;

import commands.*;
import commands.types.*;
import elements.heros.*;
import system.game.*;
import system.game.characters.Character;
import system.player.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

public abstract class Controller <T extends CommandType> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    protected Player currentPlayer;
    protected CommandRunner<T> runner;
    protected CommandParser<T> parser;

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Hero getCurrentHero() {
        if (currentPlayer == null)
            return null;
        return currentPlayer.getInventory().getCurrentHero();
    }

    public Deck getCurrentDeck() {
        if (currentPlayer == null || currentPlayer.getInventory().getCurrentDeck() == null)
            return null;
        return currentPlayer.getInventory().getCurrentDeck();
    }

    public ArrayList<?> getObjectsList(String name) {
        return switch (name) {
            case "Deck": yield currentPlayer.getInventory().getAllDecks();
            case "Card": yield GameData.getInstance().getCardsList();
            case "MyElement": yield getMyCharacter().getElements();
            case "EnemyElement": yield getMyCharacter().getOpponent().getElements();
            default: yield new ArrayList<>();
        };
    }

    public static String getTime() {
        return dtf.format(LocalDateTime.now());
    }

    public CommandRunner<T> getRunner() {
        return runner;
    }

    public CommandParser<T> getParser() {
        return parser;
    }

    public Game getGame() {
        if (currentPlayer == null)
            return null;
        return currentPlayer.getGame();
    }

    public void setGame(Game game) {
        if (currentPlayer == null)
            return;
        currentPlayer.setGame(game);
    }

    protected abstract Character getMyCharacter();
}