package gameObjects.cards.abilities;

import gameObjects.Playable;
import gameObjects.player.*;
import gameObjects.cards.*;

import static gameObjects.cards.abilities.ChangeStatsType.CONSTANT;

public class ChangeStats extends Ability {
    private ChangeStatsType type;
    private int attackChange, healthChange, durabilityChange;
    private boolean giveTaunt, giveDivineShield;

    @Override
    protected void doAction(GamePlayer actionPerformer, Playable caller, Card target) {
        if (type == CONSTANT) {
            if (target instanceof Minion minion) {
                minion.setAttack(minion.getAttack() + attackChange);
                minion.setHealth(minion.getHealth() + healthChange);
                if (giveTaunt)
                    minion.setTaunt(true);
                if (giveDivineShield)
                    minion.setDivineShield(true);
            } else if (target instanceof Weapon weapon) {
                weapon.setAttack(weapon.getAttack() + attackChange);
                weapon.setDurability(weapon.getDurability() + durabilityChange);
            }
        } else {
            if (target instanceof Minion minion && caller instanceof Minion minionCaller)
                minion.setHealth(minionCaller.getHealth());
        }
    }
}

enum ChangeStatsType {
    CONSTANT,
    COPY_HEALTH
}