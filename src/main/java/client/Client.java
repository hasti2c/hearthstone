package client;

import javafx.application.Platform;
import javafx.stage.*;
import commands.*;
import commands.types.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private final ClientController controller;
    private Socket socket;
    private PrintStream printer;
    private final Object socketMonitor = new Object();

    public Client(Stage stage) {
        try {
            socket = new Socket("localhost", 8000);
            printer = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller = new ClientController(this, stage);
    }

    public void start() {
        new Listener().start();
        controller.start();
    }

    public boolean request(Command<ServerCommandType> command) {
        synchronized (socketMonitor) {
            printer.println(command);
        }
        return false;
    }

    private class Listener extends Thread {
        private Scanner scanner;
        private final CommandRunner<ClientCommandType> runner;
        private final CommandParser<ClientCommandType> parser;

        private Listener() {
            try {
                scanner = new Scanner(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            runner = controller.getRunner();
            parser = controller.getParser();
        }

        @Override
        public void run() {
            while (true) {
                Command<ClientCommandType> command = parser.parse(scanner.nextLine());
                synchronized (socketMonitor) {
                    Platform.runLater(() -> runner.run(command));
                }
            }
        }
    }
}
