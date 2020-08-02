package client.graphics.directories.playground;

import client.Client;
import shared.commands.*;
import elements.abilities.targets.Attackable;
import elements.abilities.targets.TargetEventHandler;
import elements.abilities.targets.Targetable;
import elements.cards.*;
import elements.heros.*;
import javafx.scene.control.Button;
import system.player.GamePlayer;
import system.player.PlayerFaction;
import client.graphics.directories.playground.playables.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;

import static shared.commands.types.ServerCommandType.*;

public class GamePlayerGraphics extends CharacterGraphics<GamePlayer> {
    private Attackable selectedAttackable;

    GamePlayerGraphics(PlayGround playGround, Client client, GamePlayer gamePlayer) {
        super(playGround, client, gamePlayer);
    }

    protected void configMana() {
        manaLabel.setText(character.getMana() + "/10");
        for (int i = 0; i < character.getMana(); i++)
            manaHBox.getChildren().get(i).setVisible(true);
        for (int i = character.getMana(); i < 10; i++)
            manaHBox.getChildren().get(i).setVisible(false);
    }

    protected void configHero() {
        Hero hero = character.getHero();
        Node node = heroImagePane.getChildren().get(0);
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, new AttackEventHandler(hero, node));
    }

    protected void configHandNode(Card card, ImageView imageView) {
        GamePlayerGraphics.HandEventHandler eventHandler = new GamePlayerGraphics.HandEventHandler(card, imageView);
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
        imageView.addEventHandler(MouseEvent.MOUSE_ENTERED, eventHandler);
        imageView.addEventHandler(MouseEvent.MOUSE_EXITED, eventHandler);
    }

    protected void configTargetNode(Minion minion, Node node) {
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, new GamePlayerGraphics.AttackEventHandler(minion, node));
    }

    protected Node getHeroPowerNode(HeroPower heroPower) {
        Group group = (new HeroPowerGraphics(heroPower)).getGroup();
        group.setOnMouseClicked(e -> {
            character.useHeroPower();
            if (!heroPower.needsTarget())
                playGround.config();
        });
        return group;
    }

    private Attackable getSelectedAttackable() {
            return selectedAttackable;
    }

    protected void configEndTurnButton() {
        Button button = playGround.getEndTurnButton();
        button.setDisable(false);
        button.setText("End Turn");
    }

    private class HandEventHandler implements EventHandler<MouseEvent> {
        private final Card card;
        private final ImageView normalImageView;
        private final ImageView bigImageView;

        private HandEventHandler(Card card, ImageView normalImageView) {
            this.card = card;
            this.normalImageView = normalImageView;
            bigImageView = card.getImageView(-1, 200);
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
                client.request(new Command<>(PLAY, card));
                if (!card.needsTarget())
                    playGround.config();
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED) {
                pane.getChildren().add(bigImageView);
                bigImageView.setLayoutX(getLayoutX() + getWidth(normalImageView) / 2 - getWidth(bigImageView) / 2);
                if (playerFaction == PlayerFaction.FRIENDLY)
                    bigImageView.setLayoutY(getLayoutY() - getHeight(bigImageView));
                else
                    bigImageView.setLayoutY(getLayoutY() + getHeight(bigImageView) - getHeight(normalImageView) / 2);
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED) {
                pane.getChildren().remove(bigImageView);
            }
            mouseEvent.consume();
        }

        private double getLayoutX() {
            if (handHBox1.getChildren().contains(normalImageView))
                return handHBox1.getLayoutX() + normalImageView.getLayoutX();
            else if (handHBox2.getChildren().contains(normalImageView))
                return handHBox2.getLayoutX() + normalImageView.getLayoutX();
            return 0;
        }

        private double getLayoutY() {
            if (handHBox1.getChildren().contains(normalImageView))
                return handHBox1.getLayoutY();
            else if (handHBox2.getChildren().contains(normalImageView))
                return handHBox2.getLayoutY();
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

    private class AttackEventHandler extends TargetEventHandler {
        AttackEventHandler(Targetable targetable, Node node) {
            super(targetable, node);
            initialize();
        }

        @Override
        protected void setSelectedTargetable(Targetable targetable) {
            selectedAttackable = (Attackable) targetable;
        }

        @Override
        protected boolean isEnough() {
            return playGround.getCurrentCharacter() != GamePlayerGraphics.this;
        }

        @Override
        protected void deselectedMode() {
            playGround.getCurrentCharacter().attackMode();
            playGround.getOtherCharacter().attackMode();
        }

        @Override
        protected void oneSelectedMode() {
            playGround.getCurrentCharacter().defenseMode((Attackable) targetable);
            playGround.getOtherCharacter().defenseMode((Attackable) targetable);
        }

        @Override
        protected void doAction() {
            GamePlayerGraphics current = (GamePlayerGraphics) playGround.getCurrentCharacter();
            client.request(new Command<>(ATTACK, current.getSelectedAttackable(), selectedAttackable));
            playGround.config();
        }
    }
}
