package elements.abilities.targets;

import system.player.GamePlayer;

public interface Attackable extends Targetable {
    boolean getHasAttacked();
    void setHasAttacked(boolean hasAttacked);
    void doDamage(GamePlayer gamePlayer, int damage);
    int getAttack(GamePlayer gamePlayer);
}
