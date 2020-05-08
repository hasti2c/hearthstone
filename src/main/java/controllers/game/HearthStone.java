package controllers.game;

import graphics.*;
import javafx.application.*;
import javafx.stage.*;

public class HearthStone extends Application {
    //TODO separator
    private GameController game;
    private GraphicsController graphics;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        game = new GameController();
        game.configGame();
        graphics = new GraphicsController(game, primaryStage);
    }
}
