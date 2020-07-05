package gameObjects.cards.abilities;

import gameObjects.player.*;
import gameObjects.cards.*;

import java.util.ArrayList;

import static gameObjects.cards.abilities.AddCardType.*;

public class AddCard extends Ability {
    private AddCardType type;
    private CardType discardCardType;
    private boolean insert = false;

    @Override
    protected void doAction(GamePlayer actionPerformer, Card caller, Card target) {
        GamePlayer player;
        if (actionPerformer.owns(caller))
            player = actionPerformer;
        else
            player = actionPerformer.getOpponent();

        if (target.getCardType().equals(discardCardType)) {
            if (type.equals(DRAW))
                player.getLeftInDeck().remove(target);
            return;
        }

        switch (type) {
            case ALL_THREE -> {
                if (target instanceof Minion minion)
                    addToList(player.getMinionsInGame(), caller, minion, 7);
                else if (target instanceof Weapon weapon)
                    player.setCurrentWeapon(weapon);
                addToList(player.getHand(), caller, target, 12);
                addToList(player.getLeftInDeck(), caller, target, -1);
            }
            case DRAW -> {
                addToList(player.getHand(), caller, target, 12);
                player.getLeftInDeck().remove(target);
            }
            case SUMMON -> {
                if (target instanceof Minion minion)
                    addToList(player.getMinionsInGame(), caller, minion, 7);
                else if (target instanceof Weapon weapon)
                    player.setCurrentWeapon(weapon);
            }
        }
    }

    private <T extends Card> void addToList(ArrayList<T> arrayList, Card caller, T target, int maxSize) {
        int index = arrayList.indexOf(caller);
        if (!insert)
            index = -1;

        if (maxSize == -1 || arrayList.size() < maxSize) {
            if (index == -1)
                arrayList.add((T) target.clone());
            else
                arrayList.add(index, (T) target.clone());
        }
    }
}

enum AddCardType {
    ALL_THREE,
    SUMMON,
    DRAW
}