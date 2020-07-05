package gameObjects.player;

import controllers.game.*;
import gameObjects.*;
import gameObjects.cards.abilities.targets.Attackable;
import graphics.directories.playground.GamePlayerGraphics;
import gameObjects.cards.*;
import gameObjects.heros.*;
import java.lang.reflect.*;
import java.util.*;

public class GamePlayer {
    private final Inventory inventory;
    private ArrayList<Card> leftInDeck;
    private final ArrayList<Card> hand = new ArrayList<>();
    private final ArrayList<Minion> minionsInGame = new ArrayList<>();
    private Spell lastSpell;
    private Weapon currentWeapon;
    private final int playerNumber;
    private int mana = 1;
    private boolean usedHeroPower, randomDraw = true;
    private final PlayerFaction playerFaction;
    private final Game game;
    private GamePlayerGraphics graphics;

    public GamePlayer(GameController controller, Game game, PlayerFaction playerFaction) {
        Player player = controller.getCurrentPlayer();
        inventory = player.getInventory().clone();
        leftInDeck = new ArrayList<>();
        for (Card card : inventory.getCurrentDeck().getCards())
            leftInDeck.add(card.clone());
        this.game = game;
        this.playerFaction = playerFaction;
        playerNumber = playerFaction.getPlayerNumber();
    }

    public GamePlayer(GameController controller, Game game, PlayerFaction playerFaction, Deck deck) {
        this(controller, game, playerFaction);
        inventory.setCurrentDeck(deck.clone(inventory));
        leftInDeck = new ArrayList<>(inventory.getCurrentDeck().getCards());
        randomDraw = false;
    }

    public void initialize() {
        for (int i = 0; i < 3; i++)
            draw();
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
            doCardAction("doActionOnDraw", card);
        }
        return true;
    }

    private Card getNextCard(ArrayList<Card> cards) {
        if (randomDraw)
            return Card.getRandomCard(cards);
        return leftInDeck.get(0);
    }

    public boolean playCard(Card card) {
        if (!isMyTurn() || !hand.contains(card) || mana < card.getMana() || (card instanceof Minion && minionsInGame.size() >= 7))
            return false;

        inventory.getCurrentDeck().addUse(card);
        mana -= card.getMana();
        hand.remove(card);
        if (card instanceof Minion minion)
            minionsInGame.add(minion);
        else if (card instanceof Weapon weapon) {
            setCurrentWeapon(weapon);
        } else if (card instanceof Spell spell)
            lastSpell = spell;
        doCardAction("doActionOnPlay", card);
        return true;
    }

    public void setCurrentWeapon(Weapon weapon) {
        currentWeapon = weapon;
        inventory.getCurrentHero().setHasAttacked(false);
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

    public boolean owns(Card card) {
        return minionsInGame.contains(card) || lastSpell == card || currentWeapon == card || hand.contains(card) || leftInDeck.contains(card);
    }

    public GamePlayer getOpponent() {
        if (game.getCurrentPlayer() == this)
            return game.getOtherPlayer();
        return game.getCurrentPlayer();
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

    public void endTurn() {
        doCardAction("doActionOnEndTurn");
    }

    public boolean canAttack(Attackable attackable) {
        boolean ret = this == game.getCurrentPlayer() && !attackable.getHasAttacked();
        if (attackable instanceof Minion minion)
            return ret && minionsInGame.contains(minion);
        return ret && currentWeapon != null;
    }

    public boolean canBeAttacked(Attackable attackable) {
        return this != game.getCurrentPlayer() && (attackable instanceof Hero || minionsInGame.contains(attackable));
    }

    public boolean attack(Attackable attacker, Attackable defender) {
        if (!canAttack(attacker) || !getOpponent().canBeAttacked(defender))
            return false;
        rawAttack(attacker, defender);
        attacker.setHasAttacked(true);
        clearDeadMinions();
        getOpponent().clearDeadMinions();
        return true;
    }

    public void rawAttack(Attackable attacker, Attackable defender) {
        if (defender instanceof Card card)
            doCardAction("doActionOnDamaged", card);
        attacker.setHealth(attacker.getHealth() - defender.getAttack(getOpponent()));
        defender.setHealth(defender.getHealth() - attacker.getAttack(this));
        if (attacker instanceof Hero && currentWeapon != null)
            currentWeapon.setDurability(currentWeapon.getDurability() - 1);
    }

    public void clearDeadMinions() {
        for (int i = 0; i < minionsInGame.size(); i++)
            if (!minionsInGame.get(i).isValid())
                minionsInGame.remove(i--);
    }

    private void doCardAction(String actionName) {
        try {
            Method method = Card.class.getDeclaredMethod(actionName, GamePlayer.class);
            for (Card card : minionsInGame)
                method.invoke(card, this);
            clearDeadMinions();
            getOpponent().clearDeadMinions();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //TODO cleaaaaan soon

    private void doCardAction(String actionName, Card input) {
        try {
            Method method = Card.class.getDeclaredMethod(actionName, GamePlayer.class, Card.class);

            int n = minionsInGame.size();
            if (n > 0)
                for (int i = 0; i < n; i++)
                    method.invoke(minionsInGame.get(i), this, input);
            if (lastSpell != null)
                method.invoke(lastSpell, this, input);

            n = getOpponent().minionsInGame.size();
            if (n > 0)
                for (int i = 0; i < n; i++)
                    method.invoke(getOpponent().minionsInGame.get(i), this, input);
            if (getOpponent().lastSpell != null)
                method.invoke(getOpponent().lastSpell, this, input);

            clearDeadMinions();
            getOpponent().clearDeadMinions();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void setGraphics(GamePlayerGraphics graphics) {
        this.graphics = graphics;
    }

    public GamePlayerGraphics getGraphics() {
        return graphics;
    }
}
