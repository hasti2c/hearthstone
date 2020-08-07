package client.graphics.directories.playground.targets;

import client.graphics.directories.playground.*;
import elements.cards.*;
import javafx.event.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import shared.Pair;
import system.player.*;

public class HandEventHandler implements EventHandler<MouseEvent> {
    private final Card card;
    private final ImageView normalImageView;
    private final ImageView bigImageView;
    private final GamePlayerGraphics gamePlayer;

    public HandEventHandler(GamePlayerGraphics gamePlayer, Card card, ImageView normalImageView) {
        this.gamePlayer = gamePlayer;
        this.card = card;
        this.normalImageView = normalImageView;
        bigImageView = card.getImageView(-1, 200);
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            gamePlayer.playCard(card);
        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED) {
            gamePlayer.getPane().getChildren().add(bigImageView);
            bigImageView.setLayoutX(getLayoutX() + getWidth(normalImageView) / 2 - getWidth(bigImageView) / 2);
            if (gamePlayer.getCharacter().getPlayerFaction() == PlayerFaction.FRIENDLY)
                bigImageView.setLayoutY(getLayoutY() - getHeight(bigImageView));
            else
                bigImageView.setLayoutY(getLayoutY() + getHeight(bigImageView) - getHeight(normalImageView) / 2);
        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED)
            gamePlayer.getPane().getChildren().remove(bigImageView);
        mouseEvent.consume();
    }

    private double getLayoutX() {
        Pair<HBox, HBox> handHBoxes = gamePlayer.getHandHBoxes();
        if (handHBoxes.getFirst().getChildren().contains(normalImageView))
            return handHBoxes.getFirst().getLayoutX() + normalImageView.getLayoutX();
        else if (gamePlayer.getHandHBoxes().getSecond().getChildren().contains(normalImageView))
            return gamePlayer.getHandHBoxes().getSecond().getLayoutX() + normalImageView.getLayoutX();
        return 0;
    }

    private double getLayoutY() {
        Pair<HBox, HBox> handHBoxes = gamePlayer.getHandHBoxes();
        if (handHBoxes.getFirst().getChildren().contains(normalImageView))
            return handHBoxes.getFirst().getLayoutY();
        else if (gamePlayer.getHandHBoxes().getSecond().getChildren().contains(normalImageView))
            return gamePlayer.getHandHBoxes().getSecond().getLayoutY();
        return 0;
    }

    private double getWidth(ImageView iv) {
        if (iv.getFitWidth() != 0 || iv.getFitHeight() == 0)
            return iv.getFitWidth();
        return iv.getFitHeight() * iv.getImage().getWidth() / iv.getImage().getHeight();
    }

    private double getHeight(ImageView iv) {
        if (iv.getFitHeight() != 0 || iv.getFitWidth() == 0)
            return iv.getFitHeight();
        return iv.getFitWidth() * iv.getImage().getHeight() / iv.getImage().getWidth();
    }
}
