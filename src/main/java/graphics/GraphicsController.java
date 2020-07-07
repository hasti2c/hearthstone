package graphics;

import controllers.commands.*;
import controllers.game.*;
import elements.cards.*;
import system.player.Player;
import graphics.directories.*;
import graphics.directories.collections.*;
import javafx.scene.*;
import javafx.stage.*;

public class GraphicsController {
    private final Stage stage;
    private final CommandRunner runner;
    private final StartPage startPage;
    private final Home home;
    private final GameController game;
    private Player currentPlayer;

    public GraphicsController(GameController game, Stage stage) {
        this.game = game;
        this.stage = stage;
        runner = new CommandRunner(game, this);
        currentPlayer = game.getCurrentPlayer();
        startPage = new StartPage(this, runner);
        home = new Home(this, runner);

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

    public StartPage getStartPage() {
        return startPage;
    }

    public void exit() {
        runner.run(new Command(CommandType.EXIT));
        stage.close();
    }

    public void displayHome() {
        home.display();
    }

    public void displayStartPage() {
        runner.run(new Command(CommandType.LOGOUT));
        startPage.display();
    }

    public void updatePlayer() {
        currentPlayer = game.getCurrentPlayer();
    }

    public void setScene(Scene scene) {
        stage.setScene(scene);
    }

    public void viewCardInStore(Card card) {
        Store store = home.getStore();
        store.display();
        store.search(card.toString());
    }

    public void endGame() {
        home.getPlayGround().endGame();
    }
}
