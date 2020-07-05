package gameObjects.cards.abilities.targets;

import gameObjects.player.*;

public interface Attackable extends Targetable {
    boolean getHasAttacked();
    void setHasAttacked(boolean hasAttacked);
    int getHealth();
    void setHealth(int health);
    int getAttack(GamePlayer gamePlayer);
}
