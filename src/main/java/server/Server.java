package server;

import shared.*;
import system.game.*;

import java.io.*;
import java.net.*;
import java.util.*;

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
            if (type.isMultiPlayer())
                gameQueues.put(type, new ArrayList<>());
    }

    public boolean joinGame(ClientHandler client, GameType gameType) {
        synchronized (queueMonitor) {
            ArrayList<ClientHandler> gameQueue = gameQueues.get(gameType);
            if (gameQueue.contains(client))
                return false;
            gameQueue.add(client);
            boolean ret = true;
            while (gameQueue.size() >= 2)
                ret &= pairClients(new Pair<>(gameQueue.remove(0), gameQueue.remove(0)), gameType);
            return ret && gameQueue.size() == 0;
        }
    }

    private boolean pairClients(Pair<ClientHandler, ClientHandler> clients, GameType gameType) {
        ClientHandler first = clients.getFirst(), second = clients.getSecond();
        controller.setGameCount(controller.getGameCount() + 1);
        GameHandler game = new GameHandler(first, second);
        return game.createGame(gameType, controller.getGameCount());
    }

    public void disconnect(ClientHandler client) {
        clients.remove(client);
        for (ArrayList<ClientHandler> queue : gameQueues.values())
            queue.remove(client);
        //TODO disconnect from game
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
