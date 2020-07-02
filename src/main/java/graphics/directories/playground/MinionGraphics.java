package graphics.directories.playground;

import gameObjects.cards.Card;
import gameObjects.cards.Minion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;

import java.io.IOException;

public class MinionGraphics {
    private final Minion minion;
    private Group group;
    @FXML
    private Ellipse ellipse;
    @FXML
    private Label healthLabel, attackLabel;

    MinionGraphics(Minion minion) {
        this.minion = minion;
        load();
        ellipse.setFill(minion.getFullImagePattern());
        config();
    }

    private FXMLLoader getLoader() {
        return new FXMLLoader(GamePlayerGraphics.class.getResource("/fxml/card.fxml"));
    }

    private void load() {
        FXMLLoader loader = getLoader();
        loader.setController(this);
        try {
            group = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void config() {
        healthLabel.setText(minion.getHP() + "");
        attackLabel.setText(minion.getAttack() + "");
    }

    public Group getGroup() {
        return group;
    }
}
