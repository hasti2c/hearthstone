package gameObjects.cards;

import gameObjects.cards.abilities.targets.Attackable;
import gameObjects.player.*;

public class Minion extends Card implements Attackable {
    private int health;
    private int attack;
    private boolean hasAttacked = false;
    private boolean taunt, asleep = true, rush, divineShield;

    public Minion() {}

    Card cloneHelper() {
        Minion c = new Minion();
        c.health = health;
        c.attack = attack;
        c.hasAttacked = hasAttacked;
        c.taunt = taunt;
        c.asleep = asleep;
        c.rush = rush;
        c.divineShield = divineShield;
        return c;
    }

    public void doDamage(int damage) {
        if (divineShield) {
            divineShield = false;
            return;
        }
        health -= damage;
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

    public boolean getTaunt() {
        return taunt;
    }

    public void setTaunt(boolean taunt) {
        this.taunt = taunt;
    }

    public boolean getAsleep() {
        return asleep;
    }

    public void setAsleep(boolean asleep) {
        this.asleep = asleep;
    }

    public boolean getRush() {
        return rush;
    }

    public void setRush(boolean rush) {
        this.rush = rush;
    }

    public boolean getDivineShield() {
        return divineShield;
    }

    public void setDivineShield(boolean divineShield) {
        this.divineShield = divineShield;
    }
}
