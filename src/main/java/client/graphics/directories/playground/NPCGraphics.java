package client.graphics.directories.playground;

import client.Client;
import elements.cards.Card;
import elements.cards.Minion;
import elements.heros.HeroPower;
import client.graphics.directories.playground.playables.HeroPowerGraphics;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import system.player.NPC;

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
