package controllers.game;

import directories.Directory;
import directories.collections.Collections;
import directories.collections.HeroDirectory;
import gameObjects.Player;
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
        game = new GameController();
        game.configGame();
        graphics = new GraphicsController(game, primaryStage);
    }


}

