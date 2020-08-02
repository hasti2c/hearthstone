package client.graphics.directories.collections;

import java.util.*;

import client.Client;
import elements.heros.*;
import client.graphics.*;
import client.graphics.popups.*;
import client.graphics.directories.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import shared.commands.Command;
import shared.GameData;

import static shared.commands.types.ServerCommandType.*;

public class Collections extends Directory {
    private ArrayList<Hero> heros;
    private ArrayList<elements.heros.Deck> decks;
    @FXML
    private HBox topHBox1, topHBox2;
    @FXML
    private GridPane grid;
    @FXML
    private Button allButton, neutralButton, addButton;

    public Collections(GraphicsController controller, Client client) {
        super(controller, client);
        addButton.setOnAction(e -> addDeck());
    }

    private void clear() {
        heros = new ArrayList<>();
        decks = new ArrayList<>();

        for (int i = topHBox1.getChildren().size() - 1; i > 1; i--)
            topHBox1.getChildren().remove(i);
        topHBox2.getChildren().clear();

        for (int i = grid.getChildren().size() - 1; i >= 0; i--) {
            Node n = grid.getChildren().get(i);
            if (GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) > 0)
                grid.getChildren().remove(n);
        }
    }

    protected void config() {
        clear();
        configTopHBoxes();
        configDecks();
    }

    private void configTopHBoxes() {
        allButton.setOnAction(e -> displayHeroCards(null));
        neutralButton.setOnAction(e -> displayHeroCards(HeroClass.NEUTRAL));
        for (HeroClass hc : HeroClass.values())
            if (hc != HeroClass.NEUTRAL) {
                Button button = new Button("View " + GameData.getInstance().toProperCase(hc.toString()) + " Cards");
                button.setOnAction(e -> displayHeroCards(hc));
                if (topHBox1.getChildren().size() < 4)
                    topHBox1.getChildren().add(button);
                else
                    topHBox2.getChildren().add(button);
            }
        topHBox2.setVisible(topHBox2.getChildren().size() != 0);
    }

    private void configDecks() {
        for (elements.heros.Deck deck : controller.getCurrentPlayer().getInventory().getAllDecks())
            configDeckRow(deck);
        for (Node n : grid.getChildren()) {
            GridPane.setHalignment(n, HPos.CENTER);
            GridPane.setValignment(n, VPos.CENTER);
        }
    }

    private void configDeckRow(elements.heros.Deck deck) {
        int i = grid.getRowCount();
        grid.add(deck.getHeroClass().getIcon(), 0, i);
        grid.add(new Label(deck.toString()), 1, i);
        grid.add(new Label(deck.getCards().size() + ""), 2, i);

        Node selectDeck;
        if (controller.getCurrentPlayer().getInventory().getCurrentDeck() == deck)
            selectDeck = new Label("Current Deck");
        else {
            selectDeck = new Button ("Select Deck");
            ((Button) selectDeck).setOnAction(e -> selectDeck(deck));
        }
        grid.add(selectDeck, 3, i);

        Button view = new Button("View Deck");
        view.setOnAction(e -> displayDeck(deck));
        grid.add(view, 4, i);

        MenuButton options = new MenuButton();
        MenuItem rename = new MenuItem("Rename");
        MenuItem changeHero = new MenuItem("Change Hero");
        MenuItem delete = new MenuItem("Delete");
        rename.setOnAction(e -> renameDeck(deck));
        changeHero.setOnAction(e -> changeHero(deck));
        delete.setOnAction(e -> deleteDeck(deck));
        options.getItems().addAll(rename, changeHero, delete);
        grid.add(options, 5, i);
    }

    private void selectDeck(elements.heros.Deck deck) {
        client.request(new Command<>(SELECT, deck));
        config();
    }

    private void addDeck() {
        ArrayList<String> heroStrings = new ArrayList<>();
        for (Hero h : controller.getCurrentPlayer().getInventory().getAllHeros())
            heroStrings.add(h.toString());

        OptionAndQuestionBox optionAndQuestionBox = new OptionAndQuestionBox("Type in the name of your new deck and choose its hero.", "Done", "Cancel", heroStrings);
        optionAndQuestionBox.display();

        if (optionAndQuestionBox.getButtonResponse()) {
            if (!client.request(new Command<>(ADD_DECK, optionAndQuestionBox.getHeroChoice(), optionAndQuestionBox.getDeckName())))
                deckNameError();
        }
        config();
    }

    private void renameDeck(elements.heros.Deck deck) {
        QuestionBox questionBox = new QuestionBox("What is the name you want to set for " + deck.toString() + "?", "Done", "Cancel");
        questionBox.display();
        if (questionBox.getButtonResponse()) {
            if (!client.request(new Command<>(RENAME, deck, questionBox.getText())))
                deckNameError();
        }
        config();
    }

    private void changeHero(elements.heros.Deck deck) {
        ArrayList<String> heroStrings = new ArrayList<>();
        for (Hero h : controller.getCurrentPlayer().getInventory().getAllHeros())
            heroStrings.add(h.toString());
        OptionBox optionBox = new OptionBox("To which hero do you want to move " + deck + "?", "Done", "Cancel", heroStrings);
        optionBox.display();
        if (optionBox.getButtonResponse()) {
            HeroClass heroClass = HeroClass.valueOf(optionBox.getChoice().toUpperCase());
            if (!client.request(new Command<>(MOVE, deck, heroClass))) {
                String alert = """
                        Hero change couldn't be done.
                        Possible reasons include:
                           - Selected hero already has a deck with that name.
                           - There are cards in this deck that can't be used by the selected Hero.
                        """;

                (new AlertBox(alert, Color.RED, "Okay")).display();
            }
        }
        config();
    }

    private void deleteDeck(elements.heros.Deck deck) {
        ConfirmationBox confirmationBox = new ConfirmationBox("Are you sure you want to delete the deck " + deck.toString() + "?", "Yes", "No");
        confirmationBox.display();
        if (confirmationBox.getResponse())
            client.request(new Command<>(REMOVE_DECK, deck));
        config();
    }

    private void displayDeck(elements.heros.Deck deck) {
        DeckGraphics graphics = new DeckGraphics(deck, controller, client);
        graphics.display();
    }

    private void displayHeroCards(HeroClass heroClass) {
        HeroCards graphics = new HeroCards(heroClass, controller, client);
        graphics.display();
    }

    private void deckNameError() {
        String alert = """
                That's not a valid name for a deck.
                Possible reasons include:
                   - Characters other than A-Z, a-z, 0-9, _, . and space are used.
                   - This hero already has a deck with that name.
                """;
        new AlertBox(alert, Color.RED, "Okay").display();
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(Collections.class.getResource("/fxml/directories/collections.fxml"));
    }
}
