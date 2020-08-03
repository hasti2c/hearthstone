package client;

import javafx.stage.*;
import server.*;
import commands.*;
import commands.types.*;

public class Client extends NetworkMember<ClientCommandType> {
    private final ClientController graphics;

    public Client(Server target, Stage stage) {
        super(new ClientController(), target);
        runner = new ClientCommandRunner();
        parser = new CommandParser<>(controller, ClientCommandType.class);

        graphics = new ClientController(this, stage);
        target.setClient(this);
        graphics.start();
    }

    public void endGame() {
        graphics.endGame();
    }
}
