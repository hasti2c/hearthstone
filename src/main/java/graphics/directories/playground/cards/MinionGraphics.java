package graphics.directories.playground.cards;

import gameObjects.cards.*;
import graphics.directories.playground.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.shape.*;

public class MinionGraphics extends CardGraphics<Minion> {
    @FXML
    protected Ellipse ellipse;
    @FXML
    private Label healthLabel, attackLabel;

    public MinionGraphics(Minion card) {
        super(card);
    }

    protected FXMLLoader getLoader() {
        return new FXMLLoader(GamePlayerGraphics.class.getResource("/fxml/cards/minion.fxml"));
    }

    protected void config() {
        healthLabel.setText(card.getHealth() + "");
        attackLabel.setText(card.getAttack() + "");
    }

    protected Shape getShape() {
        return ellipse;
    }
}
