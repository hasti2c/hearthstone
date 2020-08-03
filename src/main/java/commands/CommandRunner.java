package commands;

import commands.types.*;

public abstract class CommandRunner <T extends CommandType> {
    public abstract boolean run(Command<T> command);
}
