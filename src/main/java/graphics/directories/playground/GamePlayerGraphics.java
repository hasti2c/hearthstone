package graphics.directories.playground;

import controllers.commands.*;
import elements.abilities.targets.Attackable;
import elements.abilities.targets.TargetEventHandler;
import elements.abilities.targets.Targetable;
import elements.cards.*;
import elements.heros.*;
import system.player.GamePlayer;
import system.player.PlayerFaction;
import graphics.directories.playground.playables.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import java.io.*;
import java.util.*;

public class GamePlayerGraphics extends CharacterGraphics<GamePlayer> {
    private final CommandRunner runner;
    private Attackable selectedAttackable;

    GamePlayerGraphics(PlayGround playGround, CommandRunner runner, GamePlayer gamePlayer) {
        super(playGround, gamePlayer);
        this.runner = runner;
        gamePlayer.setGraphics(this);
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

    protected Node getHandNode(Card card) {
        ImageView iv;
        int n = character.getHand().size();
        if (n <= 5)
            iv = card.getImageView(Math.min(300 / n, 100), -1);
        else
            iv = card.getImageView(60, -1);

        HandEventHandler eventHandler = new HandEventHandler(card, iv);
        iv.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
        iv.addEventHandler(MouseEvent.MOUSE_ENTERED, eventHandler);
        iv.addEventHandler(MouseEvent.MOUSE_EXITED, eventHandler);
        return iv;
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

    private void attackMode() {
        for (Minion minion : character.getMinionsInGame())
            if (character.canAttack(minion))
                TargetEventHandler.enableNode(getNode(minion));
            else
                TargetEventHandler.disableNode(getNode(minion));
        Hero hero = character.getHero();
        if (character.canAttack(hero))
            TargetEventHandler.enableNode(heroImagePane);
        else
            TargetEventHandler.disableNode(heroImagePane);
    }

    private void defenseMode(Attackable attacker) {
        for (Minion minion : character.getMinionsInGame())
            if (character.canBeAttacked(attacker, minion))
                TargetEventHandler.enableNode(getNode(minion));
            else
                TargetEventHandler.disableNode(getNode(minion));
        Hero hero = character.getHero();
        if (character.canBeAttacked(attacker, hero))
            TargetEventHandler.enableNode(heroImagePane);
        else
            TargetEventHandler.disableNode(heroImagePane);
    }


    private Attackable getSelectedAttackable() {
            return selectedAttackable;
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
                runner.run(new Command(CommandType.PLAY, card));
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
            return playGround.getCurrentGamePlayer() != GamePlayerGraphics.this;
        }

        @Override
        protected void deselectedMode() {
            playGround.getCurrentGamePlayer().attackMode();
            playGround.getOtherGamePlayer().attackMode();
        }

        @Override
        protected void oneSelectedMode() {
            playGround.getCurrentGamePlayer().defenseMode((Attackable) targetable);
            playGround.getOtherGamePlayer().defenseMode((Attackable) targetable);
        }

        @Override
        protected void doAction() {
            GamePlayerGraphics current = playGround.getCurrentGamePlayer();
            runner.run(new Command(CommandType.ATTACK, current.getSelectedAttackable(), selectedAttackable));
            playGround.config();
        }
    }
}
