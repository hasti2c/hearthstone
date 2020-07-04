package gameObjects.cards;

import gameObjects.player.*;
import graphics.directories.playground.targets.*;

public class Minion extends Card implements Targetable {
    private int health;
    private int attack;
    private boolean hasAttacked = false;

    public Minion() {}

    Card cloneHelper() {
        Minion c = new Minion();
        c.health = health;
        c.attack = attack;
        return c;
    }

    public boolean getHasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttack(GamePlayer gamePlayer) {
        return attack;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }
}
