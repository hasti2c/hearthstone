package gameObjects.cards.abilities;

import gameObjects.player.*;
import gameObjects.cards.*;

import static gameObjects.cards.abilities.AddCardType.*;

public class AddCard extends Ability {
    private AddCardType type;
    private CardType discardCardType;

    @Override
    protected void doAction(GamePlayer gamePlayer, Card caller, Card target) {
        GamePlayer player;
        if (gamePlayer.owns(caller))
            player = gamePlayer;
        else
            player = gamePlayer.getOpponent();

        if (target.getCardType().equals(discardCardType)) {
            if (type.equals(DRAW))
                player.getLeftInDeck().remove(target);
            return;
        }

        switch (type) {
            case ALL_THREE -> {
                addToMinionsInGame(player, target);
                addToDeck(player, target);
                addToHand(player, target);
            }
            case DRAW -> {
                addToHand(player, target);
                player.getLeftInDeck().remove(target);
            }
            case SUMMON -> addToMinionsInGame(player, target);
        }
    }

    private void addToMinionsInGame(GamePlayer gamePlayer, Card target) {
        if (gamePlayer.getMinionsInGame().size() < 7 && target instanceof Minion minion)
            gamePlayer.getMinionsInGame().add((Minion) minion.clone());
    }

    private void addToHand(GamePlayer gamePlayer, Card target) {
        if (gamePlayer.getHand().size() < 12)
            gamePlayer.getHand().add(target.clone());
    }

    private void addToDeck(GamePlayer gamePlayer, Card target) {
        gamePlayer.getLeftInDeck().add(target.clone());
    }
}

enum AddCardType {
    ALL_THREE,
    SUMMON,
    DRAW
}