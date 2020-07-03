package graphics.directories.playground.cards;

import gameObjects.cards.Minion;
import gameObjects.cards.Weapon;
import graphics.directories.playground.GamePlayerGraphics;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;

public class WeaponGraphics extends CardGraphics<Weapon> {
    @FXML
    protected Circle circle;
    @FXML
    private Label durabilityLabel, attackLabel;

    public WeaponGraphics(Weapon card) {
        super(card);
    }

    protected FXMLLoader getLoader() {
        return new FXMLLoader(GamePlayerGraphics.class.getResource("/fxml/cards/weapon.fxml"));
    }

    protected void config() {
        durabilityLabel.setText(card.getDurability() + "");
        attackLabel.setText(card.getAttack() + "");
    }

    protected Shape getShape() {
        return circle;
    }
}
