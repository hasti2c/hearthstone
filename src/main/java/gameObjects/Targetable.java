package gameObjects;

import gameObjects.Player.GamePlayer;

public interface Targetable {
    boolean getHasAttacked();
    void setHasAttacked(boolean hasAttacked);
    int getHealth();
    void setHealth(int health);
    int getAttack(GamePlayer gamePlayer);
}
