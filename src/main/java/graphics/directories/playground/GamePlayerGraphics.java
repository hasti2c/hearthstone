package graphics.directories.playground;

import controllers.commands.Command;
import controllers.commands.CommandRunner;
import controllers.commands.CommandType;
import gameObjects.Player.GamePlayer;
import gameObjects.Player.PlayerFaction;
import gameObjects.Targetable;
import gameObjects.cards.Card;
import gameObjects.cards.Minion;
import gameObjects.cards.Weapon;
import gameObjects.heros.Hero;
import graphics.directories.playground.cards.MinionGraphics;
import graphics.directories.playground.cards.WeaponGraphics;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;

public class GamePlayerGraphics {
    private final PlayGround playGround;
    private final CommandRunner runner;
    private final PlayerFaction playerFaction;
    private final GamePlayer gamePlayer;
    private Pane pane;
    private Targetable selectedTargetable;
    private final TargetEventHandler heroEventHandler;
    @FXML
    private ImageView heroImage;
    @FXML
    private Label hpLabel, manaLabel, deckLabel;
    @FXML
    private Button heroPowerButton;
    @FXML
    private HBox manaHBox, handHBox1, handHBox2, minionsHBox;
    @FXML
    private Pane weaponPane;

    GamePlayerGraphics(PlayGround playGround, CommandRunner runner, GamePlayer gamePlayer) {
        this.playGround = playGround;
        this.runner = runner;
        this.gamePlayer = gamePlayer;
        this.playerFaction = gamePlayer.getPlayerFaction();
        load();

        heroImage.setImage(gamePlayer.getInventory().getCurrentHero().getGameImage());
        heroEventHandler = new TargetEventHandler(gamePlayer.getInventory().getCurrentHero(), heroImage);
        heroImage.addEventHandler(MouseEvent.MOUSE_CLICKED, heroEventHandler);

        heroPowerButton.setOnAction(e -> {
            runner.run(new Command(CommandType.HERO_POWER));
            config();
        });
    }

        private FXMLLoader getLoader() {
            return new FXMLLoader(GamePlayerGraphics.class.getResource("/fxml/directories/" + playerFaction.toString().toLowerCase() + "GamePlayer.fxml"));
        }

        private void load() {
            FXMLLoader loader = getLoader();
            loader.setController(this);
            try {
                pane = loader.load();
                pane.setLayoutX(115);
                switch (playerFaction) {
                    case FRIENDLY: pane.setLayoutY(387); break;
                    case ENEMY: pane.setLayoutY(0); break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void clear() {
            handHBox1.getChildren().clear();
            handHBox2.getChildren().clear();
            minionsHBox.getChildren().clear();
            weaponPane.getChildren().clear();
            heroEventHandler.deselect();
        }

        void config() {
            clear();

            hpLabel.setText(gamePlayer.getInventory().getCurrentHero().getHealth() + "");
            manaLabel.setText(gamePlayer.getMana() + "/10");
            for (int i = 0; i < gamePlayer.getMana(); i++)
                manaHBox.getChildren().get(i).setVisible(true);
            for (int i = gamePlayer.getMana(); i < 10; i++)
                manaHBox.getChildren().get(i).setVisible(false);
            deckLabel.setText(gamePlayer.getLeftInDeck().size() + "/" + gamePlayer.getInventory().getCurrentDeck().getCards().size());

            configHand();
            configTargets();
            configWeapon();

            if (gamePlayer.isHeroPowerUsed()) {
                heroPowerButton.setText("used");
                heroPowerButton.setDisable(true);
            } else {
                heroPowerButton.setText(gamePlayer.getInventory().getCurrentHero().getHeroPower().toString());
                heroPowerButton.setDisable(false);
            }
        }

        private void configHand() {
        ArrayList<Card> hand = gamePlayer.getHand();
        for (int i = 0; i < hand.size(); i++) {
            ImageView iv = getImageView(hand.get(i));
            if (i < 5)
                handHBox1.getChildren().add(iv);
            else
                handHBox2.getChildren().add(iv);
        }
        if (hand.size() < 5)
            handHBox2.setVisible(false);
        else {
            handHBox2.setVisible(true);
            handHBox2.setLayoutX(640 - handHBox2.getChildren().size() * 30);
        }
    }

        private void configTargets() {
            for (Minion minion : gamePlayer.getMinionsInGame()) {
                Group group = new MinionGraphics(minion).getGroup();
                group.addEventFilter(MouseEvent.MOUSE_CLICKED, new TargetEventHandler(minion, group));
                minionsHBox.getChildren().add(group);
            }

            if (this == playGround.getCurrentGamePlayer())
                attackMode();
            else
                defenseMode();
        }

        private void configWeapon() {
            if (gamePlayer.getCurrentWeapon() == null)
                return;
            if (gamePlayer.canAttack(gamePlayer.getInventory().getCurrentHero()))
                weaponPane.getChildren().add((new WeaponGraphics(gamePlayer.getCurrentWeapon()).getGroup()));
            else
                weaponPane.getChildren().add(Weapon.getClosedImageView());
        }

        private void attackMode() {
            for (Minion minion : gamePlayer.getMinionsInGame())
                if (gamePlayer.canAttack(minion))
                    enableTarget(minion);
                else
                    disableTarget(minion);
            Hero hero = gamePlayer.getInventory().getCurrentHero();
            if (gamePlayer.canAttack(hero))
                enableTarget(hero);
            else
                disableTarget(hero);
        }

        private void defenseMode() {
            for (Minion minion : gamePlayer.getMinionsInGame())
                if (gamePlayer.canBeAttacked(minion))
                    enableTarget(minion);
                else
                    disableTarget(minion);
            Hero hero = gamePlayer.getInventory().getCurrentHero();
            if (gamePlayer.canBeAttacked(hero))
                enableTarget(hero);
            else
                disableTarget(hero);
        }

        private Node getNode(Targetable targetable) {
            if (targetable instanceof Hero)
                return heroImage;
            assert gamePlayer.getMinionsInGame().contains(targetable);
            return minionsHBox.getChildren().get(gamePlayer.getMinionsInGame().indexOf(targetable));
        }

        private void enableTarget(Targetable targetable) {
            Node node = getNode(targetable);
            node.setDisable(false);
            node.setEffect(new Glow());
        }

        private void disableTarget(Targetable targetable) {
            Node node = getNode(targetable);
            node.setDisable(true);
            node.setEffect(null);
        }

        private ImageView getImageView(Card card) {
            ImageView iv;
            int n = gamePlayer.getHand().size();
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

        public Pane getPane() {
        return pane;
    }

        private Targetable getSelectedTarget() {
            return selectedTargetable;
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
                    playGround.config();
                } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED) {
                    pane.getChildren().add(bigImageView);
                    bigImageView.setLayoutX(getLayoutX() + getWidth(normalImageView) / 2 - getWidth(bigImageView) / 2);
                    bigImageView.setLayoutY(getLayoutY() - getHeight(bigImageView));
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

        private class TargetEventHandler implements EventHandler<MouseEvent> {
            private final Node node;
            private final Targetable targetable;
            private boolean isSelected = false;

            private TargetEventHandler(Targetable targetable, Node node) {
                this.targetable = targetable;
                this.node = node;
            }

            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    if (!isSelected)
                        select();
                    else
                        deselect();
                }
            }

            private void select() {
                isSelected = true;
                selectedTargetable = targetable;

                GamePlayerGraphics current = playGround.getCurrentGamePlayer(), other = playGround.getOtherGamePlayer();
                if (current == GamePlayerGraphics.this) {
                    current.defenseMode();
                    other.defenseMode();
                    enableTarget(targetable);
                    node.setEffect(new Bloom());
                } else {
                    runner.run(new Command(CommandType.ATTACK, current.getSelectedTarget(), selectedTargetable));
                    deselect();
                    current.config();
                    other.config();
                }
            }

            private void deselect() {
                isSelected = false;
                selectedTargetable = null;
                node.setEffect(null);

                GamePlayerGraphics current = playGround.getCurrentGamePlayer(), other = playGround.getOtherGamePlayer();
                current.attackMode();
                other.attackMode();
            }
        }
}
