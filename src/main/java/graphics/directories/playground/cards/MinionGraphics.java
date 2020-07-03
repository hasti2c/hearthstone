package graphics.directories.playground.cards;

import gameObjects.cards.Minion;
import graphics.directories.playground.GamePlayerGraphics;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;

import java.io.IOException;

public class MinionGraphics extends CardGraphics <Minion> {
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
