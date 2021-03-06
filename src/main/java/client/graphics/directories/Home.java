package client.graphics.directories;

import client.*;
import commands.*;
import elements.cards.*;
import system.game.Game;
import system.game.*;
import system.player.*;
import client.graphics.directories.collections.*;
import client.graphics.directories.playground.*;
import client.graphics.popups.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import static commands.types.ServerCommandType.*;
import static system.game.GameType.*;

public class Home extends Directory {
    private PlayGround playGround;
    private Collections collections;
    private Store store;
    private Stats stats;
    private GameStartPage gameBeginning;
    @FXML
    private Button playButton, collectionsButton, storeButton, statsButton, homeLogoutButton, homeExitButton, deleteAccountButton;

    public Home(ClientController controller, Client client) {
        super(controller, client);
        playButton.setOnAction(e -> {
            if (gameBeginning == null)
                gameBeginning = new GameStartPage();
            gameBeginning.display();
        });
        collectionsButton.setOnAction(e -> displayCollections());
        storeButton.setOnAction(e -> {
            if (store == null)
                store = new Store(controller, client);
            store.display();
        });
        statsButton.setOnAction(e -> {
            if (stats == null)
                stats = new Stats(controller, client);
            stats.display();
        });
        homeLogoutButton.setOnAction(e -> controller.displayStartPage());
        homeExitButton.setOnAction(e -> controller.exit());
        deleteAccountButton.setOnAction(e -> deleteAccount());
    }

    @Override
    public void config() {}

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(Home.class.getResource("/fxml/directories/home.fxml"));
    }

    private void displayCollections() {
        if (collections == null)
            collections = new Collections(controller, client);
        collections.display();
    }

    public Store getStore() {
        if (store == null)
            store = new Store(controller, client);
        return store;
    }

    public PlayGround getPlayGround() {
        return playGround;
    }

    public void startGame(int gameIndex) {
        gameBeginning.startGame(gameIndex);
    }

    public void closeGameBeginning() {
        gameBeginning.close();
    }

    private void deleteAccount() {
        String username = controller.getCurrentPlayer().toString();
        ConfirmationBox confirmationBox = new ConfirmationBox("Are you sure you want to delete the account \"" + username + "\"? \nThis action is irreversible.", "Yes, I'm Sure.", "No, Never Mind.");
        confirmationBox.display();
        if (confirmationBox.getResponse()) {
            QuestionBox question = new QuestionBox("Please enter your password.", "Done", "Cancel", true);
            question.display();
            if (question.getButtonResponse())
                client.request(new Command<>(DELETE, question.getText()));
        }
    }

    private class GameStartPage extends PopupBox {
        private Game game;
        @FXML
        private HBox deckHBox, noDeckHBox, passiveHBox;
        @FXML
        private VBox vBox;
        @FXML
        private Label deckName;
        @FXML
        private Button singlePlayerButton, offlineMultiPlayerButton, onlineMultiPlayerButton, cancelButton, collectionsButton, deckReaderButton, tavernBrawlButton;
        @FXML
        private ChoiceBox<Passive> passiveChoiceBox;

        protected GameStartPage() {
            vBox.getChildren().remove(deckHBox);
            vBox.getChildren().remove(passiveHBox);
            vBox.getChildren().remove(noDeckHBox);
            cancelButton.setOnAction(e -> close());
            singlePlayerButton.setOnAction(e -> joinGame(SINGLE_PLAYER));
            offlineMultiPlayerButton.setOnAction(e -> joinGame(OFFLINE_MULTIPLAYER));
            onlineMultiPlayerButton.setOnAction(e -> joinGame(ONLINE_MULTIPLAYER));
            collectionsButton.setOnAction(e -> {
                displayCollections();
                close();
            });
            deckReaderButton.setOnAction(e -> joinGame(DECK_READER));
            tavernBrawlButton.setOnAction(e -> joinGame(TAVERN_BRAWL));
        }

        private void clear() {
            if (deckHBox.getChildren().size() == 3)
                deckHBox.getChildren().remove(1);
            passiveChoiceBox.getItems().clear();
        }

        protected void config() {
            clear();
            if (controller.getCurrentPlayer() == null || controller.getCurrentPlayer().getInventory().getCurrentDeck() == null)
                configNoDeck();
            else
                configHasDeck();
        }

        @Override
        protected FXMLLoader getLoader() {
            return new FXMLLoader(GameStartPage.class.getResource("/fxml/popups/gameBeginning.fxml"));
        }

        private void configHasDeck() {
            if (!vBox.getChildren().contains(deckHBox))
                vBox.getChildren().add(deckHBox);
            if (!vBox.getChildren().contains(passiveHBox))
                vBox.getChildren().add(passiveHBox);
            vBox.getChildren().remove(noDeckHBox);

            singlePlayerButton.setDisable(false);
            onlineMultiPlayerButton.setDisable(false);
            offlineMultiPlayerButton.setDisable(false);
            deckReaderButton.setDisable(false);
            tavernBrawlButton.setDisable(false);

            Inventory inventory = controller.getCurrentPlayer().getInventory();
            deckHBox.getChildren().add(1, inventory.getCurrentHero().getHeroClass().getIcon());
            deckName.setText(inventory.getCurrentDeck().toString());

            while (passiveChoiceBox.getItems().size() < 3) {
                Passive p = Passive.getRandomPassive();
                if (!passiveChoiceBox.getItems().contains(p))
                    passiveChoiceBox.getItems().add(p);
            }
        }

        private void configNoDeck() {
            if (!vBox.getChildren().contains(noDeckHBox))
                vBox.getChildren().add(noDeckHBox);
            vBox.getChildren().remove(deckHBox);
            vBox.getChildren().remove(passiveHBox);
            singlePlayerButton.setDisable(true);
            offlineMultiPlayerButton.setDisable(true);
            onlineMultiPlayerButton.setDisable(true);
            deckReaderButton.setDisable(false);
            tavernBrawlButton.setDisable(true);
        }

        private void joinGame(GameType gameType) {
            client.request(new Command<>(JOIN_GAME, gameType));
        }

        public void startGame(int gameIndex) {
            close();
            game = controller.getCurrentPlayer().getGame();
            game.getCharacters()[0].setPassive(passiveChoiceBox.getValue());
            game.getCharacters()[1].setPassive(passiveChoiceBox.getValue());
            playGround = new PlayGround(game, controller, client, gameIndex);
            playGround.display();
        }
    }
}
