package elements.abilities;

import elements.*;
import elements.abilities.targets.*;
import shared.*;
import system.*;
import elements.cards.*;
import system.player.*;
import system.player.Character;

import java.util.*;

import static elements.abilities.targets.TargetType.*;

public abstract class Ability implements Configable {
    protected AbilityType abilityType;
    protected PlayerSide callerSide;
    protected TargetType targetType;
    protected ArrayList<ElementType> targetElementTypes = new ArrayList<>();
    private Element specificTarget;
    private int times = 1;
    private int targetAttack, targetHealth, targetMana;
    private boolean hasTaunt = false, hasRush = false;
    private Ability nextAbility;
    private AddCard nextAddCard;
    private ChangeStats nextChangeStats;
    private RemoveCard nextRemoveCard;
    private Attack nextAttack;

    @Override
    public void initialize() {
        setNextAbility();
    }

    @Override
    public String getJsonPath(String name) {
        return null;
    }

    private void callDoAction(Character actionPerformer, Element caller, Element played, Element selected, boolean assertValidCaller) {
        handleSelectionForNPC(actionPerformer);

        for (int i = 0; i < times; i++)
            if (targetType.equals(SELECTED) || targetType.equals(DISCOVER))
                doActionAndNext(actionPerformer, caller, selected);
            else
                for (Element target : getTarget(actionPerformer, caller, (Card) played)) {
                    if (isValidCaller(actionPerformer, caller) && (assertValidCaller || caller.isValid()) && target.isValid())
                        doActionAndNext(actionPerformer, caller, target);
                }
    }

    private void handleSelectionForNPC(Character actionPerformer) {
        if (actionPerformer instanceof GamePlayer)
            return;
        if (targetType == SELECTED)
            targetType = IN_GAME;
        else
            targetType = RANDOM;
    }

    public void callDoAction(Character actionPerformer, Playable caller, Element played, Element selected) {
        callDoAction(actionPerformer, caller, played, selected, false);
    }

    public void doActionAndNext(Character actionPerformer, Element caller, Element target) {
        doAction(actionPerformer, caller, target);
        if (nextAbility != null)
            nextAbility.callDoAction(actionPerformer, target, null, null,true);
    }

    protected abstract void doAction(Character actionPerformer, Element caller, Element target);

    public ArrayList<Element> getTarget(Character actionPerformer, Element caller, Card played) {
        ArrayList<Card> cardsList = GameData.getInstance().getCardsList();
        ArrayList<Element> targets = new ArrayList<>();
        switch (targetType) {
            case SELF -> addIfValid(targets, caller);
            case SPECIFIC -> addIfValid(targets, specificTarget);
            case PLAYED -> addIfValid(targets, played);
            case ALL_ELSE -> {
                for (Card card : actionPerformer.getOpponent().getMinionsInGame())
                    addIfValid(targets, card);
                for (Card card : actionPerformer.getMinionsInGame())
                    addIfValid(targets, card);
                addIfValid(targets, actionPerformer.getHero());
                addIfValid(targets, actionPerformer.getOpponent().getHero());
                targets.remove(caller);
            }
            case RANDOM_FRIENDLY -> {
                ArrayList<Element> possibleElements = new ArrayList<>();
                for (Card card : actionPerformer.getMinionsInGame())
                    addIfValid(possibleElements, card);
                addIfValid(possibleElements, actionPerformer.getHero());
                targets.add(Element.getRandomElement(possibleElements));
            }
            case RANDOM_ENEMY -> {
                ArrayList<Element> possibleElements = new ArrayList<>();
                for (Card card : actionPerformer.getOpponent().getMinionsInGame())
                    addIfValid(possibleElements, card);
                addIfValid(possibleElements, actionPerformer.getOpponent().getHero());
                targets.add(Element.getRandomElement(possibleElements));
            }
            case RANDOM -> {
                ArrayList<Element> possibleElements = getValidSublist(cardsList);
                if (possibleElements.size() > 0)
                    targets.add(Element.getRandomElement(possibleElements));
            }
            case BY_STATS -> {
                ArrayList<Element> possibleElements = new ArrayList<>();
                for (Card card : cardsList)
                    if (isValidTarget(card) && card instanceof Minion minion && matchesStats(minion))
                        possibleElements.add(card);
                if (possibleElements.size() > 0)
                    targets.add(Element.getRandomElement(possibleElements));
                else {
                    Minion minion = (Minion) Element.getRandomElement(getValidSublist(cardsList));
                    if (minion == null)
                        break;
                    Minion minionClone = (Minion) minion.clone();
                    if (targetHealth != 0)
                        minionClone.setHealth(targetHealth);
                    if (targetAttack != 0)
                        minionClone.setAttack(targetAttack);
                    if (targetMana != 0)
                        minionClone.setMana(targetMana);
                    if (hasTaunt)
                        minionClone.setTaunt(true);
                    if (hasRush)
                        minionClone.setRush(true);
                    targets.add(minionClone);
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
            case OPPONENT_HAND -> {
                ArrayList<Element> possibleElements = getValidSublist(actionPerformer.getOpponent().getHand());
                if (possibleElements.size() > 0)
                    targets.add(Element.getRandomElement(possibleElements));
            }
            case IN_GAME -> {
                ArrayList<Element> possibleElements = getValidSublist(actionPerformer.getMinionsInGame());
                addIfValid(possibleElements, actionPerformer.getCurrentWeapon());
                addIfValid(possibleElements, actionPerformer.getHero());
                possibleElements.addAll(getValidSublist(actionPerformer.getOpponent().getMinionsInGame()));
                if (possibleElements.size() > 0)
                    targets.add(Element.getRandomElement(possibleElements));
            }
            case BATTLEFIELD -> {
                ArrayList<Element> possibleElements = getValidSublist(actionPerformer.getMinionsInGame());
                possibleElements.addAll(getValidSublist(actionPerformer.getOpponent().getMinionsInGame()));
                if (possibleElements.size() > 0)
                    targets.add(Element.getRandomElement(possibleElements));
            }
            case MY_WEAPON -> {
                if (actionPerformer.getCurrentWeapon() != null)
                    addIfValid(targets, actionPerformer.getCurrentWeapon());
            }
            case OPPONENT_WEAPON -> {
                if (actionPerformer.getOpponent().getCurrentWeapon() != null)
                    addIfValid(targets, actionPerformer.getCurrentWeapon());
            }
            case DISCOVER -> {
                ArrayList<Element> possibleElements = getValidSublist(cardsList);
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

    private boolean isValidCaller(Character actionPerformer, Element caller) {
        if (callerSide == null)
            return true;
        boolean ret = callerSide.equals(PlayerSide.SELF) && actionPerformer.owns(caller);
        ret |= callerSide.equals(PlayerSide.OPPONENT) && !actionPerformer.owns(caller);
        return ret;
    }

    public boolean isValidTarget(Element target) {
        return targetElementTypes.size() == 0 || targetElementTypes.contains(target.getElementType());
    }

    private boolean matchesStats(Minion minion) {
        boolean ret = targetHealth == 0 || minion.getHealth() == targetHealth;
        ret &= targetAttack == 0 || minion.getAttack() == targetAttack;
        ret &= targetMana == 0 || minion.getMana() == targetMana;
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

    public void setNextAbility() {
        if (nextAddCard != null)
            nextAbility = nextAddCard;
        else if (nextChangeStats != null)
            nextAbility = nextChangeStats;
        else if (nextRemoveCard != null)
            nextAbility = nextRemoveCard;
        else if (nextAbility != null)
            nextAbility = nextAttack;
    }

    public void removeNextAbility() {
        nextAbility = null;
    }

    public Ability getNextAbility() {
        return nextAbility;
    }

    public void setSpecificTarget(Card reward) {
        targetType = SPECIFIC;
        specificTarget = reward;
    }

    public static Ability getRandomAbility(ArrayList<Ability> abilities) {
        int n = abilities.size(), i = (int) Math.floor(Math.random() * n) % n;
        return abilities.get(i);
    }

    public ArrayList<ElementType> getTargetElementTypes() {
        return targetElementTypes;
    }
}
