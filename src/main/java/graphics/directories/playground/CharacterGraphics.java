package graphics.directories.playground;

import controllers.commands.CommandRunner;
import elements.abilities.targets.Attackable;
import elements.abilities.targets.TargetEventHandler;
import elements.cards.Card;
import elements.cards.Minion;
import elements.cards.Weapon;
import elements.heros.Hero;
import elements.heros.HeroPower;
import graphics.directories.playground.playables.HeroPowerGraphics;
import graphics.directories.playground.playables.MinionGraphics;
import graphics.directories.playground.playables.WeaponGraphics;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import system.player.Character;
import system.player.GamePlayer;
import system.player.PlayerFaction;

import java.io.IOException;
import java.util.ArrayList;

public abstract class CharacterGraphics <C extends Character> {
    protected final PlayGround playGround;
    protected CommandRunner runner;
    protected final PlayerFaction playerFaction;
    protected final C character;
    protected Pane pane;
    @FXML
    protected Label hpLabel, manaLabel, deckLabel;
    @FXML
    protected HBox manaHBox, handHBox1, handHBox2, minionsHBox;
    @FXML
    protected Pane weaponPane, heroPowerPane, heroImagePane;

    public CharacterGraphics(PlayGround playGround, CommandRunner runner, C character) {
        this.playGround = playGround;
        this.runner = runner;
        this.character = character;
        playerFaction = character.getPlayerFaction();
        character.setGraphics(this);
        load();
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
                case FRIENDLY -> pane.setLayoutY(387);
                case ENEMY -> pane.setLayoutY(0);
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

        hpLabel.setText(character.getHero().getHealth() + "");
        deckLabel.setText(character.getLeftInDeck().size() + "/" + character.getDeck().getCards().size());
        configMana();

        reloadHeroImage();
        configHero();

        configHero();
        configHand();
        configTargets();
        configWeapon();
        configHeroPower();

        if (playGround.getCurrentCharacter() == this)
            configEndTurnButton();
    }

    protected abstract void configMana();

    protected abstract void configHero();

    public void reloadHeroImage() {
        heroImagePane.getChildren().clear();
        heroImagePane.getChildren().add(character.getHero().getGameImageView(125, -1));
    }

    private void configHand() {
        ArrayList<Card> hand = character.getHand();
        for (int i = 0; i < hand.size(); i++) {
            Node node = getHandNode(hand.get(i));
            if (i < 5)
                handHBox1.getChildren().add(node);
            else
                handHBox2.getChildren().add(node);
        }
        if (hand.size() < 5)
            handHBox2.setVisible(false);
        else {
            handHBox2.setVisible(true);
            handHBox2.setLayoutX(525 - handHBox2.getChildren().size() * 30);
        }
    }

    protected Node getHandNode(Card card) {
        ImageView iv;
        int n = character.getHand().size();
        if (n <= 5)
            iv = card.getImageView(Math.min(300 / n, 100), -1);
        else
            iv = card.getImageView(60, -1);
        configHandNode(card, iv);
        return iv;
    }

    protected abstract void configHandNode(Card card, ImageView imageView);

    private void configTargets() {
        character.clearDeadMinions();
        reloadMinionsHBox();
        for (int i = 0; i < character.getMinionsInGame().size(); i++)
            configTargetNode(character.getMinionsInGame().get(i), minionsHBox.getChildren().get(i));
    }

    public void reloadMinionsHBox() {
        minionsHBox.getChildren().clear();
        for (Minion minion : character.getMinionsInGame()) {
            Group group = new MinionGraphics(minion).getGroup();
            minionsHBox.getChildren().add(group);
        }
    }

    protected abstract void configTargetNode(Minion minion, Node node);

    private void configWeapon() {
        if (character.getCurrentWeapon() == null)
            return;
        if (character.canAttack(character.getHero()))
            weaponPane.getChildren().add((new WeaponGraphics(character.getCurrentWeapon()).getGroup()));
        else
            weaponPane.getChildren().add(Weapon.getClosedImageView());
    }

    private void configHeroPower() {
        HeroPower heroPower = character.getHero().getHeroPower();
        if (character.canUseHeroPower())
            heroPowerPane.getChildren().add(getHeroPowerNode(heroPower));
        else if (heroPower.isPassive())
            heroPowerPane.getChildren().add((new HeroPowerGraphics(heroPower)).getGroup());
        else
            heroPowerPane.getChildren().add(HeroPower.getClosedImageView());
    }

    protected abstract Node getHeroPowerNode(HeroPower heroPower);

    public Pane getPane() {
        return pane;
    }

    public HBox getMinionsHBox() {
        return minionsHBox;
    }

    public CharacterGraphics<C> getOpponent() {
        return (CharacterGraphics<C>) character.getOpponent().getGraphics();
    }

    public PlayGround getPlayGround() {
        return playGround;
    }

    public Character getCharacter() {
        return character;
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

    public void enableHero() {
        TargetEventHandler.enableNode(heroImagePane);
    }

    public void enableMinions() {
        for (Node node : minionsHBox.getChildren())
            TargetEventHandler.enableNode(node);
    }

    protected Node getNode(Minion minion) {
        if (character.getMinionsInGame().size() != minionsHBox.getChildren().size())
            return null;
        return minionsHBox.getChildren().get(character.getMinionsInGame().indexOf(minion));
    }

    protected void attackMode() {
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

    protected void defenseMode(Attackable attacker) {
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

    protected abstract void configEndTurnButton();
}
