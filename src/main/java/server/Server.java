package server;

import client.*;
import commands.*;
import commands.types.*;

public class Server extends NetworkMember<ServerCommandType> {
    public Server() {
        super(ServerController.getInstance());
    }

    public void setClient(Client client) {
        this.target = client;
        runner = new ServerCommandRunner((ServerController) controller, client);
        parser = new CommandParser<>(controller, ServerCommandType.class);
    }
}
