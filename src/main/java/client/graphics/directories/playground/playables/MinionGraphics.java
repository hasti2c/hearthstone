package client.graphics.directories.playground.playables;

import elements.cards.*;
import client.graphics.directories.playground.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.shape.*;

public class MinionGraphics extends PlayableGraphics<Minion> {
    @FXML
    protected Ellipse ellipse;
    @FXML
    private Label healthLabel, attackLabel;
    @FXML
    private ImageView taunt, asleep, divineShield;

    public MinionGraphics(Minion card) {
        super(card);
    }

    protected FXMLLoader getLoader() {
        return new FXMLLoader(GamePlayerGraphics.class.getResource("/fxml/cards/minion.fxml"));
    }

    protected void config() {
        healthLabel.setText(playable.getHealth() + "");
        attackLabel.setText(playable.getAttack() + "");
        taunt.setVisible(playable.getTaunt());
        asleep.setVisible(playable.getAsleep() && !playable.getRush());
        divineShield.setVisible(playable.getDivineShield());
    }

    protected Shape getShape() {
        return ellipse;
    }
}
