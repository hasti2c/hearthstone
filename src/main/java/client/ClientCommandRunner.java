package client;

import commands.*;
import commands.types.*;

public class ClientCommandRunner extends CommandRunner<ClientCommandType> {
    @Override
    public boolean run(Command<ClientCommandType> command) {
        return false;
    }
}
