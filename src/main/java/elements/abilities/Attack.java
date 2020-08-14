package elements.abilities;

import elements.*;
import elements.abilities.targets.*;
import system.game.characters.Character;

public class Attack extends Ability {
    private AttackType type;
    private int damageAmount;

    @Override
    protected void doAction(Character actionPerformer, Element caller, Element target) {
        if (!(target instanceof Attackable defender))
            return;
        switch (type) {
            case NORMAL -> {
                if (caller instanceof Attackable attacker)
                    actionPerformer.rawAttack(attacker, defender);
            }
            case CONSTANT -> defender.doDamage(actionPerformer, damageAmount);
        }
    }
}

enum AttackType {
    CONSTANT,
    NORMAL
}