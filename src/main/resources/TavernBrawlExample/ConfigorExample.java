import elements.Playable;
import system.game.Game;
import system.game.GameConfigor;
import system.game.characters.Character;

import java.util.HashMap;

public class ConfigorExample extends GameConfigor {
    private final HashMap<Character, Integer> lastMana = new HashMap<>();

    public ConfigorExample(Game game) {
        super(game);
        for (Character character : game.getCharacters())
            lastMana.put(character, 0);
    }

    @Override
    public int getTurnMana(Character character) {
        //type 1 & 2 & 4
        int turn = character.getTurnCount(), ret;
        if (turn == 1)
            ret = 1;
        else
            ret = lastMana.get(character) + character.getState().getMinionsInGame().size();
        lastMana.replace(character, ret);
        return Math.min(ret, character.getHero().getHealth());
    }

    @Override
    public int getMana(Playable playable) {
        //type 3
        return playable.getMana() % 4;
    }

    @Override
    public int getHandCap(Character character) {
        //type 6
        return character.getState().getLeftInDeck().size();
    }

    @Override
    public int getMinionsCap(Character character) {
        //type 7
        return character.getState().getHand().size();
    }
}
