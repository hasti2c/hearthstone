package system;

import server.*;

public interface Configable {
    void initialize(ServerController controller);
    String getJsonPath(ServerController controller, String name);
}
