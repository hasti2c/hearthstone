package client;

import commands.*;
import commands.types.*;
import elements.heros.*;
import shared.*;
import system.game.*;

import java.util.*;

import static system.game.GameType.*;

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
                if (!(input[0] instanceof GameType gameType))
                    break;
                updateGame(gameType, input);
            }
            //TODO end game
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
        if (!(input[1] instanceof Integer id && Methods.isArrayOfType(HeroClass.class, input[2], input[3]) && Methods.isArrayOfType(String.class, input[4], input[5])))
            return;

        System.out.println("update game: " + gameType + " " + Arrays.toString(input));
        ArrayList<HeroClass> heroClasses = new ArrayList<>(Arrays.asList((HeroClass) input[2], (HeroClass) input[3]));
        ArrayList<String> jsons = new ArrayList<>(Arrays.asList((String) input[4], (String) input[5]));

        Game game = controller.getGame();
        if (game != null)
            game.updateState(jsons);
        else if (gameType == OFFLINE_MULTIPLAYER)
            createGame(gameType, id, heroClasses, jsons);
        else if (input.length > 6 && input[6] instanceof Integer index)
            createGame(gameType, id, index, heroClasses, jsons);
    }

    private void createGame(GameType gameType, int id, ArrayList<HeroClass> heroClasses, ArrayList<String> jsons) {
        createGame(gameType, id, -1, heroClasses, jsons);
    }

    private void createGame(GameType gameType, int id, int index, ArrayList<HeroClass> heroClasses, ArrayList<String> jsons) {
        System.out.println("create game: " + gameType + " " + index);
        controller.setGame(new Game(controller, gameType, id, heroClasses, jsons));
        controller.gameInitialized(index);
    }
}
