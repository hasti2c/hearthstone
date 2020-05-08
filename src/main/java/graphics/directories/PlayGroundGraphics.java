package graphics.directories;

import controllers.commands.Command;
import controllers.commands.CommandRunner;
import controllers.commands.CommandType;
import controllers.game.GameController;
import gameObjects.Game;
import gameObjects.cards.Card;
import graphics.GraphicsController;
import graphics.popups.ConfirmationBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PlayGroundGraphics extends DirectoryGraphics {
    private Game game;
    @FXML
    private Label heroName, heroPowerName, weaponName, deckName, hpLabel, manaLabel;
    @FXML
    private HBox handHBox, inGameHBox;
    @FXML
    private Button endTurnButton;

    protected PlayGroundGraphics(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
        this.game = controller.getCurrentPlayer().getGame();

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

        heroName.setText(GameController.toProperCase(game.getHero().toString()));
        endTurnButton.setOnAction(e -> {
            game.endTurn();
            config();
        });
    }

    private void clear() {
        handHBox.getChildren().clear();
        inGameHBox.getChildren().clear();
    }

    @Override
    protected void config() {
        clear();

        hpLabel.setText("HP: " + game.getHero().getHealth());
        manaLabel.setText("Mana: " + game.getMana());
        deckName.setText("deck (" + game.getLeftInDeck().size() + ")");

        for (Card c : game.getHand())
            handHBox.getChildren().add(getButton(c));
        for (Card c : game.getInGame())
            inGameHBox.getChildren().add(getLabel(c));
    }

    private Label getLabel(Card card) {
        return new Label(card.toString());
    }

    private Button getButton(Card card) {
        Button button = new Button(card.toString() + " (" + card.getMana() + ")");
        button.setOnAction(e -> {
            game.playCard(card);
            config();
        });
        return button;
    }

    private boolean confirm() {
        ConfirmationBox confirmationBox = new ConfirmationBox("Your progress will not be saved.\nAre you sure you want to quit the game?", "Proceed", "Cancel");
        confirmationBox.display();
        return confirmationBox.getResponse();
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(PlayGroundGraphics.class.getResource("/fxml/playground.fxml"));
    }

    @Override
    protected void runCd() {
        runner.run(new Command(CommandType.CD, "~/play"));
    }
}
