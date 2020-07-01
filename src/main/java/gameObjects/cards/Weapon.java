package gameObjects.cards;

public class Weapon extends Card {
    private int durability;
    private int attack;

    public Weapon() {}

    Card cloneHelper() {
        Weapon c = new Weapon();
        c.durability = durability;
        c.attack = attack;
        return c;
    }

    public int getDurability() {
        return durability;
    }

    public int getAttack() {
        return attack;
    }
}
