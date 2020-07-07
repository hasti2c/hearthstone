package elements.abilities;

import elements.Element;
import elements.cards.Minion;
import system.player.GamePlayer;

public class RemoveCard extends Ability {
    private RemoveCardType type;

    @Override
    protected void doAction(GamePlayer actionPerformer, Element caller, Element target) {
        GamePlayer player;
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
            case HAND -> player.getHand().remove(target);
            case DECK -> player.getLeftInDeck().remove(target);
        }
    }
}

enum RemoveCardType {
    BATTLEFIELD,
    WEAPON,
    HAND,
    DECK
}