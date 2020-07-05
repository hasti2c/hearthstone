package graphics.directories.playground.cards;

import gameObjects.cards.*;
import graphics.directories.playground.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.shape.*;

public class MinionGraphics extends CardGraphics<Minion> {
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
        healthLabel.setText(card.getHealth() + "");
        attackLabel.setText(card.getAttack() + "");
        taunt.setVisible(card.getTaunt());
        asleep.setVisible(card.getAsleep() && !card.getRush());
        divineShield.setVisible(card.getDivineShield());
    }

    protected Shape getShape() {
        return ellipse;
    }
}
