package client;

import javafx.application.*;
import javafx.stage.*;

public class HearthStoneClient extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        (new Client(primaryStage)).start();
    }
}
