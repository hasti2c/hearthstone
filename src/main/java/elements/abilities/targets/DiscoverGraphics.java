package elements.abilities.targets;

import elements.cards.*;
import elements.*;
import elements.abilities.*;
import system.player.*;
import client.graphics.directories.playground.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.ArrayList;

public class DiscoverGraphics {
    private PlayGround playGround;
    private GamePlayer actionPerformer;
    private Pane pane;
    private Card caller;
    private ArrayList<Card> targets = new ArrayList<>();
    private Ability ability;
    @FXML
    private HBox cardsHBox;

    public DiscoverGraphics(GamePlayer actionPerformer, Ability ability, Card caller, ArrayList<Element> targets) {
        this.actionPerformer = actionPerformer;
        playGround = actionPerformer.getGraphics().getPlayGround();
        for (Element element : targets)
            this.targets.add((Card) element);
        this.caller = caller;
        this.ability = ability;
        load();
    }

    private void config() {
        for (Card card : targets) {
            ImageView iv = card.getImageView(250, -1);
            cardsHBox.getChildren().add(iv);
            iv.addEventHandler(MouseEvent.MOUSE_CLICKED, new DiscoverEventHandler(card, iv));
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
            ability.doActionAndNext(actionPerformer, caller, (Card) targetable);
            playGround.removeDiscover(pane);
            playGround.config();
        }
    }
}

