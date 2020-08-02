package elements.cards;

import server.Controller;
import elements.abilities.targets.Attackable;
import shared.GameData;
import system.player.Character;

import static elements.ElementType.MINION;

public class Minion extends Card implements Attackable {
    private int health;
    private int attack;
    private boolean hasAttacked = false;
    private boolean taunt, asleep = true, rush, charge, divineShield;

    public Minion() {
        elementType = MINION;
    }

    @Override
    public void initialize(Controller controller) {
        super.initialize(controller);
        setAsleep(true);
    }

    Card cloneHelper() {
        Minion c = new Minion();
        c.health = health;
        c.attack = attack;
        c.hasAttacked = hasAttacked;
        c.taunt = taunt;
        c.rush = rush;
        c.charge = charge;
        c.divineShield = divineShield;
        c.setAsleep(true);
        return c;
    }

    Card copyHelper() {
        Minion c = new Minion();
        c.health = health;
        c.attack = attack;
        return c;
    }

    public void doDamage(Character character, int damage) {
        if (divineShield) {
            divineShield = false;
            return;
        }
        health -= damage;
        character.doCardAction("doActionOnDamaged", this);
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

    public int getAttack(Character character) {
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
        if (charge)
            this.asleep = false;
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

    public void restore() {
        Minion minion = (Minion) GameData.getInstance().getCard(name);
        health = minion.health;
    }

    @Override
    public int compareTo(Card card) {
        int n = mana - card.getMana();
        if (n != 0)
            return n;
        if (!(card instanceof Minion minion))
            return 1;
        n = attack - minion.attack;
        if (n != 0)
            return n;
        n = health - minion.health;
        return n;
    }
}
