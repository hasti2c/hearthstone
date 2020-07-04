package gameObjects.heros;

import controllers.game.*;
import gameObjects.*;

public class HeroPower implements Configable {
    private String name;
    private int mana;

    public String toString() {
        return name;
    }

    public int getMana() {
        return mana;
    }

    @Override
    public void initialize(GameController controller) {}

    @Override
    public String getJsonPath(GameController controller, String name) {
        return "hero powers/";
    }
}
