package commands;

import commands.types.*;

public abstract class CommandRunner <T extends CommandType> {
    //TODO void
    public abstract void run(Command<T> command);
}
