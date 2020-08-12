package client;

import commands.*;
import commands.types.*;
import elements.heros.*;
import shared.*;
import system.game.*;

public class ClientCommandRunner extends CommandRunner<ClientCommandType> {
    private final ClientController controller;

    public ClientCommandRunner(ClientController controller) {
        this.controller = controller;
    }

    @Override
    public void run(Command<ClientCommandType> command) {
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
            case INIT_GAME -> {
                if (controller.getCurrentPlayer() == null)
                    break;
                if (input[0] instanceof Integer id && Methods.isArrayOfType(HeroClass.class, new Object[]{input[1], input[2]}) && Methods.isArrayOfType(String.class, new Object[]{input[3], input[4]})) {
                    controller.setGame(Game.getInstance(controller, 2, id, Methods.getListOfType(HeroClass.class, new Object[]{input[1], input[2]}), Methods.getListOfType(String.class, new Object[]{input[3], input[4]})));
                    //TODO clean
                    controller.createGameResult(true);
                }
            }
            case UPDATE_GAME -> {
                if (controller.getGame() == null)
                    break;
                if (input[0].equals("null")) {
                    controller.getCurrentPlayer().setGame(null);
                    break;
                }
                if (input[0] instanceof Integer num && input[1] instanceof String json)
                    controller.getGame().getCharacters()[num].updateState(json);
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
            case CREATE_GAME, DECK_READER -> controller.createGameResult(bool);
            case START_GAME -> controller.startGameResult(bool);
            case END_TURN -> controller.endTurnResult(bool);
        }
    }
}
