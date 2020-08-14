package server;

import commands.*;
import commands.types.*;
import elements.cards.*;
import shared.*;
import system.game.Character;
import system.game.GameType;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler extends Controller<ServerCommandType> {
    private final ArrayList<Command<ServerCommandType>> commands = new ArrayList<>();
    private final Object commandsMonitor = new Object();
    private final Server server;
    private final Socket socket;
    private final Listener listener;
    private PrintStream printer;
    private GameHandler gameHandler;

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
            return false;
        return gameHandler.isMyTurn(this);
    }

    protected Character getMyCharacter() {
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
            while (true) {
                Command<ServerCommandType> command = parser.parse(scanner.nextLine());
                synchronized (commandsMonitor) {
                    tempCommands.add(command);
                }
            }
        }
    }
}
