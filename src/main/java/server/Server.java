package server;

import shared.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private final ArrayList<ClientHandler> clients = new ArrayList<>(), gameQueue = new ArrayList<>();
    private final Map<ClientHandler, GameHandler> gameHandlers = new HashMap<>();
    private final Object clientsMonitor = new Object(), queueMonitor = new Object();
    private final ServerController controller;

    public Server() {
        try {
            serverSocket = new ServerSocket(8000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller = ServerController.getInstance();
        (new Accepter()).start();
    }

    public void start() {
        (new Thread(Server.this::run)).start();
    }

    private void run() {
        while (true) {
            synchronized (clientsMonitor) {
                for (ClientHandler client : clients) {
                    client.run();
                }
            }
        }
    }

    public boolean joinGame(ClientHandler client) {
        synchronized (queueMonitor) {
            gameQueue.add(client);
            while (gameQueue.size() >= 2)
                pairClients(new Pair<>(gameQueue.remove(0), gameQueue.remove(0)));
            return gameQueue.size() == 0;
        }
    }

    private void pairClients(Pair<ClientHandler, ClientHandler> clients) {
        ClientHandler first = clients.getFirst(), second = clients.getSecond();
        GameHandler gameHandler = new GameHandler(first, second);
        gameHandlers.put(first, gameHandler);
        gameHandlers.put(second, gameHandler);
    }

    private class Accepter extends Thread {
        public void run() {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    synchronized (clientsMonitor) {
                        clients.add(new ClientHandler(Server.this, controller, socket));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
