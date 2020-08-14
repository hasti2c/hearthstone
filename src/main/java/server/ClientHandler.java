package server;

import commands.*;
import commands.types.*;
import elements.cards.*;
import shared.*;
import system.game.characters.Character;
import system.game.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler extends Controller<ServerCommandType> {
    private final ArrayList<Command<ServerCommandType>> commands = new ArrayList<>();
    private final Object commandsMonitor = new Object(), connectionMonitor = new Object();
    private final Server server;
    private final Socket socket;
    private final Listener listener;
    private PrintStream printer;
    private GameHandler gameHandler;
    private boolean isConnected = true;

    public ClientHandler(Server server, ServerController controller, Socket socket) {
        this.server = server;
        this.socket = socket;
        runner = new ServerCommandRunner(controller,this);
        parser = new CommandParser<>(this, ServerCommandType.class);
        listener = new Listener();
        listener.start();
        try {
            printer = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        synchronized (commandsMonitor) {
            commands.addAll(listener.tempCommands);
            listener.tempCommands.clear();
        }
        while (commands.size() > 0) {
            runner.run(commands.get(0));
            commands.remove(0);
        }
        synchronized (connectionMonitor) {
            if (!isConnected)
                server.disconnect(this);
        }
    }

    public void respond(Command<ClientCommandType> command) {
        printer.println(command.toString());
    }

    public void setGameHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public boolean joinGame(GameType gameType) {
        return server.joinGame(this, gameType);
    }

    public boolean startGame(ArrayList<Card> cards) {
        Game game = currentPlayer.getGame();
        if (game == null)
            return false;
        if (!game.getType().isMultiPlayer()) {
            game.startGame(new ArrayList<>(Arrays.asList(cards, cards)));
            return true;
        }

        if (gameHandler == null)
            return false;
        return gameHandler.startGame(this, cards);
    }

    public ClientHandler getOpponent() {
        if (gameHandler == null)
            return null;
        return gameHandler.getOpponent(this);
    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }

    public boolean isMyTurn() {
        if (gameHandler == null)
            return true;
        return gameHandler.isMyTurn(this);
    }

    protected Character getMyCharacter() {
        if (gameHandler == null)
            return getGame().getCurrentCharacter();
        return gameHandler.getGame().getCharacters()[gameHandler.indexOf(this)];
    }

    private class Listener extends Thread {
        private final ArrayList<Command<ServerCommandType>> tempCommands = new ArrayList<>();
        private Scanner scanner;

        private Listener() {
            try {
                scanner = new Scanner(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (isConnected) {
                try {
                    Command<ServerCommandType> command = parser.parse(scanner.nextLine());
                    synchronized (commandsMonitor) {
                        tempCommands.add(command);
                    }
                } catch (NoSuchElementException e) {
                    synchronized (connectionMonitor) {
                        isConnected = false;
                    }
                }
            }
        }
    }
}
