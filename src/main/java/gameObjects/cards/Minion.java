package gameObjects.cards;

public class Minion extends Card {
    private int health;
    private int attack;

    public Minion() {}

    Card cloneHelper() {
        Minion c = new Minion();
        c.health = health;
        c.attack = attack;
        return c;
    }

    public int getHP() {
        return health;
    }

    public int getAttack() {
        return attack;
    }
}
