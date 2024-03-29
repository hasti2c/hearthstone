package client.graphics.directories.playground;

import client.*;
import client.graphics.directories.playground.targets.*;
import commands.*;
import commands.types.*;
import elements.*;
import elements.abilities.*;
import elements.abilities.targets.*;
import elements.cards.*;
import elements.heros.*;
import client.graphics.directories.playground.playables.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import system.game.*;
import system.player.*;
import system.game.characters.Character;

import java.io.*;
import java.util.*;

public abstract class CharacterGraphics <C extends Character> {
    protected final PlayGround playGround;
    protected Client client;
    protected final PlayerFaction playerFaction;
    protected final C character;
    protected Pane pane;
    private Attackable selectedAttackable;
    private final boolean isSelf;
    @FXML
    protected Label hpLabel, manaLabel, deckLabel;
    @FXML
    protected HBox manaHBox, handHBox1, handHBox2, minionsHBox;
    @FXML
    protected Pane weaponPane, heroPowerPane, heroImagePane;

    public CharacterGraphics(PlayGround playGround, Client client, C character, boolean isSelf) {
        this.playGround = playGround;
        this.client = client;
        this.character = character;
        playerFaction = character.getPlayerFaction();
        this.isSelf = isSelf;
        load();
    }

    private FXMLLoader getLoader() {
        return new FXMLLoader(GamePlayerGraphics.class.getResource("/fxml/directories/" + (isBottom() ? "friendly" : "enemy") + "GamePlayer.fxml"));
    }

    private void load() {
        FXMLLoader loader = getLoader();
        loader.setController(this);
        try {
            pane = loader.load();
            pane.setLayoutX(115);
            if (isBottom())
                pane.setLayoutY(387);
            else
                pane.setLayoutY(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isBottom() {
        Game game = playGround.getGame();
        return isSelf && (game.getType().isMultiPlayer() || game.getCharacters()[0] == character);
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
        deckLabel.setText(character.getState().getLeftInDeck().size() + "/" + character.getDeck().getCards().size());
        configMana();

        reloadHeroImage();
        configHero();
        configHand();
        configTargets();
        configWeapon();
        configHeroPower();
    }

    protected abstract void configMana();

    protected abstract void configHero();

    public void reloadHeroImage() {
        heroImagePane.getChildren().clear();
        heroImagePane.getChildren().add(character.getHero().getGameImageView(125, -1));
    }

    private void configHand() {
        ArrayList<Card> hand = character.getState().getHand();
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
        int n = character.getState().getHand().size();
        if (n <= 5)
            iv = card.getImageView(Math.min(300 / n, 100), -1, isSelf);
        else
            iv = card.getImageView(60, -1, isSelf);
        if (isSelf)
            configHandNode(card, iv);
        return iv;
    }

    protected abstract void configHandNode(Card card, ImageView imageView);

    private void configTargets() {
        character.clearDeadMinions();
        reloadMinionsHBox();
        for (int i = 0; i < character.getState().getMinionsInGame().size(); i++)
            configTargetNode(character.getState().getMinionsInGame().get(i), minionsHBox.getChildren().get(i));
    }

    public void reloadMinionsHBox() {
        minionsHBox.getChildren().clear();
        for (Minion minion : character.getState().getMinionsInGame()) {
            Group group = new MinionGraphics(minion).getGroup();
            minionsHBox.getChildren().add(group);
        }
    }

    protected abstract void configTargetNode(Minion minion, Node node);

    private void configWeapon() {
        if (character.getState().getCurrentWeapon() == null)
            return;
        if (character.canAttack(character.getHero()))
            weaponPane.getChildren().add((new WeaponGraphics(character.getState().getCurrentWeapon()).getGroup()));
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

    public CharacterGraphics<?> getOpponent() {
        return playGround.getOpponent(this);
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
        if (element instanceof Minion minion && character.getState().getMinionsInGame().size() == minionsHBox.getChildren().size())
            return minionsHBox.getChildren().get(character.getState().getMinionsInGame().indexOf(minion));
        if (element instanceof Weapon weapon && character.getState().getCurrentWeapon() == weapon)
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
        for (Minion minion : character.getState().getMinionsInGame())
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
        for (Minion minion : character.getState().getMinionsInGame())
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

    protected ArrayList<Element> getCurrentElementsAndNodes() {
        reloadMinionsHBox();
        reloadHeroImage();

        ArrayList<Element> elements = new ArrayList<>(character.getState().getMinionsInGame());
        if (character.getState().getCurrentWeapon() != null)
            elements.add(character.getState().getCurrentWeapon());
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
        else
            client.request(new Command<>(ServerCommandType.HERO_POWER));
    }

    public void playCard(Card card) {
        if (card.needsSelection())
            handleSelection(card);
        else if (card.needsDiscover())
            handleDiscover(card);
        else
            client.request(new Command<>(ServerCommandType.PLAY, card));
    }

    protected void selectionMode(Ability ability, Playable caller) {
        ArrayList<Element> elements = getCurrentElementsAndNodes();
        for (Element element : elements) {
            Node node = getNode(element);
            if (ability.isValidTarget(element) && element instanceof Targetable targetable) {
                SelectionEventHandler handler = new SelectionEventHandler(client, this,caller, targetable, ability);
                node.setOnMouseClicked(handler);
            } else if (element instanceof Targetable)
                TargetEventHandler.disableNode(node);
        }
    }

    protected void discoverMode(Ability ability, Playable caller) {
        DiscoverGraphics discover = new DiscoverGraphics(client, this, ability, caller);
        discover.display();
    }

    public boolean getIsSelf() {
        return isSelf;
    }
}
