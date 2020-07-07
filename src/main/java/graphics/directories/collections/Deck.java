package graphics.directories.collections;

import controllers.commands.*;
import elements.cards.*;
import elements.heros.*;
import graphics.*;
import graphics.popups.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

public class Deck extends CardsList {
    private final elements.heros.Deck deck;
    @FXML
    private Label deckCount;

    Deck(elements.heros.Deck deck, GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
        border.setId("deck-bg");
        this.deck = deck;
        initTopHBox();
    }

    protected void config() {
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
            addButton.setOnAction(e -> addCard(card));
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
        runner.run(new Command(CommandType.REMOVE_CARD, deck, card));
        config();
    }

    private void addCard(Card card) {
        if (!runner.run(new Command(CommandType.ADD_CARD, deck, card)))
            (new AlertBox("This card couldn't be added to the deck. This deck is full.", Color.RED, "Okay")).display();
        config();
    }

    protected FXMLLoader getLoader() {
        return new FXMLLoader(Deck.class.getResource("/fxml/directories/deck.fxml"));
    }
}
