package system.game.characters;

import elements.abilities.targets.*;
import elements.cards.*;
import shared.Controller;
import system.player.*;

import java.util.*;

public class NPC extends Character {
    public NPC(Controller<?> controller, PlayerFaction playerFaction) {
        super(controller.getCurrentHero().clone(), controller.getCurrentDeck(), playerFaction);
    }

    @Override
    protected void initializeHelper() {}

    @Override
    protected void playCardHelper(Card card) {}

    @Override
    public void addWin() {}

    protected void startTurnHelper() {
        while (nextStep());
    }

    private boolean nextStep() {
        if (canWin() || getOpponent().hero.getHealth() < hero.getHealth() / 2)
            if(attackHero())
                return true;

        if (attackWeakMinion())
            return true;

        if (attackHero())
            return true;

        if (playTopCard())
            return true;

        if (useHeroPower())
            return true;

        game.nextTurn();
        return false;
    }

    private boolean canWin() {
        int attackSum = hero.getAttack(this);
        for (Minion minion : state.getMinionsInGame())
            attackSum += minion.getAttack();
        return attackSum >= getOpponent().hero.getHealth();
    }

    private boolean attackWeakMinion() {
        ArrayList<Minion> opponentMinions = new ArrayList<>(getOpponent().state.getMinionsInGame());
        opponentMinions.sort(Comparator.comparingInt(Minion::getHealth));
        for (Minion minion : opponentMinions)
            if (tryToAttack(minion))
                return true;
        return false;
    }

    private boolean attackHero() {
        return tryToAttack(getOpponent().hero);
    }

    private boolean tryToAttack(Attackable target) {
        ArrayList<Minion> myMinions = new ArrayList<>(state.getMinionsInGame());
        myMinions.sort(Comparator.comparingInt(Minion::getAttack));

        for (Minion myMinion : myMinions)
            if (attack(myMinion, target))
                return true;
        return attack(hero, target);
    }

    private boolean playTopCard() {
        ArrayList<Card> cards = new ArrayList<>(state.getHand());
        Collections.sort(cards);
        int i = cards.size() - 1;
        while (i >= 0 && !playCard(cards.get(i)))
            i--;
        return i >= 0;
    }
}
