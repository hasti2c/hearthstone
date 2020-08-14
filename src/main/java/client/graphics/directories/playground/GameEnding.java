package client.graphics.directories.playground;

import client.*;
import client.graphics.directories.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import system.game.*;

public class GameEnding extends Directory {
    private final PlayGround playGround;
    private final Game game;
    @FXML
    private Label label;

    public GameEnding(ClientController controller, Client client, PlayGround playGround) {
        super(controller, client);
        this.playGround = playGround;
        game = playGround.getGame();
    }

    @Override
    public void config() {
        int friendlyHealth = playGround.getMyCharacter().getCharacter().getHero().getHealth();
        int enemyHealth = playGround.getMyCharacter().getOpponent().getCharacter().getHero().getHealth();
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
