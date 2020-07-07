package graphics.directories.playground;

import controllers.commands.CommandRunner;
import graphics.GraphicsController;
import graphics.directories.Directory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import system.Game;

public class GameEnding extends Directory {
    private Game game;
    @FXML
    private Label label;

    public GameEnding(GraphicsController controller, CommandRunner runner, Game game) {
        super(controller, runner);
        this.game = game;
    }

    @Override
    protected void config() {
        int friendlyHealth = game.getGamePlayers()[0].getInventory().getCurrentHero().getHealth();
        int enemyHealth = game.getGamePlayers()[1].getInventory().getCurrentHero().getHealth();
        if (friendlyHealth <= 0 && enemyHealth <= 0)
            label.setText("IT'S A TIE!");
        else if (friendlyHealth <= 0)
            label.setText("YOU LOSE!");
        else if (enemyHealth <= 0)
            label.setText("YOU WIN!");
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(GameEnding.class.getResource("/fxml/directories/gameEnding.fxml"));
    }
}
