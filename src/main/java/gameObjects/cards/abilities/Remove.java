package gameObjects.cards.abilities;

import gameObjects.Playable;
import gameObjects.cards.Card;
import gameObjects.cards.Element;
import gameObjects.cards.Minion;
import gameObjects.player.GamePlayer;

public class Remove extends Ability {
    private RemoveType type;

    @Override
    protected void doAction(GamePlayer actionPerformer, Playable caller, Element target) {
        GamePlayer player;
        if (actionPerformer.owns((Card) target))
            player = actionPerformer;
        else if (actionPerformer.getOpponent().owns((Card) target))
            player = actionPerformer.getOpponent();
        else
            return;

        switch (type) {
            case BATTLEFIELD -> {
                if (target instanceof Minion minion)
                    minion.setHealth(0);
            }
            case DECK -> player.getLeftInDeck().remove(target);
        }
    }
}

enum RemoveType {
    BATTLEFIELD,
    DECK
}