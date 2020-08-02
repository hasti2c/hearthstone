package client.graphics.directories;

import client.Client;
import server.commands.*;
import elements.cards.Passive;
import system.*;
import system.player.Inventory;
import client.graphics.*;
import client.graphics.directories.collections.*;
import client.graphics.directories.playground.*;
import client.graphics.popups.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class Home extends Directory {
    private PlayGround playGround;
    private Collections collections;
    private Store store;
    private Stats stats;
    private GameStartPage gameBeginning;
    @FXML
    private Button playButton, collectionsButton, storeButton, statsButton, homeLogoutButton, homeExitButton;

    public Home(GraphicsController controller, Client client) {
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
    }

    @Override
    protected void config() {}

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

    private class GameStartPage extends PopupBox {
        private Game game;
        @FXML
        private HBox deckHBox, noDeckHBox, passiveHBox;
        @FXML
        private VBox vBox;
        @FXML
        private Label deckName;
        @FXML
        private Button singlePlayerButton, multiPlayerButton, cancelButton, collectionsButton, deckReaderButton;
        @FXML
        private ChoiceBox<Passive> passiveChoiceBox;

        protected GameStartPage() {
            vBox.getChildren().remove(deckHBox);
            vBox.getChildren().remove(passiveHBox);
            vBox.getChildren().remove(noDeckHBox);
            cancelButton.setOnAction(e -> close());
            singlePlayerButton.setOnAction(e -> createGame(1));
            multiPlayerButton.setOnAction(e -> createGame(2));
            collectionsButton.setOnAction(e -> displayCollections());
            deckReaderButton.setOnAction(e -> {
                close();
                client.runCommand(new Command(CommandType.DECK_READER));
                displayPlayGround();
            });
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
        }

        private void createGame(int playerCount) {
            close();
            client.runCommand(new Command(CommandType.CREATE_GAME, playerCount));
            game = controller.getCurrentPlayer().getGame();
            game.getCharacters()[0].setPassive(passiveChoiceBox.getValue());
            game.getCharacters()[1].setPassive(passiveChoiceBox.getValue());
            displayPlayGround();
        }

        private void displayPlayGround() {
            Game game = controller.getCurrentPlayer().getGame();
            playGround = new PlayGround(game, controller, client);
            game.setPlayGround(playGround);
            playGround.display();
        }
    }
}