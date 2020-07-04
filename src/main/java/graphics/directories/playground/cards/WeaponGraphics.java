package graphics.directories.playground.cards;

import gameObjects.cards.*;
import graphics.directories.playground.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.shape.*;

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
