package gameObjects.cards.abilities;

import gameObjects.player.*;
import gameObjects.cards.*;

import static gameObjects.cards.abilities.ChangeStatsType.CONSTANT;

public class ChangeStats extends Ability {
    private ChangeStatsType type;
    private int attackChange, healthChange;

    @Override
    protected void doAction(GamePlayer gamePlayer, Card caller, Card target) {
        if (!(target instanceof Minion minion))
            return;
        if (type == CONSTANT) {
            minion.setAttack(minion.getAttack() + attackChange);
            minion.setHealth(minion.getHealth() + healthChange);
        } else {
            minion.setHealth(((Minion) caller).getHealth());
        }
    }
}

enum ChangeStatsType {
    CONSTANT,
    COPY_HEALTH
}