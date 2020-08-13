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
            case UPDATE_GAME -> {
                if (controller.getCurrentPlayer() == null)
                    break;
                if (input[0].equals("null")) {
                    controller.getCurrentPlayer().setGame(null);
                    break;
                }
                if (input[0] instanceof Integer id && Methods.isArrayOfType(HeroClass.class, new Object[]{input[1], input[2]}) && Methods.isArrayOfType(String.class, new Object[]{input[3], input[4]})) {
                    Game game = controller.getGame();
                    if (game == null)
                        createGame(id, new HeroClass[]{(HeroClass) input[1], (HeroClass) input[2]}, new String[]{(String) input[3], (String) input[4]});
                    else
                        game.updateState(new String[]{(String) input[3], (String) input[4]});
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
            case DECK_READER -> controller.gameInitialized();
            case START_GAME -> controller.startGameResult(bool);
            case END_TURN -> controller.endTurnResult(bool);
        }
    }

    private void createGame(int id, HeroClass[] heroClasses, String[] jsons) {
        controller.setGame(Game.getInstance(controller, 2, id, Methods.getListOfType(HeroClass.class, heroClasses), Methods.getListOfType(String.class, jsons)));
        controller.gameInitialized();
    }
}
