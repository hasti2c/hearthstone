package client.graphics.directories.playground;

import client.*;
import client.graphics.directories.playground.targets.*;
import commands.*;
import elements.cards.*;
import javafx.event.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import client.graphics.directories.*;
import client.graphics.popups.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import system.game.*;
import system.game.characters.*;

import java.io.*;
import java.util.*;

import static commands.types.ServerCommandType.*;
import static system.game.GameEndingType.*;
import static system.game.GameType.*;

public class PlayGround extends Directory {
    private final Game game;
    private final CharacterGraphics<?>[] characters = new CharacterGraphics<?>[2];
    private int time = 60;
    private final Timer timer;
    private int gameIndex;
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

    public PlayGround(Game game, ClientController controller, Client client, int gameIndex) {
        super(controller, client);
        this.game = game;
        this.gameIndex = gameIndex;
        for (int i = 0; i < 2; i++) {
            if (game.getCharacters()[i] instanceof GamePlayer gamePlayer)
                characters[i] = new GamePlayerGraphics(this, client, gamePlayer, i == gameIndex || gameIndex == -1);
            else
                characters[i] = new NPCGraphics(this, client, (NPC) game.getCharacters()[i], i == gameIndex || gameIndex == -1);
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

        endTurnButton.setOnAction(e -> requestEndTurn());

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
        this.timer = new Timer(this);
    }

    @Override
    public void config() {
        for (CharacterGraphics<?> character : characters)
            character.config();
        configTime();
        configEndTurnButton();
        gameEventsLabel.setText("Game Events:\n" + game.getGameEvents());
    }

    private void configTime() {
        boolean isMyTurn = getMyCharacter().getCharacter().isMyTurn() || !game.getType().isMultiPlayer();
        timerLabel.setVisible(isMyTurn);

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

    public void nextSecond() {
        time--;
        if (time <= 0)
            requestEndTurn();
        else
            configTime();
    }

    private void configEndTurnButton() {
        boolean isMyTurn = getMyCharacter().getCharacter().isMyTurn() || !game.getType().isMultiPlayer();
        endTurnButton.setDisable(!isMyTurn);
        if (isMyTurn)
            endTurnButton.setText("End Turn");
        else
            endTurnButton.setText("Waiting...");
    }

    private boolean confirm() {
        ConfirmationBox confirmationBox = new ConfirmationBox("Your progress will not be saved.\nAre you sure you want to quit the game?", "Proceed", "Cancel");
        confirmationBox.display();
        return confirmationBox.getResponse();
    }

    public CharacterGraphics<?> getCurrentCharacter() {
        return characters[game.getCurrentPlayerNumber()];
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

    public void endGame(GameEndingType endingType) {
        if (endingType != NONE) {
            GameEnding gameEnd = new GameEnding(controller, client, this, endingType);
            gameEnd.display();
        }
        timer.exit();
    }

    public Button getEndTurnButton() {
        return endTurnButton;
    }

    public Game getGame() {
        return game;
    }

    public void startTimer() {
        if (!timer.isAlive()) {
            time = 60;
            timer.start();
        }
    }

    private void requestEndTurn() {
        client.request(new Command<>(END_TURN));
    }

    public void doEndTurn() {
        game.doEndTurn();
        timer.exit();
        time = 60;
        timer.restart();
    }

    public CharacterGraphics<?> getOpponent(CharacterGraphics<?> character) {
        if (character == characters[0])
            return characters[1];
        return character == characters[1] ? characters[0] : null;
    }

    //TODO less than 6 cards has bug
    private void displayChooseCards() {
        (new ChooseCards()).display();
    }

    public void display() {
        super.display();
        if (game.getType() != DECK_READER)
            displayChooseCards();
        else
            client.request(new Command<>(START_GAME));
    }

    public CharacterGraphics<?> getMyCharacter() {
        for (CharacterGraphics<?> character : characters)
            if (character.getIsSelf())
                return character;
        return null;
    }

    public void leaveGame() {
        client.request(new Command<>(LEAVE_GAME));
    }

    private class ChooseCards {
        private Pane pane;
        private final ArrayList<Card> cards, mainCards, extraCards;
        @FXML
        private HBox cardsHBox;
        @FXML
        private Button continueButton;

        private ChooseCards() {
            ArrayList<Card> cards = new ArrayList<>(), leftInDeck = getMyCharacter().getCharacter().getState().getLeftInDeck();
            while (cards.size() < 6 && leftInDeck.size() > 0) {
                Card card = Card.getRandomElement(leftInDeck);
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
                iv.setOnMouseClicked(new ChooseCardsEventHandler(card));
            }
        }

        private void display() {
            config();
            showDiscover(pane);
        }

        private void hide() {
            PlayGround.this.pane.getChildren().remove(pane);
            client.request(new Command<>(START_GAME, cards.toArray()));
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
