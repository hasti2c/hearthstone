package client;

import commands.*;
import commands.types.*;
import system.game.Game;

public class ClientCommandRunner extends CommandRunner<ClientCommandType> {
    private final ClientController controller;

    public ClientCommandRunner(ClientController controller) {
        this.controller = controller;
    }

    @Override
    public void run(Command<ClientCommandType> command) {
        System.out.println(command);
        ClientCommandType type = command.getCommandType();
        Object[] input = command.getInput();

        switch (type) {
            case RESULT -> {
                if (input[0] instanceof ServerCommandType serverType)
                    handleResponse(serverType, input);
            }
            case UPDATE_PLAYER -> {
                if (input[0].equals("null")) {
                    controller.setCurrentPlayer(null);
                    break;
                }
                if (input[0] instanceof String username && input[1] instanceof String json)
                    controller.updatePlayer(username, json);
            }
            case UPDATE_GAME -> {
                if (controller.getCurrentPlayer() == null)
                    break;
                if (input[0].equals("null")) {
                    controller.getCurrentPlayer().setGame(null);
                    break;
                }
                if (input[0] instanceof Integer num && input[1] instanceof String json) {
                    if (controller.getGame() == null)
                        controller.setGame(new Game());
                    controller.getGame().getCharacters()[num].updateState(json);
                }
            }
            case END_GAME -> controller.endGame();
        }
        controller.config();
    }

    private void handleResponse(ServerCommandType serverType, Object[] input) {
        if (!(input[1] instanceof Boolean bool))
            return;
        switch (serverType) {
            case SIGN_UP -> controller.signUpResult(bool);
            case LOGIN -> controller.loginResult(bool);
            case ADD_CARD -> controller.addCardResult(bool);
            case ADD_DECK, RENAME -> controller.deckNameResult(bool);
            case MOVE -> controller.moveDeckResult(bool);
            //case JOIN_GAME
            case CREATE_GAME, DECK_READER -> controller.createGameResult(bool);
            case START_GAME -> controller.startGameResult(bool);
            case END_TURN -> controller.endTurnResult(bool);
        }
    }
}
