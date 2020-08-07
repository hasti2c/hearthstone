package client;

import commands.*;
import commands.types.*;
import javafx.application.*;

public class ClientCommandRunner extends CommandRunner<ClientCommandType> {
    private final ClientController controller;

    public ClientCommandRunner(ClientController controller) {
        this.controller = controller;
    }

    @Override
    public boolean run(Command<ClientCommandType> command) {
        ClientCommandType type = command.getCommandType();
        Object[] input = command.getInput();

        return switch (type) {
            case RESULT -> {
                if (!(input[0] instanceof ServerCommandType serverType))
                    yield false;
                yield handleResponse(serverType, input);
            }
            case UPDATE_PLAYER -> {
                if (input[0].equals("null")) {
                    controller.setCurrentPlayer(null);
                    yield true;
                }
                if (!(input[0] instanceof String username && input[1] instanceof String json))
                    yield false;
                controller.updatePlayer(username, json);
                yield true;
            }
        };
    }

    private boolean handleResponse(ServerCommandType serverType, Object[] input) {
        if (!(input[1] instanceof Boolean bool))
            return false;
        switch (serverType) {
            case SIGN_UP -> Platform.runLater(() -> controller.signUpResult(bool));
            case LOGIN -> Platform.runLater(() -> controller.loginResult(bool));
            case ADD_CARD -> Platform.runLater(() -> controller.addCardResult(bool));
            case ADD_DECK, RENAME -> Platform.runLater(() -> controller.deckNameResult(bool));
            case MOVE -> Platform.runLater(() -> controller.moveDeckResult(bool));
        }
        return true;
    }
}
