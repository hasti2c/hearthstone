package client.graphics.directories.playground;

import client.*;
import commands.*;
import elements.abilities.targets.*;
import elements.cards.*;
import javafx.event.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import system.*;
import client.graphics.directories.*;
import client.graphics.popups.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import system.player.*;

import java.io.IOException;
import java.util.ArrayList;

import static commands.types.ServerCommandType.*;

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

    public PlayGround(Game game, ClientController controller, Client client) {
        super(controller, client);
        this.game = game;
        for (int i = 0; i < 2; i++) {
            if (game.getCharacters()[i] instanceof GamePlayer gamePlayer)
                characters[i] = new GamePlayerGraphics(this, client, gamePlayer);
            else
                characters[i] = new NPCGraphics(this, client, (NPC) game.getCharacters()[i]);
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
            client.request(new Command<>(END_TURN));
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

        if (!game.isDeckReader())
            (new ChooseCards()).display();
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
        GameEnding gameEnd = new GameEnding(controller, client, game);
        gameEnd.display();
    }

    public Button getEndTurnButton() {
        return endTurnButton;
    }

    private class ChooseCards {
        private Pane pane;
        private ArrayList<Card> cards, mainCards, extraCards;
        @FXML
        private HBox cardsHBox;
        @FXML
        private Button continueButton;

        private ChooseCards() {
            ArrayList<Card> cards = new ArrayList<>();
            while (cards.size() < 6) {
                Card card = Card.getRandomElement(characters[0].getCharacter().getLeftInDeck());
                if (!cards.contains(card))
                    cards.add(card);
            }
            mainCards = new ArrayList<>(cards.subList(0, 3));
            extraCards = new ArrayList<>(cards.subList(3, 6));
            this.cards = mainCards;

            load();
            continueButton.setOnAction(e -> hide());
        }

        private void clear() {
            cardsHBox.getChildren().clear();
        }

        private void config() {
            clear();
            for (Card card : cards) {
                ImageView iv = card.getImageView(250, -1);
                cardsHBox.getChildren().add(iv);
                iv.addEventHandler(MouseEvent.MOUSE_CLICKED, new ChooseCardsEventHandler(card));
            }
        }

        private void display() {
            config();
            showDiscover(pane);
        }

        private void hide() {
            PlayGround.this.pane.getChildren().remove(pane);
            client.request(new Command<>(START_GAME, cards));
            PlayGround.this.config();
        }

        private void load() {
            FXMLLoader loader = new FXMLLoader(DiscoverGraphics.class.getResource("/fxml/popups/chooseCards.fxml"));
            loader.setController(this);
            try {
                pane = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private class ChooseCardsEventHandler implements EventHandler<MouseEvent> {
            Card card;

            private ChooseCardsEventHandler(Card card) {
                this.card = card;
            }

            @Override
            public void handle(MouseEvent event) {
                if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    if (mainCards.contains(card)) {
                        int i = mainCards.indexOf(card);
                        cards.set(i, extraCards.get(i));
                        config();
                    }
                }
            }
        }
    }
}
