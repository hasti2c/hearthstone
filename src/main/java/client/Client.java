package client;

import client.graphics.GraphicsController;
import javafx.stage.Stage;
import server.Controller;
import server.Server;
import shared.commands.Command;
import shared.commands.NetworkMember;
import shared.commands.types.ClientCommandType;

public class Client extends NetworkMember<ClientCommandType> {
    private final GraphicsController graphics;

    public Client(Server target, Stage stage) {
        super(target);
        graphics = new GraphicsController(this, Controller.getInstance(), stage);
        target.setClient(this);
        graphics.start();
    }

    public void endGame() {
        graphics.endGame();
    }
}
