package client;

import javafx.stage.*;
import server.*;
import commands.*;
import commands.types.*;

public class Client extends NetworkMember<ClientCommandType> {
    public Client(Server target, Stage stage) {
        super(target);
        runner = new ClientCommandRunner();
        parser = new CommandParser<>(controller, ClientCommandType.class);

        controller = new ClientController(this, stage);
        target.setClient(this);
        ((ClientController) controller).start();
    }

    public void endGame() {
        ((ClientController) controller).endGame();
    }
}
