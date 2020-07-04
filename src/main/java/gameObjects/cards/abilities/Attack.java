package gameObjects.cards.abilities;

import gameObjects.player.*;
import gameObjects.cards.*;
import static gameObjects.cards.abilities.AttackType.*;

public class Attack extends Ability {
    private AttackType type;
    private int damageAmount;

    @Override
    protected void doAction(GamePlayer gamePlayer, Card caller, Card target) {
        if (!(caller instanceof Minion targeter && target instanceof Minion defender))
            return;
        if (type.equals(NORMAL))
            gamePlayer.rawAttack(targeter, defender);
        else
            defender.setHealth(defender.getHealth() - damageAmount);
    }
}

enum AttackType {
    CONSTANT,
    NORMAL
}