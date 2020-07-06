package graphics.directories.playground.playables;

import gameObjects.cards.Weapon;
import gameObjects.heros.HeroPower;
import graphics.directories.playground.GamePlayerGraphics;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class HeroPowerGraphics extends PlayableGraphics<HeroPower> {
    @FXML
    protected Circle circle;
    @FXML
    private Label manaLabel;

    public HeroPowerGraphics(HeroPower heroPower) {
        super(heroPower);
    }

    protected FXMLLoader getLoader() {
        return new FXMLLoader(GamePlayerGraphics.class.getResource("/fxml/cards/heroPower.fxml"));
    }

    protected void config() {
        manaLabel.setText("" + playable.getMana());
    }

    protected Shape getShape() {
        return circle;
    }
}
