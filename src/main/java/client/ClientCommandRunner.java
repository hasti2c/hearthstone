package client;

import shared.commands.Command;
import shared.commands.CommandRunner;
import shared.commands.types.ClientCommandType;

public class ClientCommandRunner extends CommandRunner<ClientCommandType> {
    @Override
    public boolean run(Command<ClientCommandType> command) {
        return false;
    }
}
