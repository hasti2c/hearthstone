package client;

import commands.*;
import commands.types.*;

public class ClientCommandRunner extends CommandRunner<ClientCommandType> {
    private final ClientController controller;

    public ClientCommandRunner(ClientController controller) {
        this.controller = controller;
    }

    @Override
    public boolean run(Command<ClientCommandType> command) {
        ClientCommandType type = command.getCommandType();
        Object[] input = command.getInput();

        boolean ret = switch (type) {
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
            case UPDATE_GAME -> {
                if (controller.getCurrentPlayer() == null)
                    yield false;
                if (input[0].equals("null")) {
                    controller.getCurrentPlayer().setGame(null);
                    yield true;
                }
                if (!(input[0] instanceof Integer num && input[1] instanceof String json))
                    yield false;
                controller.getGame().getCharacters()[num].updateState(json);
                yield true;
            }
            case END_GAME -> {
                controller.endGame();
                yield true;
            }
        };
        controller.config();
        return ret;
    }

    private boolean handleResponse(ServerCommandType serverType, Object[] input) {
        if (!(input[1] instanceof Boolean bool))
            return false;
        switch (serverType) {
            case SIGN_UP -> controller.signUpResult(bool);
            case LOGIN -> controller.loginResult(bool);
            case ADD_CARD -> controller.addCardResult(bool);
            case ADD_DECK, RENAME -> controller.deckNameResult(bool);
            case MOVE -> controller.moveDeckResult(bool);
            case CREATE_GAME, DECK_READER -> controller.createGameResult(bool);
            case START_GAME -> controller.startGameResult(bool);
            case END_TURN -> controller.endTurnResult(bool);
        }
        return true;
    }
}
