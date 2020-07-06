package gameObjects.cards.abilities;

import controllers.game.*;
import gameObjects.*;
import gameObjects.cards.abilities.targets.*;
import gameObjects.player.*;
import gameObjects.cards.*;
import graphics.directories.playground.GamePlayerGraphics;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;

import java.util.*;

import static gameObjects.cards.ElementType.HERO;
import static gameObjects.cards.ElementType.MINION;
import static gameObjects.cards.abilities.targets.TargetType.*;

public abstract class Ability implements Configable {
    protected AbilityType abilityType;
    protected PlayerSide callerSide;
    protected TargetType targetType;
    protected ArrayList<ElementType> targetElementTypes = new ArrayList<>();
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

    private void callDoAction(GamePlayer actionPerformer, Playable caller, Element played, boolean assertValidCaller) {
        for (int i = 0; i < times; i++) {
            if (targetType.equals(SELECTED))
                selectAndDoAction(actionPerformer, caller);
            else if (targetType.equals(DISCOVER))
                discoverAndDoAction(actionPerformer, (Card) caller, (Card) played);
            else {
                System.out.println(getTarget(actionPerformer, caller, (Card) played));
                for (Element target : getTarget(actionPerformer, caller, (Card) played)) {
                    System.out.println(target + " " + isValidCaller(actionPerformer, caller) + " " + caller.isValid() + " " + target.isValid());
                    if (isValidCaller(actionPerformer, caller) && (assertValidCaller || caller.isValid()) && target.isValid())
                        doActionAndNext(actionPerformer, caller, target);
                }
            }
        }
    }

    public void callDoAction(GamePlayer actionPerformer, Playable caller, Card played) {
        callDoAction(actionPerformer, caller, played, false);
    }

    public void doActionAndNext(GamePlayer actionPerformer, Playable caller, Element target) {
        doAction(actionPerformer, caller, target);
        if (nextAbility != null)
            nextAbility.callDoAction(actionPerformer, (Playable) target, null, true);
    }

    protected abstract void doAction(GamePlayer actionPerformer, Playable caller, Element target);

    private ArrayList<Element> getTarget(GamePlayer actionPerformer, Playable caller, Card played) {
        ArrayList<Element> targets = new ArrayList<>();
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
                ArrayList<Element> possibleElements = getValidSublist(GameController.getCardsList());
                if (possibleElements.size() > 0)
                    targets.add(Element.getRandomElement(possibleElements));
            }
            case BY_STATS -> {
                ArrayList<Element> possibleElements = new ArrayList<>();
                for (Card card : GameController.getCardsList())
                    if (isValidTarget(card) && card instanceof Minion minion && matchesStats (minion))
                        possibleElements.add(card);
                if (possibleElements.size() > 0)
                    targets.add(Element.getRandomElement(possibleElements));
                else {
                    Minion minion = (Minion) Element.getRandomElement(getValidSublist(GameController.getCardsList()));
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
            case MY_DECK -> {
                ArrayList<Element> possibleElements = getValidSublist(actionPerformer.getLeftInDeck());
                if (possibleElements.size() > 0)
                    targets.add(Element.getRandomElement(possibleElements));
            }
            case OPPONENT_DECK -> {
                ArrayList<Element> possibleElements = getValidSublist(actionPerformer.getOpponent().getLeftInDeck());
                if (possibleElements.size() > 0)
                    targets.add(Element.getRandomElement(possibleElements));
            }
            case DISCOVER -> {
                ArrayList<Element> possibleElements = getValidSublist(GameController.getCardsList());
                for (int i = 0; i < 3; i++) {
                    Card card = (Card) Element.getRandomElement(possibleElements);
                    if (card != null)
                        targets.add(card);
                    possibleElements.remove(card);
                }
            }
        }
        return targets;
    }

    private ArrayList<Element> getValidSublist(ArrayList<? extends Element> list) {
        ArrayList<Element> validCards = new ArrayList<>();
        for (Element element : list)
            addIfValid(validCards, element);
        return validCards;
    }

    private boolean addIfValid(ArrayList<Element> targets, Element target) {
        if (target == null || !isValidTarget(target))
            return false;
        targets.add(target);
        return true;
    }

    private boolean isValidCaller(GamePlayer actionPerformer, Playable caller) {
        if (callerSide == null)
            return true;
        boolean ret = callerSide.equals(PlayerSide.SELF) && actionPerformer.owns(caller);
        ret |= callerSide.equals(PlayerSide.OPPONENT) && !actionPerformer.owns(caller);
        return ret;
    }

    private boolean isValidTarget(Element target) {
        return targetElementTypes.size() == 0 || targetElementTypes.contains(target.getElementType());
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

    protected void selectAndDoAction(GamePlayer actionPerformer, Playable caller) {
        selectionMode(actionPerformer, caller);
        selectionMode(actionPerformer.getOpponent(), caller);
    }

    private void selectionMode(GamePlayer gamePlayer, Playable caller) {
        ArrayList<Pair<Element, Node>> elements = gamePlayer.getCurrentElementsAndNodes();
        for (Pair<Element, Node> pair : elements)
            if (isValidTarget(pair.getKey()) && pair.getKey() instanceof Targetable targetable)
                pair.getValue().addEventHandler(MouseEvent.MOUSE_CLICKED, new SelectionEventHandler(gamePlayer.getGraphics(), caller, targetable, pair.getValue()));
            else if (pair.getKey() instanceof Targetable)
                TargetEventHandler.disableNode(pair.getValue());
    }

    private void discoverAndDoAction(GamePlayer actionPerformer, Card caller, Card played) {
        DiscoverGraphics discover = new DiscoverGraphics(actionPerformer, this, caller, getTarget(actionPerformer, caller, played));
        discover.display();
    }

    private class SelectionEventHandler extends TargetEventHandler {
        private GamePlayerGraphics player;
        private Playable caller;

        protected SelectionEventHandler(GamePlayerGraphics player, Playable caller, Targetable targetable, Node node) {
            super(targetable, node);
            this.player = player;
            this.caller = caller;
            initialize();
        }

        @Override
        protected void setSelectedTargetable(Targetable targetable) {
        }

        @Override
        protected boolean isEnough() {
            return true;
        }

        @Override
        protected void deselectedMode() {
            if (targetElementTypes.contains(MINION)) {
                player.enableMinions();
                player.getOpponent().enableMinions();
            }
            if (targetElementTypes.contains(HERO)) {
                player.enableHero();
                player.getOpponent().enableHero();
            }
        }

        @Override
        protected void oneSelectedMode() {}

        @Override
        protected void doAction() {
            Ability.this.doActionAndNext(player.getGamePlayer(), caller, (Element) targetable) ;
            player.getPlayGround().config();
        }
    }
}
