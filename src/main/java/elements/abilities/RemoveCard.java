package elements.abilities;

import elements.*;
import elements.cards.*;
import system.game.Character;

public class RemoveCard extends Ability {
    private RemoveCardType type;

    @Override
    protected void doAction(Character actionPerformer, Element caller, Element target) {
        Character player;
        if (actionPerformer.owns(target))
            player = actionPerformer;
        else if (actionPerformer.getOpponent().owns(target))
            player = actionPerformer.getOpponent();
        else
            return;

        switch (type) {
            case BATTLEFIELD -> {
                if (target instanceof Minion minion)
                    minion.setHealth(0);
            }
            case WEAPON -> player.setCurrentWeapon(null);
            case HAND -> player.getState().getHand().remove(target);
            case DECK -> player.getState().getLeftInDeck().remove(target);
        }
    }
}

enum RemoveCardType {
    BATTLEFIELD,
    WEAPON,
    HAND,
    DECK
}