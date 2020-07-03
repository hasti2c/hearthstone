package gameObjects.cards.abilities;

import gameObjects.cards.Card;
import gameObjects.cards.Minion;

import static gameObjects.cards.abilities.ChangeStatsType.CONSTANT;
import static gameObjects.cards.abilities.ChangeStatsType.COPY_HEALTH;

public class ChangeStats extends Ability {
    private ChangeStatsType type;
    private int attackChange, healthChange;

    @Override
    protected void doAction(Card target) {
        if (!(target instanceof Minion minion) || type == COPY_HEALTH)
            return;
        minion.setAttack(minion.getAttack() + attackChange);
        minion.setHealth(minion.getHealth() + healthChange);
    }
}

enum ChangeStatsType {
    CONSTANT,
    COPY_HEALTH
}