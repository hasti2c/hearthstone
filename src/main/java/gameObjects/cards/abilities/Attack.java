package gameObjects.cards.abilities;

import gameObjects.Playable;
import gameObjects.cards.abilities.targets.Attackable;
import gameObjects.player.*;
import gameObjects.cards.*;
import static gameObjects.cards.abilities.AttackType.*;

public class Attack extends Ability {
    private AttackType type;
    private int damageAmount;

    @Override
    protected void doAction(GamePlayer actionPerformer, Playable caller, Element target) {
        if (!(target instanceof Attackable defender))
            return;
        switch (type) {
            case NORMAL -> {
                if (caller instanceof Attackable attacker)
                    actionPerformer.rawAttack(attacker, defender);
            }
            case CONSTANT -> defender.doDamage(damageAmount);
        }
    }
}

enum AttackType {
    CONSTANT,
    NORMAL
}