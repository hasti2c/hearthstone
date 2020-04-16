package directories;

import cards.Card;
import game.*;
import heros.Hero;

import java.util.ArrayList;

public class PlayGround extends Directory {
    Directory cardsInGame, hand, leftInDeck;

    PlayGround (Directory parent, Player myPlayer) {
        super("play", parent, myPlayer);
        createCardsInGame();
        createHand();
        createLeftInDeck();
    }

    private void createCardsInGame() {
        cardsInGame = new Directory("cards in game", this, getMyPlayer());
        addChild(cardsInGame);
    }

    private void createHand() {
        hand = new Directory("hand", this, getMyPlayer());
        addChild(hand);
    }

    private void createLeftInDeck() {
        leftInDeck = new Directory("left in deck", this, getMyPlayer());
        addChild(leftInDeck);
        Hero h = getMyPlayer().getCurrentHero();
        if (h == null)
            return;
        for (Card c : h.getHeroDeck())
            leftInDeck.addContent(c);
    }

}
