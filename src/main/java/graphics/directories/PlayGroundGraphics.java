package graphics.directories;

import controllers.commands.Command;
import controllers.commands.CommandRunner;
import controllers.commands.CommandType;
import gameObjects.Game;
import gameObjects.cards.Card;
import graphics.*;
import graphics.popups.*;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class PlayGroundGraphics extends DirectoryGraphics {
    private Game game;
    @FXML
    private Pane pane;
    @FXML
    private Label deckLabel, hpLabel, manaLabel, weaponLabel;
    @FXML
    private HBox handHBox1, handHBox2, manaHBox, minionsHBox;
    @FXML
    private Button endTurnButton, heroPowerButton;
    @FXML
    private ImageView heroImage;

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

        heroImage.setImage(game.getHero().getGameImage());
        endTurnButton.setOnAction(e -> {
            game.endTurn();
            config();
        });
        heroPowerButton.setOnAction(e -> {
            game.useHeroPower();
            config();
        });
    }

    private void clear() {
        handHBox1.getChildren().clear();
        handHBox2.getChildren().clear();
        minionsHBox.getChildren().clear();
    }

    @Override
    protected void config() {
        clear();

        hpLabel.setText(game.getHero().getHealth() + "");
        manaLabel.setText(game.getMana() + "/10");
        for (int i = 0; i < game.getMana(); i++)
            manaHBox.getChildren().get(i).setVisible(true);
        for (int i = game.getMana(); i < 10; i++)
            manaHBox.getChildren().get(i).setVisible(false);
        deckLabel.setText(game.getLeftInDeck().size() + "/" + controller.getCurrentPlayer().getCurrentHero().getCurrentDeck().getCards().size());

        configHand();
        for (Card c : game.getMinionsInGame())
            minionsHBox.getChildren().add(c.getImageView(-1, 135));
        if (game.getCurrentWeapon() != null)
            weaponLabel.setText(game.getCurrentWeapon().toString());
        else
            weaponLabel.setText("");
        if (game.isHeroPowerUsed()) {
            heroPowerButton.setText("used");
            heroPowerButton.setDisable(true);
        } else {
            heroPowerButton.setText(game.getHero().getHeroPower().toString());
            heroPowerButton.setDisable(false);
        }
    }

    private void configHand() {
        ArrayList<Card> hand = game.getHand();
        for (int i = 0; i < hand.size(); i++) {
            ImageView iv = getImageView(hand.get(i));
            if (i < 5)
                handHBox1.getChildren().add(iv);
            else
                handHBox2.getChildren().add(iv);
        }
        if (hand.size() < 5)
            handHBox2.setVisible(false);
        else {
            handHBox2.setVisible(true);
            handHBox2.setLayoutX(640 - handHBox2.getChildren().size() * 30);
        }
    }

    private ImageView getImageView(Card card) {
        ImageView iv;
        int n = game.getHand().size();
        if (n <= 5)
            iv = card.getImageView(Math.min(300 / n, 100), -1);
        else
            iv = card.getImageView(60, -1);

        HandEventHandler eventHandler = new HandEventHandler(card, iv);
        iv.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
        iv.addEventHandler(MouseEvent.MOUSE_ENTERED, eventHandler);
        iv.addEventHandler(MouseEvent.MOUSE_EXITED, eventHandler);
        return iv;
    }

    private boolean confirm() {
        ConfirmationBox confirmationBox = new ConfirmationBox("Your progress will not be saved.\nAre you sure you want to quit the game?", "Proceed", "Cancel");
        confirmationBox.display();
        return confirmationBox.getResponse();
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(PlayGroundGraphics.class.getResource("/fxml/playGround.fxml"));
    }

    @Override
    protected void runCd() {
        runner.run(new Command(CommandType.CD, "~/play"));
    }

    private class HandEventHandler implements EventHandler<MouseEvent> {
        private Card card;
        private ImageView normalImageView, bigImageView;

        private HandEventHandler(Card card, ImageView normalImageView) {
            this.card = card;
            this.normalImageView = normalImageView;
            bigImageView = card.getImageView(-1, 200);
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
                game.playCard(card);
                config();
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED) {
                pane.getChildren().add(bigImageView);
                bigImageView.setLayoutX(getLayoutX() + getWidth(normalImageView) / 2 - getWidth(bigImageView) / 2);
                bigImageView.setLayoutY(getLayoutY() - getHeight(bigImageView));
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED) {
                pane.getChildren().remove(bigImageView);
            }
            mouseEvent.consume();
        }

        private double getLayoutX() {
            if (handHBox1.getChildren().contains(normalImageView))
                return handHBox1.getLayoutX() + normalImageView.getLayoutX();
            else if (handHBox2.getChildren().contains(normalImageView))
                return handHBox2.getLayoutX() + normalImageView.getLayoutX();
            return 0;
        }

        private double getLayoutY() {
            if (handHBox1.getChildren().contains(normalImageView))
                return handHBox1.getLayoutY();
            else if (handHBox2.getChildren().contains(normalImageView))
                return handHBox2.getLayoutY();
            return 0;
        }

        private double getWidth(ImageView iv) {
            if (iv.getFitWidth() != 0 || iv.getFitHeight() == 0)
                return iv.getFitWidth();
            return iv.getFitHeight() * iv.getImage().getWidth() / iv.getImage().getHeight();
        }

        private double getHeight(ImageView iv) {
            if (iv.getFitHeight() != 0 || iv.getFitWidth() == 0)
                return iv.getFitHeight();
            return iv.getFitWidth() * iv.getImage().getHeight() / iv.getImage().getWidth();
        }
    }
}
