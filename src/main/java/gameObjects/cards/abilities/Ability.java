package gameObjects.cards.abilities;

import controllers.game.*;
import gameObjects.*;
import gameObjects.player.*;
import gameObjects.cards.*;
import graphics.directories.playground.GamePlayerGraphics;
import graphics.directories.playground.targets.TargetEventHandler;
import graphics.directories.playground.targets.Targetable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.*;
import static gameObjects.cards.abilities.TargetType.*;

public abstract class Ability implements Configable {
    protected AbilityType abilityType;
    protected PlayerSide callerSide;
    protected TargetType targetType;
    protected CardType targetCardType;
    private int times = 1;
    private int targetAttack, targetHealth;
    private Ability nextAbility;
    private AddCard nextAddCard;

    @Override
    public void initialize(GameController controller) {
        if (nextAddCard != null)
            nextAbility = nextAddCard;
    }

    @Override
    public String getJsonPath(GameController controller, String name) {
        return null;
    }

    private void callDoAction(GamePlayer actionPerformer, Card caller, Card played, boolean assertValidCaller) {
        for (int i = 0; i < times; i++) {
            if (targetType.equals(SELECTED))
                selectAndDoAction(actionPerformer, caller);
            for (Card target : getTarget(actionPerformer, caller, played))
                if (isValidCaller(actionPerformer, caller) && (assertValidCaller || caller.isValid()) && target.isValid())
                    doActionAndNext(actionPerformer, caller, target);
        }
    }

    public void callDoAction(GamePlayer actionPerformer, Card caller, Card played) {
        callDoAction(actionPerformer, caller, played, false);
    }

    private void doActionAndNext(GamePlayer actionPerformer, Card caller, Card target) {
        doAction(actionPerformer, caller, target);
        if (nextAbility != null)
            nextAbility.callDoAction(actionPerformer, target, null, true);
    }

    protected abstract void doAction(GamePlayer actionPerformer, Card caller, Card target);

    private ArrayList<Card> getTarget(GamePlayer actionPerformer, Card caller, Card played) {
        ArrayList<Card> targets = new ArrayList<>();
        switch (targetType) {
            case SELF -> addIfValid(targets, caller);
            case PLAYED -> addIfValid(targets, played);
            case ALL_ELSE -> {
                for (Card card : actionPerformer.getOpponent().getMinionsInGame())
                    addIfValid(targets, card);
                for (Card card : actionPerformer.getMinionsInGame())
                    addIfValid(targets, card);
                targets.remove(caller);
            }
            case RANDOM -> addIfValid(targets, getRandomCard(GameController.getCardsList()));
            case BY_STATS -> {
                ArrayList<Card> possibleCards = new ArrayList<>();
                for (Card card : GameController.getCardsList())
                    if (isValidTarget(card) && ((Minion) card).getHealth() == targetHealth && ((Minion) card).getAttack() == targetAttack)
                        possibleCards.add(card);
                if (!addIfValid(targets, getRandomCard(possibleCards))) {
                    Minion minion = (Minion) getRandomCard(GameController.getCardsList());
                    if (minion == null)
                        break;
                    minion.setHealth(targetHealth);
                    minion.setAttack(targetAttack);
                }
            }
            case DECK -> addIfValid(targets, getRandomCard(actionPerformer.getLeftInDeck()));
        }
        return targets;
    }

    private boolean hasValidTarget(ArrayList<Card> list) {
        for (Card card : list)
            if (isValidTarget(card))
                return true;
        return false;
    }

    private boolean addIfValid(ArrayList<Card> targets, Card target) {
        if (target == null || !isValidTarget(target))
            return false;
        targets.add(target);
        return true;
    }

    private boolean isValidCaller(GamePlayer actionPerformer, Card caller) {
        if (callerSide == null)
            return true;
        boolean ret = callerSide.equals(PlayerSide.SELF) && actionPerformer.owns(caller);
        ret |= callerSide.equals(PlayerSide.OPPONENT) && !actionPerformer.owns(caller);
        return ret;
    }

    private boolean isValidTarget(Card target) {
        return targetCardType == null || targetCardType.equals(target.getCardType());
    }

    public AbilityType getAbilityType() {
        return abilityType;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    private Card getRandomCard(ArrayList<Card> possibleCards) {
        if (!hasValidTarget(possibleCards))
            return null;
        Card card;
        do {
            card = Card.getRandomCard(possibleCards);
        } while (!isValidTarget(card));
        return card;
    }

    protected void selectAndDoAction(GamePlayer actionPerformer, Card caller) {
        selectionMode(actionPerformer, caller);
        selectionMode(actionPerformer.getOpponent(), caller);
    }

    private void selectionMode(GamePlayer gamePlayer, Card caller) {
        addEventHandlerToAll(gamePlayer, caller);
        gamePlayer.getGraphics().disableHero();
    }

    private void addEventHandlerToAll(GamePlayer gamePlayer, Card caller) {
        ArrayList<Minion> minionsInGame = gamePlayer.getMinionsInGame();
        gamePlayer.getGraphics().reloadMinionsHBox();
        HBox minionsHBox = gamePlayer.getGraphics().getMinionsHBox();
        for (int i = 0; i < minionsInGame.size(); i++)
            minionsHBox.getChildren().get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, new SelectionEventHandler(gamePlayer.getGraphics(), caller, minionsInGame.get(i), minionsHBox.getChildren().get(i)));
    }

    private class SelectionEventHandler extends TargetEventHandler {
        private GamePlayerGraphics player;
        private Card caller;

        protected SelectionEventHandler(GamePlayerGraphics player, Card caller, Targetable targetable, Node node) {
            super(targetable, node);
            this.player = player;
            this.caller = caller;
            initialize();
        }

        @Override
        protected void setSelectedTargetable(Targetable targetable) {}

        @Override
        protected boolean isEnough() {
            return true;
        }

        @Override
        protected void deselectedMode() {
            player.enableMinions();
            player.getOpponent().enableMinions();
        }

        @Override
        protected void oneSelectedMode() {}

        @Override
        protected void doAction() {
            Ability.this.doActionAndNext(player.getGamePlayer(), caller, (Minion) targetable);
            player.getPlayGround().config();
        }
    }
}
