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

public class GamePlayerGraphics {
    private final PlayGround playGround;
    private final CommandRunner runner;
    private final PlayerFaction playerFaction;
    private final GamePlayer gamePlayer;
    private Pane pane;
    private Attackable selectedAttackable;
    @FXML
    private Label hpLabel, manaLabel, deckLabel;
    @FXML
    private HBox manaHBox, handHBox1, handHBox2, minionsHBox;
    @FXML
    private Pane weaponPane, heroPowerPane, heroImagePane;

    GamePlayerGraphics(PlayGround playGround, CommandRunner runner, GamePlayer gamePlayer) {
        this.playGround = playGround;
        this.runner = runner;
        this.gamePlayer = gamePlayer;
        gamePlayer.setGraphics(this);
        this.playerFaction = gamePlayer.getPlayerFaction();
        load();
        reloadHeroImage();

        /*heroPowerButton.setOnAction(e -> {
            runner.run(new Command(CommandType.HERO_POWER));
            config();
        });*/
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
        heroPowerPane.getChildren().clear();
    }

    protected void config() {
        clear();

        hpLabel.setText(gamePlayer.getInventory().getCurrentHero().getHealth() + "");
        manaLabel.setText(gamePlayer.getMana() + "/10");
        for (int i = 0; i < gamePlayer.getMana(); i++)
            manaHBox.getChildren().get(i).setVisible(true);
        for (int i = gamePlayer.getMana(); i < 10; i++)
            manaHBox.getChildren().get(i).setVisible(false);
        deckLabel.setText(gamePlayer.getLeftInDeck().size() + "/" + gamePlayer.getInventory().getCurrentDeck().getCards().size());

        configHero();
        configHand();
        configTargets();
        configWeapon();
        configHeroPower();
    }

    private void configHero() {
        reloadHeroImage();
        Hero hero = gamePlayer.getInventory().getCurrentHero();
        Node node = heroImagePane.getChildren().get(0);
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, new AttackEventHandler(hero, node));
    }

    public void reloadHeroImage() {
        heroImagePane.getChildren().clear();
        heroImagePane.getChildren().add(gamePlayer.getInventory().getCurrentHero().getGameImageView(125, -1));
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
        handHBox2.setLayoutX(525 - handHBox2.getChildren().size() * 30);
    }
}

    private void configTargets() {
        gamePlayer.clearDeadMinions();
        reloadMinionsHBox();
        for (int i = 0; i < gamePlayer.getMinionsInGame().size(); i++) {
            Minion minion = gamePlayer.getMinionsInGame().get(i);
            Node node = minionsHBox.getChildren().get(i);
            node.addEventHandler(MouseEvent.MOUSE_CLICKED, new AttackEventHandler(minion, node));
        }
    }

    public void reloadMinionsHBox() {
        minionsHBox.getChildren().clear();
        for (Minion minion : gamePlayer.getMinionsInGame()) {
            Group group = new MinionGraphics(minion).getGroup();
            minionsHBox.getChildren().add(group);
        }
    }

    private void configWeapon() {
        if (gamePlayer.getCurrentWeapon() == null)
            return;
        if (gamePlayer.canAttack(gamePlayer.getInventory().getCurrentHero()))
            weaponPane.getChildren().add((new WeaponGraphics(gamePlayer.getCurrentWeapon()).getGroup()));
        else
            weaponPane.getChildren().add(Weapon.getClosedImageView());
    }

    private void configHeroPower() {
        HeroPower heroPower = gamePlayer.getInventory().getCurrentHero().getHeroPower();
        if (gamePlayer.canUseHeroPower()) {
            Group group = (new HeroPowerGraphics(heroPower)).getGroup();
            heroPowerPane.getChildren().add(group);
            group.setOnMouseClicked(e -> {
                gamePlayer.useHeroPower();
                if (!heroPower.needsTarget())
                    playGround.config();
            });
        } else if (heroPower.isPassive())
            heroPowerPane.getChildren().add((new HeroPowerGraphics(heroPower)).getGroup());
        else
            heroPowerPane.getChildren().add(HeroPower.getClosedImageView());
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

    private void defenseMode(Attackable attacker) {
        for (Minion minion : gamePlayer.getMinionsInGame())
            if (gamePlayer.canBeAttacked(attacker, minion))
                enableTarget(minion);
            else
                disableTarget(minion);
        Hero hero = gamePlayer.getInventory().getCurrentHero();
        if (gamePlayer.canBeAttacked(attacker, hero))
            enableTarget(hero);
        else
            disableTarget(hero);
    }

    private Node getNode(Attackable attackable) {
        if (attackable instanceof Hero)
            return heroImagePane.getChildren().get(0);
        if (gamePlayer.getMinionsInGame().size() != minionsHBox.getChildren().size())
            return null;
        assert gamePlayer.getMinionsInGame().contains(attackable);
        return minionsHBox.getChildren().get(gamePlayer.getMinionsInGame().indexOf(attackable));
    }

    private void enableTarget(Attackable attackable) {
        Node node = getNode(attackable);
        if (node != null)
            TargetEventHandler.enableNode(node);
    }

    private void disableTarget(Attackable attackable) {
        Node node = getNode(attackable);
        if (node != null)
            TargetEventHandler.disableNode(node);
    }

    public void enableHero() {
        enableTarget(gamePlayer.getInventory().getCurrentHero());
    }

    public void disableHero() {
        disableTarget(gamePlayer.getInventory().getCurrentHero());
    }

    public void enableMinions() {
        for (Node node : minionsHBox.getChildren())
            TargetEventHandler.enableNode(node);
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

    private Attackable getSelectedAttackable() {
            return selectedAttackable;
        }

    public HBox getMinionsHBox() {
        return minionsHBox;
    }

    public GamePlayerGraphics getOpponent() {
        return gamePlayer.getOpponent().getGraphics();
    }

    public PlayGround getPlayGround() {
        return playGround;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public Node getWeaponNode() {
        if (weaponPane.getChildren().size() == 0)
            return null;
        return weaponPane.getChildren().get(0);
    }

    public Node getHeroPowerNode() {
        if (heroPowerPane.getChildren().size() == 0)
            return null;
        return heroPowerPane.getChildren().get(0);
    }

    public ImageView getHeroImageView() {
        return (ImageView) heroImagePane.getChildren().get(0);
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
