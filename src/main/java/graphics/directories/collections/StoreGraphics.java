package graphics.directories.collections;

import controllers.commands.*;
import gameObjects.*;
import gameObjects.cards.*;
import gameObjects.heros.HeroClass;
import graphics.*;
import graphics.popups.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class StoreGraphics extends CardsListGraphics {
    private boolean sellMode = false;

    public StoreGraphics(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
        border.setId("store-bg");
        initTopHBox();
        setSellMode(false);
        sellModeButton.setOnAction(e -> {
            setSellMode(sellModeButton.isSelected());
            config();
        });
    }

    protected void initTopHBox() {
        super.initTopHBox();
        for (int i = 0; i < 2; i++)
            topHBox.getChildren().get(i).setVisible(false);
    }

    @Override
    protected boolean validCard(Card card) {
        return (!sellMode && notOwned.contains(card) || sellMode && owned.contains(card));
    }

    protected void setSellMode(boolean sellMode) {
        this.sellMode = sellMode;
        optionsGraphics.fixOwned(!sellMode, sellMode);
        optionsGraphics.fixNotOwned(sellMode, !sellMode);
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
                runner.run(new Command(CommandType.BUY, c.toString()));
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
                runner.run(new Command(CommandType.SELL, c.toString()));
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

    @Override
    protected void runCd() {
        runner.run(new Command(CommandType.CD, "~/store"));
    }
}
