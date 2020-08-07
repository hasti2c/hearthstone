package server;

import client.*;
import javafx.application.*;
import javafx.stage.*;

public class HearthStone extends Application {
    private Server server;
    private Client client;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        server = new Server();
        server.start();
        client = new Client(primaryStage);
    }
}

