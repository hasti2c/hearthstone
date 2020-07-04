package graphics.directories;

import java.util.*;
import controllers.commands.*;
import gameObjects.heros.*;
import graphics.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class Stats extends Directory {
    private ArrayList<Deck> decks;
    @FXML
    private GridPane grid;
    @FXML
    private Button homeButton;
    @FXML
    private MenuItem settingsButton, logoutButton, exitButton;

    Stats(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
    }

    @Override
    protected void config() {
        decks = controller.getCurrentPlayer().getInventory().getAllDecks();
        Collections.sort(decks);
        if (decks.size() > 10)
            decks = new ArrayList<>(decks.subList(0, 10));
        for (int i = 0; i < decks.size(); i++) {
            Deck deck = decks.get(i);
            addLabel(deck.toString(), 1, i + 1);
            addLabel(deck.getHeroClass().toString().toLowerCase(), 2, i + 1);
            addLabel(deck.getWinPercentage() + "", 3, i + 1);
            addLabel(deck.getWins() + "", 4, i + 1);
            addLabel(deck.getGames() + "", 5, i + 1);
            addLabel(deck.getPriceAverage() + "", 6, i + 1);
            if (deck.getBestCard() != null)
                addLabel(deck.getBestCard().toString(), 7, i + 1);
        }
    }

    private void addLabel(String s, int r, int c) {
        Label l = new Label(s);
        l.setFont(Font.font(18));
        GridPane.setHalignment(l, HPos.CENTER);
        GridPane.setValignment(l, VPos.CENTER);
        grid.add(l, r, c);
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(Stats.class.getResource("/fxml/directories/stats.fxml"));
    }
}
