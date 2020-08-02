package shared.commands;

import shared.commands.types.CommandType;

public abstract class CommandRunner <T extends CommandType> {
    public abstract boolean run(Command<T> command);
}
