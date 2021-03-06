package client.graphics.directories.playground.targets;

import elements.abilities.targets.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.effect.*;
import javafx.scene.input.*;

public abstract class TargetEventHandler implements EventHandler<MouseEvent> {
    //TODO cards with targets debugged
    protected final Node node;
    protected final Targetable targetable;
    protected boolean isSelected = false;

    protected TargetEventHandler(Targetable targetable, Node node) {
        this.targetable = targetable;
        this.node = node;
    }

    public void initialize() {
        deselect();
        deselectedMode();
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            if (!isSelected)
                select();
            else
                deselect();
        }
        mouseEvent.consume();
    }

    private void select() {
        if (isSelected)
            return;
        isSelected = true;
        setSelectedTargetable(targetable);

        if (!isEnough()) {
            oneSelectedMode();
            enableNode(node);
            node.setEffect(new Bloom());
        } else {
            doAction();
            deselectedMode();
        }
    }

    public void deselect() {
        if (!isSelected)
            return;
        isSelected = false;
        setSelectedTargetable(null);
        node.setEffect(null);
        deselectedMode();
    }

    protected abstract void setSelectedTargetable(Targetable targetable);

    protected abstract boolean isEnough();

    protected abstract void deselectedMode();

    protected abstract void oneSelectedMode();

    protected abstract void doAction();

    public static void enableNode(Node node) {
        if (node == null)
            return;
        node.setDisable(false);
        node.setEffect(new Glow());
    }

    public static void disableNode(Node node) {
        if (node == null)
            return;
        node.setDisable(true);
        node.setEffect(null);
    }
}
