package gameObjects;

import controllers.game.GameController;

public interface Configable {
    void initialize(GameController controller);
    String getJsonPath(GameController controller, String name);
}
