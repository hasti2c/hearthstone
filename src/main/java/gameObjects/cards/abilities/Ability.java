package gameObjects.cards.abilities;

import controllers.game.*;
import gameObjects.*;
import gameObjects.cards.abilities.targets.*;
import gameObjects.player.*;
import gameObjects.cards.*;
import graphics.directories.playground.GamePlayerGraphics;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.*;
import static gameObjects.cards.abilities.targets.TargetType.*;

public abstract class Ability implements Configable {
    protected AbilityType abilityType;
    protected PlayerSide callerSide;
    protected TargetType targetType;
    protected CardType targetCardType;
    private int times = 1;
    private int targetAttack, targetHealth;
    private boolean hasTaunt = false, hasRush = false;
    private Ability nextAbility;
    private AddCard nextAddCard;
    private ChangeStats nextChangeStats;

    @Override
    public void initialize(GameController controller) {
        if (nextAddCard != null)
            nextAbility = nextAddCard;
        else if (nextChangeStats != null)
            nextAbility = nextChangeStats;
    }

    @Override
    public String getJsonPath(GameController controller, String name) {
        return null;
    }

    private void callDoAction(GamePlayer actionPerformer, Card caller, Card played, boolean assertValidCaller) {
        for (int i = 0; i < times; i++) {
            if (targetType.equals(SELECTED))
                selectAndDoAction(actionPerformer, caller);
            else if (targetType.equals(DISCOVER))
                discoverAndDoAction(actionPerformer, caller, played);
            else
                for (Card target : getTarget(actionPerformer, caller, played))
                    if (isValidCaller(actionPerformer, caller) && (assertValidCaller || caller.isValid()) && target.isValid())
                        doActionAndNext(actionPerformer, caller, target);
        }
    }

    public void callDoAction(GamePlayer actionPerformer, Card caller, Card played) {
        callDoAction(actionPerformer, caller, played, false);
    }

    public void doActionAndNext(GamePlayer actionPerformer, Card caller, Card target) {
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
            case RANDOM -> {
                ArrayList<Card> possibleCards = getValidSublist(GameController.getCardsList());
                if (possibleCards.size() > 0)
                    targets.add(Card.getRandomCard(possibleCards));
            }
            case BY_STATS -> {
                ArrayList<Card> possibleCards = new ArrayList<>();
                for (Card card : GameController.getCardsList())
                    if (isValidTarget(card) && card instanceof Minion minion && matchesStats (minion))
                        possibleCards.add(card);
                if (possibleCards.size() > 0)
                    targets.add(Card.getRandomCard(possibleCards));
                else {
                    Minion minion = (Minion) Card.getRandomCard(getValidSublist(GameController.getCardsList()));
                    if (minion == null)
                        break;
                    minion.setHealth(targetHealth);
                    minion.setAttack(targetAttack);
                    if (hasTaunt)
                        minion.setTaunt(true);
                    if (hasRush)
                        minion.setRush(true);
                    targets.add(minion);
                }
            }
            case DECK -> {
                ArrayList<Card> possibleCards = getValidSublist(actionPerformer.getLeftInDeck());
                if (possibleCards.size() > 0)
                    addIfValid(targets, Card.getRandomCard(possibleCards));
            }
            case DISCOVER -> {
                ArrayList<Card> possibleCards = getValidSublist(GameController.getCardsList());
                for (int i = 0; i < 3; i++) {
                    Card card = Card.getRandomCard(possibleCards);
                    if (card != null)
                        targets.add(card);
                    possibleCards.remove(card);
                }
            }
        }
        return targets;
    }

    private ArrayList<Card> getValidSublist(ArrayList<? extends Card> list) {
        ArrayList<Card> validCards = new ArrayList<>();
        for (Card card : list)
            addIfValid(validCards, card);
        return validCards;
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

    private boolean matchesStats(Minion minion) {
        boolean ret = targetHealth == 0 || minion.getHealth() == targetHealth;
        ret &= targetAttack == 0 || minion.getAttack() == targetAttack;
        ret &= !hasTaunt || minion.getTaunt();
        ret &= !hasRush || minion.getRush();
        return ret;
    }

    public AbilityType getAbilityType() {
        return abilityType;
    }

    public TargetType getTargetType() {
        return targetType;
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

    private void discoverAndDoAction(GamePlayer actionPerformer, Card caller, Card played) {
        DiscoverGraphics discover = new DiscoverGraphics(actionPerformer, this, caller, getTarget(actionPerformer, caller, played));
        discover.display();
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
            Ability.this.doActionAndNext(player.getGamePlayer(), caller, (Card) targetable) ;
            player.getPlayGround().config();
        }
    }
}
