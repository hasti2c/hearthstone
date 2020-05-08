package graphics.directories;

import java.util.*;
import com.jfoenix.controls.*;
import controllers.commands.*;
import controllers.game.*;
import gameObjects.*;
import gameObjects.cards.*;
import graphics.*;
import graphics.popups.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class StoreGraphics extends DirectoryGraphics {
    //TODO instance of CardsListGraphics?
    private ArrayList<Card> cards = new ArrayList<>();
    private boolean sellMode = false;
    @FXML
    private JFXToggleButton sellModeButton;
    @FXML
    private GridPane grid;

    StoreGraphics(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
        sellModeButton.setOnAction(e -> {
            if (sellModeButton.selectedProperty().getValue())
                configSellMode();
            else
                configBuyMode();
        });
    }

    protected void config() {
        if (sellMode) {
            sellModeButton.setSelected(true);
            configSellMode();
        } else {
            sellModeButton.setSelected(false);
            configBuyMode();
        }
    }

    private void clear() {
        cards = new ArrayList<>();
        grid.getChildren().clear();
        grid.getRowConstraints().clear();
        grid.getColumnConstraints().clear();
        addRow();
        addRow();
    }

    private void configBuyMode() {
        sellMode = false;
        clear();
        Player currentPlayer = controller.getCurrentPlayer();
        for (Card c : GameController.getCardsList())
            if (!currentPlayer.getAllCards().contains(c))
                cards.add(c);
        arrangeCards();
    }

    private void configSellMode() {
        sellMode = true;
        clear();
        Player currentPlayer = controller.getCurrentPlayer();
        cards.addAll(currentPlayer.getAllCards());
        arrangeCards();
    }

    private void addRow() {
        RowConstraints rc = new RowConstraints(360, 360, 360);
        grid.getRowConstraints().add(rc);
    }

    private void addColumn() {
        ColumnConstraints cc = new ColumnConstraints(240, 240, 240);
        grid.getColumnConstraints().add(cc);
    }

    private VBox configVBox(Card c) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        vBox.getChildren().add(new Label(c.toString()));
        Button button;
        if (sellMode) {
            button = new Button("Sell");
            button.setOnAction(e -> sellAttempt(c));
        } else {
            button = new Button("Buy");
            button.setOnAction(e -> buyAttempt(c));
        }
        vBox.getChildren().add(button);
        return vBox;
    }

    private void arrangeCards() {
        for (int i = 0; i < cards.size(); i++) {
            if (i % 2 == 0)
                addColumn();
            grid.add(configVBox(cards.get(i)), i / 2, i % 2);
        }
    }

    private void buyAttempt(Card c) {
        Player currentPlayer = controller.getCurrentPlayer();
        if (currentPlayer.canBuy(c)) {
            String text = "You are buying \"" + c + "\".\n" +
                    "Current Balance: " + currentPlayer.getBalance() + "\n" +
                    "Card Price: " + c.getPrice() + "\n" +
                    "Balance After Purchase: " + (currentPlayer.getBalance() - c.getPrice());
            ConfirmationBox confirmationBox = new ConfirmationBox(text, "Proceed", "Cancel");
            confirmationBox.display();
            if (confirmationBox.getResponse()) {
                runner.run(new Command(CommandType.BUY, c.toString()));
                configBuyMode();
            }
        } else {
            String text = "You don't have enough money to buy \"" + c + "\".\n" +
                    "Current Balance: " + currentPlayer.getBalance() + "\n" +
                    "Card Price: " + c.getPrice();
            new AlertBox(text, "Okay").display();
        }
    }

    private void sellAttempt(Card c) {
        Player currentPlayer = controller.getCurrentPlayer();
        if (currentPlayer.canSell(c)) {
            String text = "You are selling \"" + c + "\".\n" +
                    "Current Balance: " + currentPlayer.getBalance() + "\n" +
                    "Card Price: " + c.getPrice() + "\n" +
                    "Balance After Purchase: " + (currentPlayer.getBalance() + c.getPrice());
            ConfirmationBox confirmationBox = new ConfirmationBox(text, "Proceed", "Cancel");
            confirmationBox.display();
            if (confirmationBox.getResponse()) {
                runner.run(new Command(CommandType.SELL, c.toString()));
                configSellMode();
            }
        } else {
            String text = "You can't sell \"" + c + "\", because it is in at least one of your decks.";
            new AlertBox(text, "Okay").display();
        }
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(StoreGraphics.class.getResource("/fxml/store.fxml"));
    }

    @Override
    protected void runCd() {
        runner.run(new Command(CommandType.CD, "~/store"));
    }
}
