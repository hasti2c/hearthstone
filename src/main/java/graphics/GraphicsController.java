package graphics;

import java.util.*;
import controllers.commands.*;
import controllers.game.*;
import gameObjects.*;
import gameObjects.cards.Card;
import graphics.directories.*;
import graphics.directories.collections.StoreGraphics;
import javafx.scene.*;
import javafx.stage.*;

public class GraphicsController {
    private Stage stage;
    private CommandRunner runner;
    private StartPageGraphics startPage;
    private HomeGraphics home;
    private GameController game;
    private Player currentPlayer;

    public GraphicsController(GameController game, Stage stage) {
        this.game = game;
        this.stage = stage;
        runner = new CommandRunner(game, this);
        currentPlayer = game.getCurrentPlayer();
        startPage = new StartPageGraphics(this, runner);
        home = new HomeGraphics(this, runner);

        displayStartPage();
        stage.setOnCloseRequest(e -> exit());
        stage.show();
    }

    public CommandRunner getRunner() {
        return runner;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public StartPageGraphics getStartPage() {
        return startPage;
    }

    public void exit() {
        runner.run(new Command(CommandType.EXIT, new ArrayList<>(Collections.singletonList('a'))));
        stage.close();
    }

    public void displayHome() {
        home.display();
    }

    public void displayStartPage() {
        runner.run(new Command(CommandType.EXIT));
        startPage.display();
    }

    public void updatePlayer() {
        currentPlayer = game.getCurrentPlayer();
    }

    public void setScene(Scene scene) {
        stage.setScene(scene);
    }

    public void viewCardInStore(Card card) {
        StoreGraphics store = home.getStore();
        store.display();
        store.search(card.toString());
    }
}
