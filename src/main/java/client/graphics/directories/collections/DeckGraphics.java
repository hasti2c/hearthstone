package client.graphics.directories.collections;

import client.*;
import commands.*;
import elements.cards.*;
import elements.heros.*;
import client.graphics.popups.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

import static commands.types.ServerCommandType.*;

public class DeckGraphics extends CardsList {
    private final elements.heros.Deck deck;
    @FXML
    private Label deckCount;

    DeckGraphics(elements.heros.Deck deck, ClientController controller, Client client) {
        super(controller, client);
        border.setId("deck-bg");
        this.deck = deck;
        initTopHBox();
    }

    public void config() {
        super.config();
        deckCount.setText(deck.getCards().size() + "/" + controller.getCurrentPlayer().getInventory().getDeckCap());
    }

    public elements.heros.Deck getDeck() {
        return deck;
    }

    @Override
    protected boolean validCard(Card card) {
        return HeroClass.NEUTRAL.equals(card.getHeroClass()) || deck.getHeroClass().equals(card.getHeroClass());
    }

    @Override
    protected boolean validHero(HeroClass heroClass) {
        return HeroClass.NEUTRAL.equals(heroClass) || deck.getHeroClass().equals(heroClass);
    }

    protected VBox getNode(Card card) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(5, 0, 5, 0));
        vBox.setSpacing(5);


        vBox.getChildren().add(card.getImageView(-1, 300));
        if (owned.contains(card)) {
            int cnt = 0;
            for (Card c : deck.getCards())
                if (c == card)
                    cnt++;
            Label count = new Label(cnt + "");
            count.getStyleClass().add("add-remove");

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.setSpacing(20);
            hBox.setPadding(new Insets(5));

            Button removeButton = new Button("-");
            Button addButton = new Button("+");
            removeButton.setOnAction(e -> removeCard(card));
            addButton.setOnAction(e -> client.request(new Command<>(ADD_CARD, deck, card)));
            removeButton.getStyleClass().add("add-remove");
            addButton.getStyleClass().add("add-remove");

            hBox.getChildren().addAll(removeButton, count, addButton);
            if (cnt == 0)
                removeButton.setVisible(false);
            else if (cnt == 2)
                addButton.setVisible(false);
            vBox.getChildren().add(hBox);
        } else {
            Button buy = new Button("View In Store");
            buy.setOnAction(e -> controller.viewCardInStore(card));
            vBox.getChildren().add(buy);
        }
        return vBox;
    }

    private void removeCard(Card card) {
        client.request(new Command<>(REMOVE_CARD, deck, card));
        config();
    }

    protected FXMLLoader getLoader() {
        return new FXMLLoader(DeckGraphics.class.getResource("/fxml/directories/deck.fxml"));
    }
}
