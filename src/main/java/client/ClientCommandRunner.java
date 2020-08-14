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
                if (!(input[0] instanceof GameType gameType))
                    break;
                updateGame(gameType, input);
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
            case JOIN_GAME -> controller.joinGameResult(bool);
            case START_GAME -> controller.startGameResult(bool);
            case END_TURN -> controller.endTurnResult(bool);
        }
    }

    private void updateGame(GameType gameType, Object ...input) {
        switch (gameType) {
            case ONLINE_MULTIPLAYER, DECK_READER: {
                if (input[1] instanceof Integer id && input[2] instanceof Integer index && Methods.isArrayOfType(HeroClass.class, new Object[]{input[3], input[4]}) && Methods.isArrayOfType(String.class, new Object[]{input[5], input[6]})) {
                    Game game = controller.getGame();
                    if (game == null)
                        createGame(gameType, id, index, new HeroClass[]{(HeroClass) input[3], (HeroClass) input[4]}, new String[]{(String) input[5], (String) input[6]});
                    else
                        game.updateState(new String[]{(String) input[5], (String) input[6]});
                }
            }
        }
    }

    private void createGame(GameType gameType, int id, int index, HeroClass[] heroClasses, String[] jsons) {
        controller.setGame(Game.getInstance(controller, gameType, id, Methods.getListOfType(HeroClass.class, heroClasses), Methods.getListOfType(String.class, jsons)));
        controller.gameInitialized(index);
    }
}
