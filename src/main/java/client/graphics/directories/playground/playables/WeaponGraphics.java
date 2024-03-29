package client.graphics.directories.playground.playables;

import elements.cards.*;
import client.graphics.directories.playground.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.shape.*;

public class WeaponGraphics extends PlayableGraphics<Weapon> {
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
        durabilityLabel.setText(playable.getDurability() + "");
        attackLabel.setText(playable.getAttack() + "");
    }

    protected Shape getShape() {
        return circle;
    }
}
