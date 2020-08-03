package client.graphics.directories.playground;

import client.*;
import client.graphics.directories.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import system.*;

public class GameEnding extends Directory {
    private final Game game;
    @FXML
    private Label label;

    public GameEnding(ClientController controller, Client client, Game game) {
        super(controller, client);
        this.game = game;
    }

    @Override
    protected void config() {
        int friendlyHealth = game.getCharacters()[0].getHero().getHealth();
        int enemyHealth = game.getCharacters()[1].getHero().getHealth();
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
