package client.graphics.directories.playground.playables;

import elements.Playable;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.shape.*;
import java.io.*;

public abstract class PlayableGraphics<T extends Playable> {
    protected final T playable;
    protected Group group;
    protected Shape shape;

    protected PlayableGraphics(T playable) {
        this.playable = playable;
        load();
        shape = getShape();
        shape.setFill(playable.getFullImagePattern());
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
