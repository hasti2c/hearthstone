package gameObjects.cards.abilities;

import gameObjects.Playable;
import gameObjects.player.*;
import gameObjects.cards.*;

import java.util.ArrayList;

import static gameObjects.cards.abilities.AddCardType.*;

public class AddCard extends Ability {
    private AddCardType type;
    private ElementType discardElementType;
    private boolean insert = false;

    @Override
    protected void doAction(GamePlayer actionPerformer, Playable caller, Element target) {
        Card card = (Card) target;
        GamePlayer player;
        if (actionPerformer.getOpponent().owns(caller))
            player = actionPerformer.getOpponent();
        else
            player = actionPerformer;

        if (target.getElementType().equals(discardElementType)) {
            if (type.equals(DRAW))
                player.getLeftInDeck().remove(card);
            return;
        }

        switch (type) {
            case ALL_THREE -> {
                if (target instanceof Minion minion)
                    addToList(player.getMinionsInGame(), caller, minion, 7);
                else if (target instanceof Weapon weapon)
                    player.setCurrentWeapon(weapon);
                addToList(player.getHand(), caller, card, 12);
                addToList(player.getLeftInDeck(), caller, card, -1);
            }
            case DRAW -> {
                addToList(player.getHand(), caller, card, 12);
                player.getLeftInDeck().remove(card);
            }
            case HAND -> addToList(player.getHand(), caller, card, 12);
            case SUMMON -> {
                if (card instanceof Minion minion)
                    addToList(player.getMinionsInGame(), caller, minion, 7);
                else if (card instanceof Weapon weapon)
                    player.setCurrentWeapon(weapon);
            }
        }
    }

    private <T extends Card> void addToList(ArrayList<T> arrayList, Playable caller, T card, int maxSize) {
        int index = arrayList.indexOf(caller);
        if (!insert)
            index = -1;

        if (maxSize == -1 || arrayList.size() < maxSize) {
            if (index == -1)
                arrayList.add((T) card.clone());
            else
                arrayList.add(index, (T) card.clone());
        }
    }
}

enum AddCardType {
    ALL_THREE,
    SUMMON,
    DRAW,
    HAND
}