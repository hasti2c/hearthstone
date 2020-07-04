package graphics.directories.playground;

import gameObjects.cards.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.shape.*;
import java.io.*;

public abstract class CardGraphics <T extends Card> {
    protected final T card;
    protected Group group;
    protected Shape shape;

    protected CardGraphics(T card) {
        this.card = card;
        load();
        shape = getShape();
        shape.setFill(card.getFullImagePattern());
        config();
    }

    protected abstract FXMLLoader getLoader();

    private void load() {
        FXMLLoader loader = getLoader();
        loader.setController(this);
        try {
            group = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void config();

    public Group getGroup() {
        return group;
    }

    protected abstract Shape getShape();
}
