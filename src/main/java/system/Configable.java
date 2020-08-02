package system;

import server.Controller;

public interface Configable {
    void initialize(Controller controller);
    String getJsonPath(Controller controller, String name);
}
