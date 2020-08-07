package client.graphics.directories.playground.targets;

import client.*;
import commands.*;
import commands.types.*;
import elements.abilities.targets.*;
import elements.cards.*;
import elements.*;
import elements.abilities.*;
import elements.heros.*;
import client.graphics.directories.playground.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.io.*;
import java.util.*;

public class DiscoverGraphics {
    private final Client client;
    private final PlayGround playGround;
    private Pane pane;
    private final Playable caller;
    private final ArrayList<Card> targets = new ArrayList<>();
    @FXML
    private HBox cardsHBox;

    public DiscoverGraphics(Client client, CharacterGraphics<?> actionPerformer, Ability ability, Playable caller) {
        this.client = client;
        this.playGround = actionPerformer.getPlayGround();
        ArrayList<Element> targets = ability.getTarget(actionPerformer.getCharacter(), caller, null);
        for (Element element : targets)
            this.targets.add((Card) element);
        this.caller = caller;
        load();
    }

    private void config() {
        for (Card card : targets) {
            ImageView iv = card.getImageView(250, -1);
            cardsHBox.getChildren().add(iv);
            iv.setOnMouseClicked(new DiscoverEventHandler(card, iv));
        }
    }

    public void display() {
        config();
        playGround.showDiscover(pane);
    }

    private void load() {
        FXMLLoader loader = new FXMLLoader(DiscoverGraphics.class.getResource("/fxml/popups/discover.fxml"));
        loader.setController(this);
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class DiscoverEventHandler extends TargetEventHandler {
        protected DiscoverEventHandler(Targetable targetable, Node node) {
            super(targetable, node);
        }

        @Override
        protected void setSelectedTargetable(Targetable targetable) {}

        @Override
        protected boolean isEnough() {
            return true;
        }

        @Override
        protected void deselectedMode() {
            for (Node node : cardsHBox.getChildren())
                TargetEventHandler.enableNode(node);
        }

        @Override
        protected void oneSelectedMode() {}

        @Override
        protected void doAction() {
            if (caller instanceof HeroPower)
                client.request(new Command<>(ServerCommandType.HERO_POWER, targetable));
            else if (caller instanceof Card)
                client.request(new Command<>(ServerCommandType.PLAY, caller, targetable));
            playGround.removeDiscover(pane);
            playGround.config();
        }
    }
}

