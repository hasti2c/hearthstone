package system;

import controllers.game.*;

public interface Configable {
    void initialize(GameController controller);
    String getJsonPath(GameController controller, String name);
}
