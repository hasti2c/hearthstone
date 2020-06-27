package controllers.game;

import graphics.*;
import javafx.application.*;
import javafx.stage.*;

public class HearthStone extends Application {
    private GameController game;
    private GraphicsController graphics;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        game = GameController.getInstance();
        graphics = new GraphicsController(game, primaryStage);
    }

}

