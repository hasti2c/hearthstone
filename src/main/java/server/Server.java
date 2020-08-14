package server;

import shared.*;
import system.game.GameType;

import java.io.*;
import java.net.*;
import java.util.*;

import static system.game.GameType.ONLINE_MULTIPLAYER;

public class Server {
    private ServerSocket serverSocket;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final HashMap<GameType, ArrayList<ClientHandler>> gameQueues = new HashMap<>();
    private final Object clientsMonitor = new Object(), queueMonitor = new Object();
    private final ServerController controller;

    public Server() {
        try {
            serverSocket = new ServerSocket(8000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller = ServerController.getInstance();
        initQueues();
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

    private void initQueues() {
        for (GameType type : GameType.values())
            if (type.getNeedsQueue())
                gameQueues.put(type, new ArrayList<>());
    }

    public boolean joinGame(ClientHandler client, GameType gameType) {
        synchronized (queueMonitor) {
            ArrayList<ClientHandler> gameQueue = gameQueues.get(gameType);
            if (gameQueue.contains(client))
                return false;
            gameQueue.add(client);
            while (gameQueue.size() >= 2)
                pairClients(new Pair<>(gameQueue.remove(0), gameQueue.remove(0)), gameType);
            return gameQueue.size() == 0;
        }
    }

    private void pairClients(Pair<ClientHandler, ClientHandler> clients, GameType gameType) {
        ClientHandler first = clients.getFirst(), second = clients.getSecond();
        GameHandler gameHandler = new GameHandler(first, second);
        gameHandler.createGame();
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
