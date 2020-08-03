package shared;

import commands.types.CommandType;

import java.util.ArrayList;

public abstract class Controller <T extends CommandType> {
    public abstract ArrayList<?> getObjectsList(String name);
    public abstract String getInitPlayerName();
}