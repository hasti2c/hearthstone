package shared;

import commands.types.*;

import java.util.*;

public abstract class Controller <T extends CommandType> {
    public abstract ArrayList<?> getObjectsList(String name);
}
