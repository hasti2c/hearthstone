package graphics.directories.collections;

import java.util.*;
import controllers.commands.*;
import controllers.game.*;
import gameObjects.*;
import gameObjects.heros.*;
import graphics.*;
import graphics.popups.*;
import graphics.directories.*;
import javafx.collections.*;
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
    private VBox vBox;
    @FXML
    private Button allButton, neutralButton;

    public CollectionsGraphics(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
    }

    private void clear() {
        heros = new ArrayList<>();
        decks = new ArrayList<>();

        ObservableList<Node> nodes = vBox.getChildren();
        while (nodes.size() > 1)
            nodes.remove(1);
    }

    protected void config() {
        clear();
        configTopHBox();
        configHeros();
    }

    private void configTopHBox() {
        allButton.setOnAction(e -> displayHeroCards(null));
        neutralButton.setOnAction(e -> displayHeroCards(HeroClass.NEUTRAL));
    }

    //TODO proper upper/lower case change

    private void configHeros() {
        Player currentPlayer = controller.getCurrentPlayer();
        for (Hero h : currentPlayer.getAllHeros()) {
            heros.add(h);
            vBox.getChildren().add(configHeroHbox(h));
        }
    }

    private HBox configHeroHbox(Hero hero) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(20);
        hbox.getChildren().addAll(configHeroTitleVbox(hero), configHeroDecksVbox(hero));
        return hbox;
    }

    private VBox configHeroTitleVbox(Hero hero) {
        VBox heroVBox = new VBox();
        heroVBox.setAlignment(Pos.CENTER);
        heroVBox.setSpacing(20);

        HBox titleHbox = new HBox();
        titleHbox.setAlignment(Pos.CENTER);
        titleHbox.setSpacing(20);
        Label name = new Label(GameController.toProperCase(hero.toString()));
        Node selectHero;
        if (hero == controller.getCurrentPlayer().getCurrentHero())
            selectHero = new Label("Current Hero");
        else {
            selectHero = new Button("Select Hero");
            ((Button) selectHero).setOnAction(e -> selectHero(hero));
        }
        titleHbox.getChildren().addAll(name, selectHero);

        Button viewCards = new Button("View " + GameController.toProperCase(hero.toString()) + " Cards");
        viewCards.setOnAction(e -> displayHeroCards(hero.getHeroClass()));

        heroVBox.getChildren().addAll(titleHbox, viewCards);
        return heroVBox;
    }

    private VBox configHeroDecksVbox(Hero hero) {
        VBox deckHboxes = new VBox();
        deckHboxes.setAlignment(Pos.CENTER_LEFT);
        deckHboxes.setSpacing(20);
        for (Deck deck : hero.getDecks()) {
            decks.add(deck);
            deckHboxes.getChildren().add(configDeckHbox(deck));
        }

        Button addButton = new Button("+");
        addButton.setOnAction(e -> addDeck(hero));
        HBox addHBox = new HBox(addButton);
        addHBox.setAlignment(Pos.CENTER);
        deckHboxes.getChildren().add(addHBox);
        return deckHboxes;
    }

    private HBox configDeckHbox(Deck deck) {
        HBox hbox = new HBox();
        hbox.setSpacing(20);

        Label label = new Label(deck.getHero() + ":" + deck);

        Node selectDeck;
        if (deck.getHero().getCurrentDeck() == deck)
            selectDeck = new Label("Current Deck");
        else {
            selectDeck = new Button("Select Deck");
            ((Button) selectDeck).setOnAction(e -> selectDeck(deck));
        }

        Button view = new Button("View Deck");
        view.setOnAction(e -> displayDeck(deck));

        MenuButton options = new MenuButton();
        MenuItem rename = new MenuItem("Rename");
        MenuItem delete = new MenuItem("Delete");
        rename.setOnAction(e -> renameDeck(deck));
        delete.setOnAction(e -> deleteDeck(deck));
        options.getItems().addAll(rename, delete);

        hbox.getChildren().addAll(label, selectDeck, view, options);
        return hbox;
    }

    private void selectDeck(Deck deck) {
        runner.run(new Command(CommandType.CD, deck.getHero().toString()));
        runner.run(new Command(CommandType.SELECT, deck.toString()));
        runner.run(new Command(CommandType.CD, "~/collections"));
        config();
    }

    private void selectHero(Hero hero) {
        runner.run(new Command(CommandType.SELECT, hero.toString()));
        runner.run(new Command(CommandType.CD, "~/collections"));
        config();
    }

    private void addDeck(Hero hero) {
        QuestionBox questionBox = new QuestionBox("What do you want to name your new deck?", "Done", "Cancel");
        questionBox.display();
        if (questionBox.getButtonResponse()) {
            boolean b = runner.run(new Command(CommandType.CD, hero.toString()));
            b &= runner.run(new Command(CommandType.ADD, questionBox.getText()));
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
        DeckGraphics graphics = new DeckGraphics(controller, runner);
        graphics.setDeck(deck);
        graphics.display();
    }

    private void displayHeroCards(HeroClass heroClass) {
        HeroCardsGraphics graphics = new HeroCardsGraphics(controller, runner);
        graphics.setHeroClass(heroClass);
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
