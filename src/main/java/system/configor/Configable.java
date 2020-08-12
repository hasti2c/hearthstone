package system.configor;

public interface Configable {
    void initialize(String initPlayerName);
    String getName();
    String getJsonPath(String name, String initPlayerName);
}
