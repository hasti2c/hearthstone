package gameObjects.cards.abilities;

import controllers.game.*;
import gameObjects.*;
import gameObjects.player.*;
import gameObjects.cards.*;
import java.util.*;

public abstract class Ability implements Configable {
    protected AbilityType abilityType;
    protected PlayerSide callerSide;
    protected TargetType targetType;
    protected CardType targetCardType;
    private int times = 1;
    private int targetAttack, targetHealth;

    @Override
    public void initialize(GameController controller) {}

    @Override
    public String getJsonPath(GameController controller, String name) {
        return null;
    }

    public void callDoAction(GamePlayer actionPerformer, Card caller, Card played) {
        for (int i = 0; i < times; i++) {
            for (Card target : getTarget(actionPerformer, caller, played))
                if (isValidCaller(actionPerformer, caller) && caller.isValid() && target.isValid())
                    doAction(actionPerformer, caller, target);
        }
    }

    protected abstract void doAction(GamePlayer gamePlayer, Card caller, Card target);

    private ArrayList<Card> getTarget(GamePlayer gamePlayer, Card caller, Card played) {
        ArrayList<Card> targets = new ArrayList<>();
        switch (targetType) {
            case SELF -> addIfValid(targets, caller);
            case PLAYED -> addIfValid(targets, played);
            case ALL_ELSE -> {
                for (Card card : gamePlayer.getOpponent().getMinionsInGame())
                    addIfValid(targets, card);
                for (Card card : gamePlayer.getMinionsInGame())
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
            case DECK -> addIfValid(targets, getRandomCard(gamePlayer.getLeftInDeck()));
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

    private Card getRandomCard(ArrayList<Card> possibleCards) {
        if (!hasValidTarget(possibleCards))
            return null;
        Card card;
        do {
            card = Card.getRandomCard(possibleCards);
        } while (!isValidTarget(card));
        return card;
    }
}
