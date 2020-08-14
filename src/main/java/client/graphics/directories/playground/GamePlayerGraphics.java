package client.graphics.directories.playground;

import client.*;
import client.graphics.directories.playground.targets.*;
import elements.*;
import elements.abilities.*;
import elements.cards.*;
import elements.heros.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import shared.*;
import system.game.GamePlayer;
import client.graphics.directories.playground.playables.*;
import javafx.scene.*;
import javafx.scene.image.*;

public class GamePlayerGraphics extends CharacterGraphics<GamePlayer> {
    GamePlayerGraphics(PlayGround playGround, Client client, GamePlayer gamePlayer, boolean isSelf) {
        super(playGround, client, gamePlayer, isSelf);
    }

    protected void configMana() {
        int mana = character.getState().getMana();
        manaLabel.setText(mana + "/10");
        for (int i = 0; i < mana; i++)
            manaHBox.getChildren().get(i).setVisible(true);
        for (int i = mana; i < 10; i++)
            manaHBox.getChildren().get(i).setVisible(false);
    }

    protected void configHero() {
        Hero hero = character.getHero();
        Node node = heroImagePane.getChildren().get(0);
        node.setOnMouseClicked(new AttackEventHandler(this, hero, node));
    }

    protected void configHandNode(Card card, ImageView imageView) {
        HandEventHandler eventHandler = new HandEventHandler(this, card, imageView);
        imageView.setOnMouseClicked(eventHandler);
        imageView.setOnMouseEntered(eventHandler);
        imageView.setOnMouseExited(eventHandler);
    }

    protected void configTargetNode(Minion minion, Node node) {
        node.setOnMouseClicked(new AttackEventHandler(this, minion, node));
    }

    protected Node getHeroPowerNode(HeroPower heroPower) {
        Group group = (new HeroPowerGraphics(heroPower)).getGroup();
        group.setOnMouseClicked(e -> useHeroPower(heroPower));
        return group;
    }

    protected void configEndTurnButton() {
        Button button = playGround.getEndTurnButton();
        button.setDisable(false);
        button.setText("End Turn");
    }

    public Client getClient() {
        return client;
    }

    public Pair<HBox, HBox> getHandHBoxes() {
        return new Pair<>(handHBox1, handHBox2);
    }

    public PlayGround getPlayGround() {
        return playGround;
    }

    protected void handleSelection(Playable caller) {
        Ability ability = Ability.getRandomAbility(caller.getAbilities());
        selectionMode(ability, caller);
        getOpponent().selectionMode(ability, caller);
    }

    protected void handleDiscover(Playable caller) {
        Ability ability = Ability.getRandomAbility(caller.getAbilities());
        discoverMode(ability, caller);
    }
}
