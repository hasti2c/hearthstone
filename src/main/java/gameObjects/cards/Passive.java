package gameObjects.cards;

import controllers.game.GameController;
import gameObjects.Playable;

import static gameObjects.cards.ElementType.PASSIVE;

public class Passive extends Playable {
    public Passive(String name) {
        this.name = name;
        elementType = PASSIVE;
    }

    @Override
    protected String getImagePath() {
        return null;
    }

    @Override
    protected String getFullImagePath() {
        return null;
    }

    @Override
    public String getJsonPath(GameController controller, String name) {
        return null;
    }
}
