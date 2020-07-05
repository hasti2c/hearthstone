package gameObjects.cards.abilities.targets;

import gameObjects.player.*;

public interface Attackable extends Targetable {
    boolean getHasAttacked();
    void setHasAttacked(boolean hasAttacked);
    void doDamage(int damage);
    int getAttack(GamePlayer gamePlayer);
}
