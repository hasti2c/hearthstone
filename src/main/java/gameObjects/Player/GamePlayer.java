package gameObjects.Player;

import controllers.game.GameController;
import gameObjects.Game;
import gameObjects.Targetable;
import gameObjects.cards.Card;
import gameObjects.cards.Minion;
import gameObjects.cards.Weapon;
import gameObjects.heros.Deck;
import gameObjects.heros.Hero;

import java.util.ArrayList;

public class GamePlayer {
    private final Inventory inventory;
    private ArrayList<Card> leftInDeck;
    private final ArrayList<Card> hand = new ArrayList<>();
    private final ArrayList<Minion> minionsInGame = new ArrayList<>();
    private Weapon currentWeapon;
    private final int playerNumber;
    private int mana = 1;
    private boolean usedHeroPower, randomDraw = true;
    private final PlayerFaction playerFaction;
    private final Game game;

    public GamePlayer(GameController controller, Game game, PlayerFaction playerFaction) {
        Player player = controller.getCurrentPlayer();
        inventory = player.getInventory().clone();
        leftInDeck = new ArrayList<>(inventory.getCurrentDeck().getCards());
        this.game = game;
        this.playerFaction = playerFaction;
        playerNumber = playerFaction.getPlayerNumber();
        for (int i = 0; i < 3; i++)
            draw();
    }

    public GamePlayer(GameController controller, Game game, PlayerFaction playerFaction, Deck deck) {
        this(controller, game, playerFaction);
        inventory.setCurrentDeck(deck.clone(inventory));
        leftInDeck = new ArrayList<>(inventory.getCurrentDeck().getCards());
        randomDraw = false;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public PlayerFaction getPlayerFaction() {
        return playerFaction;
    }

    public ArrayList<Card> getLeftInDeck() {
        return leftInDeck;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public ArrayList<Minion> getMinionsInGame() {
        return minionsInGame;
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public boolean isHeroPowerUsed() {
        return usedHeroPower;
    }

    public void setUsedHeroPower(boolean usedHeroPower) {
        this.usedHeroPower = usedHeroPower;
    }

    public boolean draw() {
        if (leftInDeck.size() == 0)
            return false;

        ArrayList<Card> questAndReward = new ArrayList<>();
        for (Card c : leftInDeck)
            if (c instanceof gameObjects.cards.QuestAndReward)
                questAndReward.add(c);

        Card card;
        if (questAndReward.size() > 0)
            card = getNextCard(questAndReward);
        else if (leftInDeck.size() > 0)
            card = getNextCard(leftInDeck);
        else
            return false;

        leftInDeck.remove(card);
        if (hand.size() < 12) {
            hand.add(card);
            inventory.doCardAction("doActionOnDraw", card);
        }
        return true;
    }

    private Card getNextCard(ArrayList<Card> cards) {
        if (randomDraw)
            return getRandomCard(cards);
        return leftInDeck.get(0);
    }

    private Card getRandomCard(ArrayList<Card> cards) {
        int n = cards.size(), i = (int) (Math.floor(n * Math.random())) % n;
        return cards.get(i);
    }

    public boolean playCard(Card card) {
        if (!isMyTurn() || !hand.contains(card) || mana < card.getMana() || (card instanceof Minion && minionsInGame.size() >= 7))
            return false;

        inventory.getCurrentDeck().addUse(card);
        mana -= card.getMana();
        hand.remove(card);
        if (card instanceof Minion minion)
            minionsInGame.add(minion);
        else if (card instanceof Weapon w) {
            currentWeapon = w;
            inventory.getCurrentHero().setHasAttacked(false);
        }
        return true;
    }

    public boolean useHeroPower() {
        if (!isMyTurn() || usedHeroPower || mana < inventory.getCurrentHero().getHeroPower().getMana())
            return false;
        setUsedHeroPower(true);
        setMana(getMana() - inventory.getCurrentHero().getHeroPower().getMana());
        return true;
    }

    public int getMyTurnNumber() {
        return (int) Math.floor((double) game.getTurn() / (double) game.getPlayerCount()) + 1;
    }

    private boolean isMyTurn() {
        return game.getTurn() % game.getPlayerCount() == playerNumber;
    }

    public void startTurn() {
        if (getMyTurnNumber() != 1)
            draw();
        usedHeroPower = false;

        for (Card card : inventory.getCurrentDeck().getCards())
            if (card instanceof Minion minion)
                minion.setHasAttacked(false);
        inventory.getCurrentHero().setHasAttacked(false);

        mana = Math.min(getMyTurnNumber(), 10);
    }

    public boolean canAttack(Targetable targetable) {
        boolean ret = this == game.getCurrentPlayer() && !targetable.getHasAttacked();
        if (targetable instanceof Minion minion)
            return ret && minionsInGame.contains(minion);
        return ret && currentWeapon != null;
    }

    public boolean canBeAttacked(Targetable targetable) {
        return this != game.getCurrentPlayer() && (targetable instanceof Hero || minionsInGame.contains(targetable));
    }

    public boolean attack(Targetable attacker, Targetable defender) {
        GamePlayer other = game.getOtherPlayer();
        if (!canAttack(attacker) || !other.canBeAttacked(defender))
            return false;

        attacker.setHealth(attacker.getHealth() - defender.getAttack(other));
        defender.setHealth(defender.getHealth() - attacker.getAttack(this));

        if (attacker instanceof Minion minion && minion.getHealth() <= 0)
            minionsInGame.remove(minion);
        if (defender instanceof Minion minion && minion.getHealth() <= 0)
            other.minionsInGame.remove(minion);
        attacker.setHasAttacked(true);

        return true;
    }
}
