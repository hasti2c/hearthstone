package client;

import client.graphics.directories.*;
import client.graphics.directories.collections.*;
import client.graphics.popups.*;
import elements.cards.*;
import javafx.scene.*;
import javafx.scene.paint.*;
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
    private Directory currentDirectory;
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

    public void setCurrentDirectory(Directory currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public void signUpResult(boolean success) {
        if (success)
            displayHome();
        else
            startPage.displaySignUpError();
    }

    public void loginResult(boolean success) {
        if (success)
            displayHome();
        else
            startPage.displayLoginError();
    }

    public void addCardResult(boolean result) {
        if (!result)
            (new AlertBox("This card couldn't be added to the deck. This deck is full.", Color.RED, "Okay")).display();
        currentDirectory.config();
    }

    public void deckNameResult(boolean result) {
        if (!result && currentDirectory instanceof Collections collections)
            collections.displayDeckNameError();
        currentDirectory.config();
    }

    public void moveDeckResult(boolean result) {
        if (!result && currentDirectory instanceof Collections collections)
            collections.displayHeroChangeError();
        currentDirectory.config();
    }

    public void createGameResult(boolean result) {
        if (result)
            home.startGame();
    }

    public void startGameResult(boolean result) {
        if (result)
            home.getPlayGround().startTimer();
    }
}
