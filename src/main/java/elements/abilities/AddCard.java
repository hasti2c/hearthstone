package elements.abilities;

import elements.*;
import elements.cards.*;
import system.game.characters.Character;

import java.util.*;

import static elements.abilities.AddCardType.*;

public class AddCard extends Ability {
    private AddCardType type;
    private ElementType discardElementType;
    private boolean insert = false;

    @Override
    protected void doAction(Character actionPerformer, Element caller, Element target) {
        Card card = (Card) target;
        Character player;
        if (actionPerformer.getOpponent().owns(caller))
            player = actionPerformer.getOpponent();
        else
            player = actionPerformer;
        if (type == OPPONENT_SUMMON)
            player = player.getOpponent();

        ArrayList<Card> leftInDeck = player.getState().getLeftInDeck(), hand = player.getState().getHand();
        ArrayList<Minion> minionsInGame = player.getState().getMinionsInGame();

        if (target.getElementType().equals(discardElementType)) {
            if (type.equals(DRAW))
                leftInDeck.remove(card);
            return;
        }

        switch (type) {
            case ALL_THREE -> {
                if (target instanceof Minion minion) {
                    player.getHero().getHeroClass().doHeroAction(minion);
                    addToList(minionsInGame, caller, minion, 7);
                }
                else if (target instanceof Weapon weapon)
                    player.setCurrentWeapon(weapon);
                addToList(hand, caller, card, 12);
                addToList(leftInDeck, caller, card, -1);
            }
            case DRAW -> {
                addToList(hand, caller, card, 12);
                leftInDeck.remove(card);
            }
            case HAND -> addToList(hand, caller, card, 12);
            case SUMMON, OPPONENT_SUMMON -> {
                if (card instanceof Minion minion) {
                    player.getHero().getHeroClass().doHeroAction(minion);
                    addToList(minionsInGame, caller, minion, 7);
                }
                else if (card instanceof Weapon weapon)
                    player.setCurrentWeapon(weapon);
            }
            case WEAPON -> {
                if (target instanceof Weapon weapon)
                    player.setCurrentWeapon(weapon);
            }
        }
    }

    private <T extends Card> void addToList(ArrayList<T> arrayList, Element caller, T card, int maxSize) {
        int index = arrayList.indexOf(caller);
        if (!insert)
            index = -1;

        if (maxSize == -1 || arrayList.size() < maxSize) {
            if (index == -1)
                arrayList.add((T) card.copy());
            else
                arrayList.add(index, (T) card.copy());
        }
    }
}

enum AddCardType {
    ALL_THREE,
    SUMMON,
    OPPONENT_SUMMON,
    WEAPON,
    DRAW,
    HAND
}