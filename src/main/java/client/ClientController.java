package client;

import client.graphics.directories.*;
import client.graphics.directories.collections.Collections;
import client.graphics.directories.playground.*;
import client.graphics.popups.*;
import elements.cards.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import shared.*;
import commands.*;
import commands.types.*;
import system.game.*;
import system.game.characters.Character;
import system.player.*;

import java.util.*;

import static commands.types.ServerCommandType.*;

public class ClientController extends Controller<ClientCommandType> {
    private final Stage stage;
    private final StartPage startPage;
    private final Home home;
    private final Client client;
    private Directory currentDirectory;

    ClientController(Client client, Stage stage) {
        this.stage = stage;
        this.client = client;

        runner = new ClientCommandRunner(this);
        parser = new CommandParser<>(this, ClientCommandType.class);

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
        Game game = getGame();
        currentPlayer = Player.getExistingPlayer(username, json);
        setGame(game);
    }

    public void setScene(Scene scene) {
        stage.setScene(scene);
    }

    public void viewCardInStore(Card card) {
        Store store = home.getStore();
        store.display();
        store.search(card.toString());
    }

    //TODO debug end game
    public void endGame(GameEndingType endingType) {
        home.getPlayGround().endGame(endingType);
    }

    @Override
    public ArrayList<?> getObjectsList(String name) {
        return new ArrayList<>();
    }

    public void setCurrentDirectory(Directory currentDirectory) {
        if (this.currentDirectory instanceof PlayGround playGround)
            playGround.leaveGame();
        this.currentDirectory = currentDirectory;
    }

    public void signUpResult(boolean success) {
        if (!success)
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
    }

    public void deckNameResult(boolean result) {
        if (!result && currentDirectory instanceof Collections collections)
            collections.displayDeckNameError();
    }

    public void moveDeckResult(boolean result) {
        if (!result && currentDirectory instanceof Collections collections)
            collections.displayHeroChangeError();
    }

    public void gameInitialized(int gameIndex) {
        home.startGame(gameIndex);
    }

    public void joinGameResult(Boolean result) {
        //TODO don't suddenly start
        if (!result) {
            new AlertBox("You have joined the queue. The game will automatically start when we find an opponent for you. \nNote: If you were already in the queue, you weren't put in it again.", "Okay").display();
            home.closeGameBeginning();
        }
    }

    public void startGameResult(boolean result) {
        if (result)
            home.getPlayGround().startTimer();
    }

    public void config() {
        currentDirectory.config();
    }

    public void endTurnResult(Boolean result) {
        if (result)
            home.getPlayGround().doEndTurn();
    }

    @Override
    protected Character getMyCharacter() {
        if (home.getPlayGround() == null)
            return null;
        return home.getPlayGround().getMyCharacter().getCharacter();
    }

    public void deleteResult(boolean result) {
        if (!result) {
            AlertBox alert = new AlertBox("Your account couldn't be deleted. Please try again.", "Okay");
            alert.display();
        }
    }

    public void fileError() {
        new AlertBox("There isn't a corrent jar file at \"src/main/resources/TavernBrawl.jar\". Please try again.", "Okay.").display();
        home.closeGameBeginning();
    }
}
