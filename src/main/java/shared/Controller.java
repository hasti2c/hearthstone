package shared;

import commands.types.CommandType;
import elements.heros.Deck;
import elements.heros.Hero;
import system.player.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public abstract class Controller <T extends CommandType> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    protected Player currentPlayer;

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Hero getCurrentHero() {
        if (currentPlayer == null || currentPlayer.getInventory().getCurrentHero() == null)
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
            case "Attackable|mine": yield currentPlayer.getGame().getCharacters()[0].getAttackables();
            case "Attackable|opponent": yield currentPlayer.getGame().getCharacters()[1].getAttackables();
            default: yield new ArrayList<>();
        };
    }

    public abstract String getInitPlayerName();

    public static String getTime() {
        return dtf.format(LocalDateTime.now());
    }
}