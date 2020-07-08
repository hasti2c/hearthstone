package graphics.directories.playground;

import controllers.commands.*;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import system.Game;
import graphics.*;
import graphics.directories.*;
import graphics.popups.*;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import system.player.GamePlayer;
import system.player.NPC;

public class PlayGround extends Directory {
    private final Game game;
    private final CharacterGraphics<?>[] characters = new CharacterGraphics<?>[2];
    @FXML
    private Pane pane;
    @FXML
    private Label gameEventsLabel, timerLabel;
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
            if (game.getCharacters()[i] instanceof GamePlayer gamePlayer)
                characters[i] = new GamePlayerGraphics(this, runner, gamePlayer);
            else
                characters[i] = new NPCGraphics(this, runner, (NPC) game.getCharacters()[i]);
            pane.getChildren().add(i, characters[i].getPane());
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
        for (CharacterGraphics<?> character : characters)
            character.config();
        updateTime();
        timerLabel.setVisible(game.getCurrentCharacter() instanceof GamePlayer);
        gameEventsLabel.setText("Game Events:\n" + game.getGameEvents());
    }

    public void updateTime() {
        int time = game.getTime();
        String timeText;
        if (time < 10)
            timeText = "0:0" + time;
        else if (time < 60)
            timeText = "0:" + time;
        else
            timeText = "1:00";
        timerLabel.setText(timeText);
        if (time <= 10)
            timerLabel.setTextFill(Color.RED);
        else
            timerLabel.setTextFill(Color.WHITE);
    }

    private boolean confirm() {
        ConfirmationBox confirmationBox = new ConfirmationBox("Your progress will not be saved.\nAre you sure you want to quit the game?", "Proceed", "Cancel");
        confirmationBox.display();
        return confirmationBox.getResponse();
    }

    CharacterGraphics<?> getCurrentCharacter() {
        return characters[game.getCurrentPlayerNumber()];
    }

    CharacterGraphics<?> getOtherCharacter() {
        return characters[game.getPlayerCount() - 1 - game.getCurrentPlayerNumber()];
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

    public void endGame() {
        GameEnding gameEnd = new GameEnding(controller, runner, game);
        gameEnd.display();
    }

    public Button getEndTurnButton() {
        return endTurnButton;
    }
}
