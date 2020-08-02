package server;

import client.Client;
import client.graphics.GraphicsController;
import server.commands.CommandParser;
import server.commands.CommandRunner;

public class Server {
    private final Controller controller;
    private CommandRunner runner;
    private CommandParser parser;

    public Server() {
        this.controller = Controller.getInstance();
    }

    public void setClient(Client client) {
        runner = new CommandRunner(controller, client);
        parser = new CommandParser(controller);
    }

    public boolean runCommand(String message) {
        return runner.run(parser.parse(message));
    }
}
