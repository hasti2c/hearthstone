package client;

import client.graphics.GraphicsController;
import javafx.stage.Stage;
import server.Controller;
import server.Server;
import server.commands.Command;

public class Client {
    private final Server server;
    private final GraphicsController graphics;

    public Client(Server server, Stage stage) {
        this.server = server;
        graphics = new GraphicsController(this, Controller.getInstance(), stage);
        server.setClient(this);
        graphics.start();
    }

    public boolean runCommand(Command command) {
        return server.runCommand(command.toString());
    }

    public void endGame() {
        graphics.endGame();
    }
}
