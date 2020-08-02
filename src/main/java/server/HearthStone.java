package server;

import client.Client;
import client.graphics.*;
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
        client = new Client(server, primaryStage);
    }
}

