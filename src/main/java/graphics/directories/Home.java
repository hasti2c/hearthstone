package graphics.directories;

import controllers.commands.*;
import gameObjects.Game;
import gameObjects.Player.Inventory;
import gameObjects.cards.Passive;
import graphics.*;
import graphics.directories.collections.*;
import graphics.directories.playground.PlayGround;
import graphics.popups.PopupBox;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Home extends Directory {
    private PlayGround playGround;
    private final Collections collections = new Collections(controller, runner);
    private final Store store = new Store(controller, runner);
    private final Stats stats = new Stats(controller, runner);
    private final GameStartPage gameBeginning;
    @FXML
    private Button playButton, collectionsButton, storeButton, statsButton, homeLogoutButton, homeExitButton;

    public Home(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
        //playButton.setOnAction(e -> displayPlayGround());
        gameBeginning = new GameStartPage();
        playButton.setOnAction(e -> gameBeginning.display());
        collectionsButton.setOnAction(e -> collections.display());
        storeButton.setOnAction(e -> store.display());
        statsButton.setOnAction(e -> stats.display());
        homeLogoutButton.setOnAction(e -> controller.displayStartPage());
        homeExitButton.setOnAction(e -> controller.exit());
    }

    @Override
    protected void config() {}

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(Home.class.getResource("/fxml/directories/home.fxml"));
    }

    public Store getStore() {
        return store;
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
        private Button doneButton, cancelButton, collectionsButton, deckReaderButton;
        @FXML
        private ChoiceBox<Passive> passiveChoiceBox;

        protected GameStartPage() {
            vBox.getChildren().remove(deckHBox);
            vBox.getChildren().remove(passiveHBox);
            vBox.getChildren().remove(noDeckHBox);
            cancelButton.setOnAction(e -> close());
            doneButton.setOnAction(e -> {
                close();
                //game.setPassive(passiveChoiceBox.getValue());
                runner.run(new Command(CommandType.START_GAME));
                displayPlayGround();
            });
            collectionsButton.setOnAction(e -> {
                close();
                collections.display();
            });
            deckReaderButton.setOnAction(e -> {
                close();
                runner.run(new Command(CommandType.DECK_READER));
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
            doneButton.setDisable(false);

            Inventory inventory = controller.getCurrentPlayer().getInventory();
            deckHBox.getChildren().add(1, inventory.getCurrentHero().getHeroClass().getIcon());
            deckName.setText(inventory.getCurrentDeck().toString());

            /*ArrayList<Passive> passives = new ArrayList<>();
            while (passives.size() < 3) {
                Passive p = GameController.getRandomPassive();
                if (!passives.contains(p))
                    passives.add(p);
            }
            for (Passive p : passives) {
                passiveChoiceBox.getItems().add(p);
            }*/
        }

        private void configNoDeck() {
            if (!vBox.getChildren().contains(noDeckHBox))
                vBox.getChildren().add(noDeckHBox);
            vBox.getChildren().remove(deckHBox);
            vBox.getChildren().remove(passiveHBox);
            doneButton.setDisable(true);
        }

        private void displayPlayGround() {
            playGround = new PlayGround(controller.getCurrentPlayer().getGame(), controller, runner);
            playGround.display();
        }
    }
}
