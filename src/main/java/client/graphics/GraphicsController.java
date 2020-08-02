package client.graphics;

import client.Client;
import shared.commands.Command;
import server.Controller;
import elements.cards.*;
import system.player.Player;
import client.graphics.directories.*;
import client.graphics.directories.collections.*;
import javafx.scene.*;
import javafx.stage.*;

import static shared.commands.types.ServerCommandType.*;

public class GraphicsController {
    private final Stage stage;
    private final StartPage startPage;
    private final Home home;
    private final Client client;
    private final Controller game;
    private Player currentPlayer;

    public GraphicsController(Client client, Controller game, Stage stage) {
        this.client = client;
        this.game = game;
        this.stage = stage;
        currentPlayer = game.getCurrentPlayer();
        startPage = new StartPage(this, client);
        home = new Home(this, client);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void start() {
        displayStartPage();
        stage.setOnCloseRequest(e -> exit());
        stage.show();
    }

    public void exit() {
        client.request(new Command<>(EXIT));
        stage.close();
    }

    public void displayHome() {
        home.display();
    }

    public void displayStartPage() {
        client.request(new Command<>(LOGOUT));
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
