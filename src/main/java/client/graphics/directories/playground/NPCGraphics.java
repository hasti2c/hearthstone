package client.graphics.directories.playground;

import client.*;
import elements.cards.*;
import elements.heros.*;
import client.graphics.directories.playground.playables.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import system.player.*;

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
}
