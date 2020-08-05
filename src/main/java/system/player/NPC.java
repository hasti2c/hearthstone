package system.player;

import elements.abilities.targets.*;
import elements.cards.*;
import elements.heros.*;
import system.*;

import java.util.*;

public class NPC extends Character {

    public NPC(Hero hero, Deck deck, Game game, PlayerFaction playerFaction) {
        super(hero, deck, game, playerFaction);
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
        for (Minion minion : minionsInGame)
            attackSum += minion.getAttack();
        return attackSum >= getOpponent().hero.getHealth();
    }

    private boolean attackWeakMinion() {
        ArrayList<Minion> opponentMinions = new ArrayList<>(getOpponent().minionsInGame);
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
        ArrayList<Minion> myMinions = new ArrayList<>(minionsInGame);
        myMinions.sort(Comparator.comparingInt(Minion::getAttack));

        for (Minion myMinion : myMinions)
            if (attack(myMinion, target))
                return true;
        return attack(hero, target);
    }

    private boolean playTopCard() {
        ArrayList<Card> cards = new ArrayList<>(hand);
        Collections.sort(cards);
        int i = cards.size() - 1;
        while (i >= 0 && !playCard(cards.get(i)))
            i--;
        return i >= 0;
    }
}
