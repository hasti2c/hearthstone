package graphics.directories;

import controllers.commands.*;
import controllers.game.GameController;
import gameObjects.Game;
import gameObjects.cards.Passive;
import graphics.*;
import graphics.directories.collections.*;
import graphics.popups.PopupBox;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class HomeGraphics extends DirectoryGraphics {
    private PlayGroundGraphics playGround;
    private final CollectionsGraphics collections = new CollectionsGraphics(controller, runner);
    private final StoreGraphics store = new StoreGraphics(controller, runner);
    private final StatsGraphics stats = new StatsGraphics(controller, runner);
    private final GameBeginningGraphics gameBeginning;
    @FXML
    private Button playButton, collectionsButton, storeButton, statsButton, homeLogoutButton, homeExitButton;

    public HomeGraphics(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
        //playButton.setOnAction(e -> displayPlayGround());
        gameBeginning = new GameBeginningGraphics();
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
        return new FXMLLoader(HomeGraphics.class.getResource("/fxml/directories/home.fxml"));
    }

    @Override
    protected void runCd() {
        runner.run(new Command(CommandType.CD, "~"));
    }

    public StoreGraphics getStore() {
        return store;
    }

    private class GameBeginningGraphics extends PopupBox {
        private Game game;
        @FXML
        private HBox deckHBox, noDeckHBox, passiveHBox;
        @FXML
        private VBox vBox;
        @FXML
        private Label deckName;
        @FXML
        private Button doneButton, cancelButton, collectionsButton;
        @FXML
        private ChoiceBox<Passive> passiveChoiceBox;

        protected GameBeginningGraphics() {
            vBox.getChildren().remove(deckHBox);
            vBox.getChildren().remove(passiveHBox);
            vBox.getChildren().remove(noDeckHBox);
            cancelButton.setOnAction(e -> close());
            doneButton.setOnAction(e -> {
                close();
                game.setPassive(passiveChoiceBox.getValue());
                displayPlayGround();
            });
            collectionsButton.setOnAction(e -> {
                close();
                collections.display();
            });
        }

        private void clear() {
            if (deckHBox.getChildren().size() == 3)
                deckHBox.getChildren().remove(1);
            passiveChoiceBox.getItems().clear();
        }

        protected void config() {
            clear();
            if (controller.getCurrentPlayer() == null || !controller.getCurrentPlayer().getHome().hasPlayGround())
                configNoDeck();
            else
                configHasDeck();
        }

        @Override
        protected FXMLLoader getLoader() {
            return new FXMLLoader(GameBeginningGraphics.class.getResource("/fxml/popups/gameBeginning.fxml"));
        }

        private void configHasDeck() {
            if (!vBox.getChildren().contains(deckHBox))
                vBox.getChildren().add(deckHBox);
            if (!vBox.getChildren().contains(passiveHBox))
                vBox.getChildren().add(passiveHBox);
            vBox.getChildren().remove(noDeckHBox);
            doneButton.setDisable(false);

            game = controller.getCurrentPlayer().getGame();
            deckHBox.getChildren().add(1, game.getHero().getHeroClass().getIcon());
            deckName.setText(game.getPlayer().getCurrentDeck().toString());

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
            assert controller.getCurrentPlayer().getHome().hasPlayGround();
            playGround = new PlayGroundGraphics(controller.getCurrentPlayer().getGame(), controller, runner);
            playGround.display();
        }
    }
}
