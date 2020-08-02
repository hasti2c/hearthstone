package server;

import client.Client;
import shared.commands.CommandParser;
import shared.commands.NetworkMember;
import shared.commands.types.ServerCommandType;

public class Server extends NetworkMember<ServerCommandType> {
    private final Controller controller;

    public Server() {
        this.controller = Controller.getInstance();
    }

    public void setClient(Client client) {
        this.target = client;
        runner = new ServerCommandRunner(controller, client);
        parser = new CommandParser<>(controller, ServerCommandType.class);
    }
}
