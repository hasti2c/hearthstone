package client.graphics.directories.playground;

import client.*;
import client.graphics.directories.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import system.game.*;

import static system.game.GameEndingType.*;

public class GameEnding extends Directory {
    private final PlayGround playGround;
    private final Game game;
    private final GameEndingType endingType;
    @FXML
    private Label label;

    public GameEnding(ClientController controller, Client client, PlayGround playGround, GameEndingType endingType) {
        super(controller, client);
        this.playGround = playGround;
        game = playGround.getGame();
        this.endingType = endingType;
        config();
    }

    @Override
    public void config() {
        int index = game.indexOf(playGround.getMyCharacter().getCharacter());
        if (endingType == TIE)
            label.setText("IT'S A TIE!");
        else if (endingType.getWinnerIndex() == index)
            label.setText("YOU WIN!");
        else if (endingType.getLoserIndex() == index)
            label.setText("YOU LOSE!");
    }

    @Override
    protected FXMLLoader getLoader() {
        return new FXMLLoader(GameEnding.class.getResource("/fxml/directories/gameEnding.fxml"));
    }
}
