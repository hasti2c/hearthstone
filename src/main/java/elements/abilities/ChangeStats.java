package elements.abilities;

import elements.*;
import elements.heros.*;
import elements.cards.*;
import system.player.Character;

import static elements.abilities.ChangeStatsType.CONSTANT;

public class ChangeStats extends Ability {
    private ChangeStatsType type;
    private int attackChange, healthChange, durabilityChange;
    private boolean giveTaunt, giveDivineShield;

    @Override
    protected void doAction(Character actionPerformer, Element caller, Element target) {
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
            } else if (target instanceof Hero hero)
                hero.setHealth(hero.getHealth() + healthChange);
        } else {
            if (target instanceof Minion minion && caller instanceof Minion minionCaller)
                minion.setHealth(minionCaller.getHealth());
        }
    }

    public boolean onlyHeals() {
        return type == CONSTANT && attackChange == 0 && durabilityChange == 0 && !giveDivineShield && !giveTaunt && healthChange > 0;
    }
}

enum ChangeStatsType {
    CONSTANT,
    COPY_HEALTH
}