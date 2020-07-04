package controllers.commands;

public class Command {
    private final CommandType commandType;
    private final Object[] input;

    public Command(CommandType commandType, Object ...input) {
        this.commandType = commandType;
        this.input = input;
    }

    CommandType getCommandType() {
        return commandType;
    }

    Object[] getInput() {
        return input;
    }
}
