package client.graphics.directories;

import client.*;
import client.graphics.directories.collections.*;
import com.jfoenix.controls.*;
import elements.cards.*;
import elements.heros.*;
import commands.*;
import system.player.*;
import client.graphics.popups.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import static commands.types.ServerCommandType.*;

public class Store extends CardsList {
    private boolean sellMode = false;
    @FXML
    private Label balanceLabel;
    @FXML
    private JFXToggleButton sellModeButton;

    public Store(ClientController controller, Client client) {
        super(controller, client);
        border.setId("store-bg");
        initTopHBox();
        setSellMode(false);
        sellModeButton.setOnAction(e -> {
            setSellMode(sellModeButton.isSelected());
            config();
        });
    }

    @Override
    public void config() {
        super.config();
        balanceLabel.setText("Balance: " + controller.getCurrentPlayer().getBalance());
    }

    @Override
    protected boolean validCard(Card card) {
        return (!sellMode && notOwned.contains(card) || sellMode && owned.contains(card));
    }

    protected void setSellMode(boolean sellMode) {
        this.sellMode = sellMode;
        optionsPage.fixOwned(!sellMode, sellMode);
        optionsPage.fixNotOwned(sellMode, !sellMode);
    }

    protected VBox getNode(Card card) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5, 0, 5, 0));
        vBox.setSpacing(5);

        vBox.getChildren().add(card.getImageView(-1, 300));
        Button button;
        if (sellMode) {
            button = new Button("Sell for " + card.getPrice());
            button.setOnAction(e -> sellAttempt(card));
        } else {
            button = new Button("Buy for " + card.getPrice());
            button.setOnAction(e -> buyAttempt(card));
        }
        vBox.getChildren().add(button);
        return vBox;
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
            if (confirmationBox.getResponse())
                client.request(new Command<>(BUY, c));
        } else {
            String text = "You don't have enough money to buy \"" + c + "\".\n" +
                    "Current Balance: " + currentPlayer.getBalance() + "\n" +
                    "Card Price: " + c.getPrice();
            new AlertBox(text, "Okay").display();
        }
        config();
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
            if (confirmationBox.getResponse())
                client.request(new Command<>(SELL, c));
        } else {
            String text = "You can't sell \"" + c + "\", because it is in at least one of your decks.";
            new AlertBox(text, "Okay").display();
        }
        config();
    }

    @Override
    protected boolean validHero(HeroClass hc) {
        return true;
    }

    protected FXMLLoader getLoader() {
        return new FXMLLoader(Store.class.getResource("/fxml/directories/store.fxml"));
    }
}
