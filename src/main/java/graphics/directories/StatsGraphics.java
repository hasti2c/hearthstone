package graphics.directories;

import java.util.*;
import cli.*;
import controllers.commands.*;
import directories.*;
import gameObjects.heros.*;
import graphics.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;


public class StatsGraphics extends DirectoryGraphics {
    private ArrayList<Deck> decks = new ArrayList<>();
    @FXML
    private GridPane grid;
    @FXML
    private Button homeButton;
    @FXML
    private MenuItem settingsButton, logoutButton, exitButton;

    StatsGraphics(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
    }

    @Override
    protected void config() {
        Home home = controller.getCurrentPlayer().getHome();
        Stats stats = null;
        for (Directory d : home.getChildren())
            if (d instanceof Stats)
                stats = (Stats) d;
        assert stats != null;

        ArrayList<Printable> allDecks = stats.getContent();
        for (int i = 0; i < 10 && i < allDecks.size(); i++) {
            Deck d = (Deck) allDecks.get(i);
            decks.add(d);
            addLabel(d.toString(), 1, i + 1);
            addLabel(d.getHero().toString(), 2, i + 1);
            addLabel(d.getWinPercentage() + "", 3, i + 1);
            addLabel(d.getWins() + "", 4, i + 1);
            addLabel(d.getGames() + "", 5, i + 1);
            addLabel(d.getPriceAverage() + "", 6, i + 1);
            if (d.getBestCard() != null)
                addLabel(d.getBestCard().toString(), 7, i + 1);
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
        return new FXMLLoader(StatsGraphics.class.getResource("/fxml/stats.fxml"));
    }

    @Override
    protected void runCd() {
        runner.run(new Command(CommandType.CD, "~/stats"));
    }
}
