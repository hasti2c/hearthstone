package graphics.directories.collections;

import java.util.*;
import controllers.commands.*;
import controllers.game.*;
import gameObjects.heros.*;
import graphics.*;
import graphics.popups.*;
import graphics.directories.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

public class CollectionsGraphics extends DirectoryGraphics {
    private ArrayList<Hero> heros;
    private ArrayList<Deck> decks;
    @FXML
    private HBox topHBox;
    @FXML
    private GridPane grid;
    @FXML
    private Button allButton, neutralButton, addButton;

    public CollectionsGraphics(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
        addButton.setOnAction(e -> addDeck());
    }

    private void clear() {
        heros = new ArrayList<>();
        decks = new ArrayList<>();

        for (int i = topHBox.getChildren().size() - 1; i > 1; i--)
            topHBox.getChildren().remove(i);

        for (int i = grid.getChildren().size() - 1; i >= 0; i--) {
            Node n = grid.getChildren().get(i);
            if (GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) > 0)
                grid.getChildren().remove(n);
        }
    }

    protected void config() {
        clear();
        configTopHBox();
        configDecks();
    }

    private void configTopHBox() {
        allButton.setOnAction(e -> displayHeroCards(null));
        neutralButton.setOnAction(e -> displayHeroCards(HeroClass.NEUTRAL));
        for (HeroClass hc : HeroClass.values())
            if (hc != HeroClass.NEUTRAL) {
                Button button = new Button("View " + GameController.toProperCase(hc.toString()) + " Cards");
                button.setOnAction(e -> displayHeroCards(hc));
                topHBox.getChildren().add(button);
            }
    }

    private void configDecks() {
        for (Deck deck : controller.getCurrentPlayer().getAllDecks())
            configDeckRow(deck);
        for (Node n : grid.getChildren()) {
            GridPane.setHalignment(n, HPos.CENTER);
            GridPane.setValignment(n, VPos.CENTER);
        }
    }

    private void configDeckRow(Deck deck) {
        int i = grid.getRowCount();
        grid.add(deck.getHero().getHeroClass().getIcon(), 0, i);
        grid.add(new Label(deck.toString()), 1, i);
        grid.add(new Label(deck.getCards().size() + ""), 2, i);

        Node selectDeck;
        if (controller.getCurrentPlayer().getCurrentDeck() == deck)
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

    private void selectDeck(Deck deck) {
        runner.run(new Command(CommandType.SELECT, deck.getHero().toString()));
        runner.run(new Command(CommandType.CD, deck.getHero().toString()));
        runner.run(new Command(CommandType.SELECT, deck.toString()));
        runner.run(new Command(CommandType.CD, "~/collections"));
        config();
    }

    private void addDeck() {
        ArrayList<String> heroStrings = new ArrayList<>();
        for (Hero h : controller.getCurrentPlayer().getAllHeros())
            heroStrings.add(h.toString());

        OptionAndQuestionBox optionAndQuestionBox = new OptionAndQuestionBox("Type in the name of your new deck and choose its hero.", "Done", "Cancel", heroStrings);
        optionAndQuestionBox.display();

        if (optionAndQuestionBox.getButtonResponse()) {
            boolean b = runner.run(new Command(CommandType.CD, optionAndQuestionBox.getHeroChoice()));
            b &= runner.run(new Command(CommandType.ADD, optionAndQuestionBox.getDeckName()));
            b &= runner.run(new Command(CommandType.CD, ".."));
            if (!b)
                deckNameError();
        }
        config();
    }

    private void renameDeck(Deck deck) {
        QuestionBox questionBox = new QuestionBox("What is the name you want to set for " + deck.toString() + "?", "Done", "Cancel");
        questionBox.display();
        if (questionBox.getButtonResponse()) {
            boolean b = runner.run(new Command(CommandType.CD, deck.getHero().toString()));
            b &= runner.run(new Command(CommandType.MV, deck.toString() + ":" + questionBox.getText()));
            b &= runner.run(new Command(CommandType.CD, ".."));
            if (!b)
                deckNameError();
        }
        config();
    }

    private void changeHero(Deck deck) {
        ArrayList<String> heroStrings = new ArrayList<>();
        for (Hero h : controller.getCurrentPlayer().getAllHeros())
            heroStrings.add(h.toString());
        OptionBox optionBox = new OptionBox("To which hero do you want to move " + deck + "?", "Done", "Cancel", heroStrings);
        optionBox.display();
        if (optionBox.getButtonResponse()) {
            String heroName = optionBox.getChoice();
            boolean ret = runner.run(new Command(CommandType.CD, deck.getHero().toString()));
            ret &= runner.run(new Command(CommandType.MV, deck + ":../" + heroName + "/" + deck));
            if (!ret) {
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

    private void deleteDeck(Deck deck) {
        ConfirmationBox confirmationBox = new ConfirmationBox("Are you sure you want to delete the deck " + deck.toString() + "?", "Yes", "No");
        confirmationBox.display();
        if (confirmationBox.getResponse()) {
            runner.run(new Command(CommandType.CD, deck.getHero().toString()));
            runner.run(new Command(CommandType.REMOVE, deck.toString()));
            runner.run(new Command(CommandType.CD, ".."));
        }
        config();
    }

    private void displayDeck(Deck deck) {
        DeckGraphics graphics = new DeckGraphics(deck, controller, runner);
        graphics.display();
    }

    private void displayHeroCards(HeroClass heroClass) {
        HeroCardsGraphics graphics = new HeroCardsGraphics(heroClass, controller, runner);
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
        return new FXMLLoader(CollectionsGraphics.class.getResource("/fxml/collections.fxml"));
    }

    @Override
    protected void runCd() {
        runner.run(new Command(CommandType.CD, "~/collections"));
    }
}
