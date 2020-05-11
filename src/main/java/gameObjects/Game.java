package gameObjects;

import gameObjects.cards.Card;
import gameObjects.cards.Minion;
import gameObjects.cards.Weapon;
import gameObjects.heros.Deck;
import gameObjects.heros.Hero;

import java.util.ArrayList;

public class Game {
    private Player player;
    private Hero hero;
    private Deck deck;
    private ArrayList<Card> leftInDeck, hand = new ArrayList<>(), minionsInGame = new ArrayList<>();
    private Weapon currentWeapon;
    private int turn = 1, playerCount = 1, playerNumber = 0, mana = 1;
    private boolean usedHeroPower;

    public Game(Deck deck) {
        this.deck = deck;
        this.hero = deck.getHero().clone();
        this.player = hero.getPlayer();
        leftInDeck = new ArrayList<>(deck.getCards());
    }

    public void startGame() {
        for (int i = 0; i < 3; i++)
            draw();

    }

    public Hero getHero() {
        return hero;
    }

    public ArrayList<Card> getMinionsInGame() {
        return minionsInGame;
    }

    public ArrayList<Card> getHand() { return hand; }

    public ArrayList<Card> getLeftInDeck() { return leftInDeck; }

    private boolean draw() {
        if (leftInDeck.size() == 0)
            return false;

        int n = leftInDeck.size(), i = (int) (Math.floor(n * Math.random())) % n;
        Card card = leftInDeck.get(i);
        leftInDeck.remove(i);

        if (hand.size() < 12)
            hand.add(card);
        return true;
    }

    public boolean playCard(Card card) {
        if (!isMyTurn() || !hand.contains(card) || mana < card.getMana() || (card instanceof Minion && minionsInGame.size() >= 7))
            return false;

        deck.addUse(card);
        mana -= card.getMana();
        hand.remove(card);
        if (card instanceof Minion)
            minionsInGame.add(card);
        else if (card instanceof Weapon)
            currentWeapon = (Weapon) card;
        return true;
    }

    public boolean endTurn() {
        if (!isMyTurn())
            return false;

        turn++;
        usedHeroPower = false;
        draw();
        mana = Math.min(myTurnNumber(), 10);
        return true;
    }

    public boolean useHeroPower() {
        if (usedHeroPower)
            return false;
        usedHeroPower = true;
        return true;
    }

    private boolean isMyTurn() {
        return turn % playerCount == playerNumber;
    }

    private int myTurnNumber() {
        return (int) Math.ceil((double) turn / (double) playerCount);
    }

    public int getMana() {
        return mana;
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public boolean isHeroPowerUsed() {
        return usedHeroPower;
    }
}
