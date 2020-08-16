package system.game.configor;

import elements.abilities.targets.Attackable;
import system.game.Game;
import system.game.GameConfigor;
import system.game.characters.Character;

public class OneShot extends GameConfigor {
    public OneShot(Game game) {
        super(game);
    }

    @Override
    public int getAttack(Attackable damager, Attackable damaged, Character damagerOwner) {
        return damaged.getHealth();
    }
}
