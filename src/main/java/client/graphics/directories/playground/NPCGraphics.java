package client.graphics.directories.playground;

import client.*;
import commands.Command;
import commands.types.ServerCommandType;
import elements.Element;
import elements.Playable;
import elements.abilities.Ability;
import elements.cards.*;
import elements.heros.*;
import client.graphics.directories.playground.playables.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import system.player.*;

import java.util.ArrayList;

public class NPCGraphics extends CharacterGraphics<NPC> {
    public NPCGraphics(PlayGround playGround, Client client, NPC character) {
        super(playGround, client, character);
    }

    protected void configMana() {
        manaLabel.setVisible(false);
        manaHBox.setVisible(false);
    }

    protected void configHero() {}

    protected void configHandNode(Card card, ImageView imageView) {}

    protected void configTargetNode(Minion minion, Node node) {}

    protected Node getHeroPowerNode(HeroPower heroPower) {
        return (new HeroPowerGraphics(heroPower)).getGroup();
    }

    protected void configEndTurnButton() {
        Button button = playGround.getEndTurnButton();
        button.setDisable(true);
        button.setText("Waiting...");
    }

    protected void handleSelection(Playable playable) {
        Ability ability = Ability.getRandomAbility(playable.getAbilities());
        ArrayList<Element> targets = ability.getTarget(character, playable, null);
        client.request(new Command<>(ServerCommandType.PLAY, playable, Element.getRandomElement(targets)));
    }

    protected void handleDiscover(Playable playable) {
        Ability ability = Ability.getRandomAbility(playable.getAbilities());
        ArrayList<Element> targets = ability.getTarget(character, playable, null);
        client.request(new Command<>(ServerCommandType.PLAY, playable, Element.getRandomElement(targets)));
    }
}
