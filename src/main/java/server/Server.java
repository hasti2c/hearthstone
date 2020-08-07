package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final Object clientsMonitor = new Object();
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

    private class Accepter extends Thread {
        public void run() {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    synchronized (clientsMonitor) {
                        clients.add(new ClientHandler(controller, socket));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
