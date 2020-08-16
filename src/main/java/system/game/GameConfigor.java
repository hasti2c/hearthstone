package system.game;

import elements.*;
import elements.abilities.targets.*;
import shared.*;
import system.game.characters.Character;

import java.lang.reflect.*;

import static system.game.GameType.*;

public class GameConfigor {
    private Game game;

    protected GameConfigor(Game game) {
        this.game = game;
    }

    public static GameConfigor getInstance(Game game) throws NoSuchMethodException, ClassNotFoundException {
        if (game.getType() != TAVERN_BRAWL)
            return new GameConfigor(game);
        Class<? extends GameConfigor> configorClass = Methods.loadClass("TavernBrawl");
        Constructor<? extends GameConfigor> constructor = configorClass.getDeclaredConstructor(Game.class);
        constructor.setAccessible(true);
        try {
            return constructor.newInstance(game);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getTurnMana(Character character) {
        return Math.min(character.getTurnCount(), 10);
    }

    public int getMana(Playable playable) {
        return playable.getMana();
    }

    public int getHandCap(Character character) {
        return 12;
    }

    public int getMinionsCap(Character character) {
        return 7;
    }

    public int getAttack(Attackable damager, Attackable damaged, Character damagerOwner) {
        return damager.getAttack(damagerOwner);
    }
}
