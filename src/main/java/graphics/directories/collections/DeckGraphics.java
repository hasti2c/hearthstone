package graphics.directories.collections;

import controllers.commands.*;
import gameObjects.cards.*;
import gameObjects.heros.*;
import graphics.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

public class DeckGraphics extends CardsListGraphics {
    private Deck deck;

    DeckGraphics(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
    }

    void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Deck getDeck() {
        return deck;
    }

    @Override
    protected boolean validCard(Card card) {
        return HeroClass.NEUTRAL.equals(card.getHeroClass()) || deck.getHero().getHeroClass().equals(card.getHeroClass());
    }

    @Override
    protected boolean validHero(HeroClass heroClass) {
        return HeroClass.NEUTRAL.equals(heroClass) || deck.getHero().getHeroClass().equals(heroClass);
    }

    protected VBox getNode(Card card) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);

        if (notOwned.contains(card)) {
            Label name = new Label(card.toString());
            name.setTextFill(Color.LIGHTGRAY);
            vBox.getChildren().add(name);
        } else {
            Label name = new Label(card.toString());
            if (!deck.getCards().contains(card))
                name.setTextFill(Color.DARKGRAY);
            int cnt = 0;
            for (Card c : deck.getCards())
                if (c == card)
                    cnt++;
            Label count = new Label(cnt + "");

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.setSpacing(20);

            Button removeButton = new Button("-");
            Button addButton = new Button("+");
            removeButton.setOnAction(e -> removeCard(card));
            addButton.setOnAction(e -> addCard(card));

            hBox.getChildren().addAll(name, removeButton, count, addButton);
            if (cnt == 0)
                removeButton.setVisible(false);
            else if (cnt == 2)
                addButton.setVisible(false);

            vBox.getChildren().addAll(name, hBox);
        }

        return vBox;
    }

    private void removeCard(Card card) {
        System.out.println(runner.run(new Command(CommandType.REMOVE, card.toString())));
        config();
    }

    private void addCard(Card card) {
        runner.run(new Command(CommandType.ADD, card.toString()));
        config();
    }

    @Override
    protected void runCd() {
        runner.run(new Command(CommandType.CD, "~/collections/" + deck.getHero().toString() + "/" + deck.toString()));
    }
}
