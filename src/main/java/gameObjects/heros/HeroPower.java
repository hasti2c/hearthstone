package gameObjects.heros;

public class HeroPower {
    private String name;
    private Hero hero;
    private int mana;

    HeroPower(String name, int mana, Hero hero) {
        this.name = name;
        this.mana = mana;
        this.hero = hero;
    }

    public String toString() {
        return name;
    }
}
