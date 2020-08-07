package client;

import client.graphics.directories.*;
import elements.cards.*;
import javafx.scene.*;
import javafx.stage.*;
import shared.*;
import commands.*;
import commands.types.*;
import system.player.*;

import java.util.*;

import static commands.types.ServerCommandType.*;

public class ClientController extends Controller<ClientCommandType> {
    private final Stage stage;
    private final StartPage startPage;
    private final Home home;
    private final Client client;
    //private final ServerController serverController;

    ClientController(Client client, Stage stage) {
        this.stage = stage;
        this.client = client;

        runner = new ClientCommandRunner(this);
        parser = new CommandParser<>(this, ClientCommandType.class);

        //serverController = (ServerController) client.getTarget().getController();
        //currentPlayer = this.serverController.getCurrentPlayer();
        startPage = new StartPage(this, client);
        home = new Home(this, client);
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
        if (currentPlayer != null)
            client.request(new Command<>(LOGOUT));
        startPage.display();
    }

    public void updatePlayer(String username, String json) {
        currentPlayer = Player.getExistingPlayer(username, json);
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

    @Override
    public ArrayList<?> getObjectsList(String name) {
        return new ArrayList<>();
    }

    public StartPage getStartPage() {
        return startPage;
    }
}
