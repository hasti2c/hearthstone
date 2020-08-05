package client.graphics.directories.playground;

import client.*;
import client.graphics.directories.playground.targets.DiscoverGraphics;
import client.graphics.directories.playground.targets.SelectionEventHandler;
import client.graphics.directories.playground.targets.TargetEventHandler;
import commands.Command;
import commands.types.CommandType;
import commands.types.ServerCommandType;
import elements.Element;
import elements.Playable;
import elements.abilities.Ability;
import elements.abilities.targets.*;
import elements.cards.*;
import elements.heros.*;
import client.graphics.directories.playground.playables.*;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import shared.Pair;
import system.player.*;
import system.player.Character;

import java.io.*;
import java.util.*;

public abstract class CharacterGraphics <C extends Character> {
    protected final PlayGround playGround;
    protected Client client;
    protected final PlayerFaction playerFaction;
    protected final C character;
    protected Pane pane;
    private Attackable selectedAttackable;
    @FXML
    protected Label hpLabel, manaLabel, deckLabel;
    @FXML
    protected HBox manaHBox, handHBox1, handHBox2, minionsHBox;
    @FXML
    protected Pane weaponPane, heroPowerPane, heroImagePane;

    public CharacterGraphics(PlayGround playGround, Client client, C character) {
        this.playGround = playGround;
        this.client = client;
        this.character = character;
        playerFaction = character.getPlayerFaction();
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
        return (CharacterGraphics<C>) playGround.getOpponent(this);
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

    public Node getNode(Element element) {
        if (element instanceof Minion minion && character.getMinionsInGame().size() == minionsHBox.getChildren().size())
            return minionsHBox.getChildren().get(character.getMinionsInGame().indexOf(minion));
        if (element instanceof Weapon weapon && character.getCurrentWeapon() == weapon)
            return getFirst(weaponPane);
        if (element instanceof HeroPower heroPower && character.getHero().getHeroPower() == heroPower)
            return getFirst(heroPowerPane);
        if (element instanceof Hero hero && character.getHero() == hero)
            return getFirst(heroImagePane);
        return null;
    }

    private Node getFirst(Pane pane) {
        return pane.getChildren().size() != 0 ? pane.getChildren().get(0) : null;
    }

    public void attackMode() {
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

    public void defenseMode(Attackable attacker) {
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

    protected ArrayList<Element> getCurrentElementsAndNodes() {
        reloadMinionsHBox();
        reloadHeroImage();

        ArrayList<Element> elements = new ArrayList<>(character.getMinionsInGame());
        if (character.getCurrentWeapon() != null)
            elements.add(character.getCurrentWeapon());
        if (character.canUseHeroPower())
            elements.add(character.getHero().getHeroPower());
        elements.add(character.getHero());
        return elements;
    }

    public Attackable getSelectedAttackable() {
        return selectedAttackable;
    }

    public void setSelectedAttackable(Attackable selectedAttackable) {
        this.selectedAttackable = selectedAttackable;
    }

    protected abstract void handleSelection(Playable playable);

    protected abstract void handleDiscover(Playable playable);

    protected void useHeroPower(HeroPower heroPower) {
        if (heroPower.needsSelection())
            handleSelection(heroPower);
        else if (heroPower.needsDiscover())
            handleDiscover(heroPower);
        else {
            client.request(new Command<>(ServerCommandType.HERO_POWER));
            playGround.config();
        }
    }

    public void playCard(Card card) {
        if (card.needsSelection())
            handleSelection(card);
        else if (card.needsDiscover())
            handleDiscover(card);
        else {
            client.request(new Command<>(ServerCommandType.PLAY, card));
            playGround.config();
        }
    }

    protected void selectionMode(Ability ability, Playable caller) {
        ArrayList<Element> elements = getCurrentElementsAndNodes();
        for (Element element : elements) {
            Node node = getNode(element);
            if (ability.isValidTarget(element) && element instanceof Targetable targetable)
                node.addEventHandler(MouseEvent.MOUSE_CLICKED, new SelectionEventHandler(client, this, caller, targetable, ability));
            else if (element instanceof Targetable)
                TargetEventHandler.disableNode(node);
        }
    }

    protected void discoverMode(Ability ability, Playable caller) {
        DiscoverGraphics discover = new DiscoverGraphics(client, this, ability, caller);
        discover.display();
    }
}
