package client.graphics.directories.playground.playables;

import elements.heros.*;
import client.graphics.directories.playground.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.shape.*;

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
