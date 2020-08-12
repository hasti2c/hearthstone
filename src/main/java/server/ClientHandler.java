package server;

import commands.*;
import commands.types.*;
import shared.*;

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
    private ClientHandler opponent;

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

    public boolean joinGame() {
        return server.joinGame(this);
    }

    public ClientHandler getOpponent() {
        return opponent;
    }

    public void setOpponent(ClientHandler opponent) {
        this.opponent = opponent;
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
