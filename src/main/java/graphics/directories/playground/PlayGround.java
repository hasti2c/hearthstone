package graphics.directories.playground;

import controllers.commands.*;
import gameObjects.*;
import graphics.*;
import graphics.directories.*;
import graphics.popups.*;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PlayGround extends Directory {
    private final Game game;
    private final GamePlayerGraphics[] gamePlayers = new GamePlayerGraphics[2];
    @FXML
    private Pane pane;
    @FXML
    private Label gameEventsLabel;
    @FXML
    private Button endTurnButton, gameEventsButton;
    @FXML
    private ScrollPane gameEventsScrollPane;
    @FXML
    private HBox topBar;

    public PlayGround(Game game, GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
        this.game = game;
        for (int i = 0; i < 2; i++) {
            gamePlayers[i] = new GamePlayerGraphics(this, runner, game.getGamePlayers()[i]);
            pane.getChildren().add(i, gamePlayers[i].getPane());
        }


        homeButton.setOnAction(e -> {
            if (confirm())
                controller.displayHome();
        });
        logoutButton.setOnAction(e -> {
            if (confirm())
                controller.displayStartPage();
        });
        exitButton.setOnAction(e -> {
            if (confirm())
                controller.exit();
        });

        endTurnButton.setOnAction(e -> {
            runner.run(new Command(CommandType.END_TURN));
            config();
        });

        gameEventsScrollPane.setVisible(false);
        gameEventsButton.setOnAction(e -> {
            if (gameEventsScrollPane.isVisible()) {
                gameEventsScrollPane.setVisible(false);
                gameEventsButton.setText("Click to see game events.");
            } else {
                gameEventsScrollPane.setVisible(true);
                gameEventsButton.setText("Click to hide game events.");
            }
        });
    }

    @Override
    public void config() {
        for (GamePlayerGraphics gamePlayer : gamePlayers)
            gamePlayer.config();
        gameEventsLabel.setText("Game Events:\n" + game.getGameEvents());
    }

    private boolean confirm() {
        ConfirmationBox confirmationBox = new ConfirmationBox("Your progress will not be saved.\nAre you sure you want to quit the game?", "Proceed", "Cancel");
        confirmationBox.display();
        return confirmationBox.getResponse();
    }

    GamePlayerGraphics getCurrentGamePlayer() {
        return gamePlayers[game.getCurrentPlayerNumber()];
    }

    GamePlayerGraphics getOtherGamePlayer() {
        return gamePlayers[game.getPlayerCount() - 1 - game.getCurrentPlayerNumber()];
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(PlayGround.class.getResource("/fxml/directories/playGround.fxml"));
    }

    public void showDiscover(Pane pane) {
        this.pane.getChildren().add(pane);
        putOnTop(topBar);
        putOnTop(endTurnButton);
    }

    public void removeDiscover(Pane pane) {
        this.pane.getChildren().remove(pane);
    }

    private void putOnTop(Node node) {
        pane.getChildren().remove(node);
        pane.getChildren().add(node);
    }
}
