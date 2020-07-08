package system.player;

import controllers.game.*;
import elements.Element;
import elements.abilities.targets.Attackable;
import elements.Playable;
import system.Game;
import graphics.directories.playground.GamePlayerGraphics;
import elements.cards.*;
import elements.heros.*;
import javafx.scene.Node;
import javafx.util.Pair;

import java.lang.reflect.*;
import java.util.*;

public class GamePlayer {
    private final String name;
    private final Inventory inventory;
    private final Hero hero;
    private final ArrayList<Card> leftInDeck;
    private final ArrayList<Card> hand = new ArrayList<>();
    private final ArrayList<Minion> minionsInGame = new ArrayList<>();
    private Spell lastSpell;
    private Weapon currentWeapon;
    private Passive passive;
    private final int playerNumber;
    private int mana = 1, drawCap = 1, heroPowerCap = 1, heroPowerCount = 0;
    private boolean randomDraw = true;
    private final PlayerFaction playerFaction;
    private final Game game;
    private GamePlayerGraphics graphics;

    public GamePlayer(GameController controller, Game game, PlayerFaction playerFaction) {
        Player player = controller.getCurrentPlayer();
        name = player.toString();
        inventory = player.getInventory();
        hero = inventory.getCurrentHero().clone();
        leftInDeck = new ArrayList<>();
        for (Card card : inventory.getCurrentDeck().getCards())
            leftInDeck.add(card.clone());
        this.game = game;
        this.playerFaction = playerFaction;
        playerNumber = playerFaction.getPlayerNumber();
    }

    public GamePlayer(GameController controller, Game game, PlayerFaction playerFaction, Deck deck) {
        this(controller, game, playerFaction);
        inventory.setCurrentDeck(deck.clone());

        for (Card card : inventory.getCurrentDeck().getCards())
            leftInDeck.add(card.clone());
        randomDraw = false;
    }

    public void initialize() {
        if (passive != null) {
            mana = 1 + passive.getTurnManaPromotion(1);
            drawCap = passive.getDrawCap();
            heroPowerCap = passive.getHeroPowerCap();
        }

        for (int i = 0; i < 3; i++)
            draw();
    }

    public String toString() {
        return name;
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

    public void setPassive(Passive passive) {
        this.passive = passive;
    }

    public boolean draw() {
        if (leftInDeck.size() == 0)
            return false;

        ArrayList<Card> questAndReward = new ArrayList<>();
        for (Card c : leftInDeck)
            if (c instanceof elements.cards.QuestAndReward)
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
            doCardAction("doActionOnDraw");
        }
        return true;
    }

    private Card getNextCard(ArrayList<Card> cards) {
        if (randomDraw)
            return Element.getRandomElement(cards);
        return leftInDeck.get(0);
    }

    public boolean playCard(Card card) {
        if (!isMyTurn() || !hand.contains(card) || mana < card.getGameMana(this) || (card instanceof Minion && minionsInGame.size() >= 7))
            return false;

        mana -= card.getGameMana(this);
        hand.remove(card);
        if (card instanceof Minion minion) {
            hero.getHeroClass().doHeroAction(minion);
            minionsInGame.add(minion);
            minion.setAsleep(true);
        } else if (card instanceof Weapon weapon) {
            setCurrentWeapon(weapon);
        } else if (card instanceof Spell spell)
            lastSpell = spell;
        inventory.getCurrentDeck().addUse(card);
        doCardAction("doActionOnPlay", card);
        return true;
    }

    public void setCurrentWeapon(Weapon weapon) {
        currentWeapon = weapon;
        if (weapon != null)
            hero.getHeroPower().promote();
        else
            hero.getHeroPower().demote();
        hero.setHasAttacked(false);
    }

    public boolean useHeroPower() {
        HeroPower heroPower = hero.getHeroPower();
        if (!isMyTurn() || heroPowerCount >= heroPowerCap || mana < heroPower.getGameMana(this))
            return false;
        heroPowerCount++;
        heroPower.reduceCost(this);
        doCardAction("doActionOnHeroPower");
        return true;
    }

    public boolean canUseHeroPower() {
        return heroPowerCount < heroPowerCap && !hero.getHeroPower().isPassive();
    }

    public int getMyTurnNumber() {
        return (int) Math.floor((double) game.getTurn() / (double) game.getPlayerCount()) + 1;
    }

    private boolean isMyTurn() {
        return game.getTurn() % game.getPlayerCount() == playerNumber;
    }

    public boolean owns(Element element) {
        boolean ret = minionsInGame.contains(element) || hand.contains(element) || leftInDeck.contains(element);
        ret |= lastSpell == element || currentWeapon == element;
        ret |= hero.getHeroPower() == element;
        ret |= hero == element;
        return ret;
    }

    public GamePlayer getOpponent() {
        if (game.getCurrentPlayer() == this)
            return game.getOtherPlayer();
        return game.getCurrentPlayer();
    }

    public void startTurn() {
        if (getMyTurnNumber() != 1)
            for (int i = 0; i < drawCap; i++)
                draw();
        heroPowerCount = 0;

        for (Minion minion : minionsInGame)
            minion.setHasAttacked(false);
        hero.setHasAttacked(false);

        mana = Math.min(getMyTurnNumber(), 10);
        if (passive != null)
            mana += passive.getTurnManaPromotion(getMyTurnNumber());
    }

    public void endTurn() {
        doCardAction("doActionOnEndTurn");
        if (passive != null)
            passive.doEndTurnAction(minionsInGame);
        for (Minion minion : getMinionsInGame())
            minion.setAsleep(false);
    }

    public boolean canAttack(Attackable attacker) {
        boolean ret = this == game.getCurrentPlayer() && !attacker.getHasAttacked();
        if (attacker instanceof Hero)
            return ret && currentWeapon != null;
        Minion minion = (Minion) attacker;
        ret &= minionsInGame.contains(minion) && (!minion.getAsleep() || minion.getRush());
        return ret;
        /*
        if (target == null || target instanceof Minion)
            ret &= !minion.getAsleep() || minion.getRush();
        else
            ret &= !minion.getAsleep();
        return ret;
         */
    }

    public boolean canBeAttacked(Attackable attacker, Attackable target) {
        boolean ret = this != game.getCurrentPlayer();
        if (target instanceof Minion minion) {
            ret &= minionsInGame.contains(minion);
            ret &= !hasAnyTaunt() || minion.getTaunt();
        } else {
            ret &= !hasAnyTaunt();
            if (attacker instanceof Minion minion)
                ret &= !minion.getAsleep();
        }
        return ret;
    }

    private boolean hasAnyTaunt() {
        for (Minion minion : minionsInGame)
            if (minion.getTaunt())
                return true;
        return false;
    }

    public boolean attack(Attackable attacker, Attackable defender) {
        if (!canAttack(attacker) || !getOpponent().canBeAttacked(attacker, defender))
            return false;
        rawAttack(attacker, defender);
        attacker.setHasAttacked(true);
        clearDeadMinions();
        getOpponent().clearDeadMinions();
        return true;
    }

    public void rawAttack(Attackable attacker, Attackable defender) {
        attacker.doDamage(this, defender.getAttack(getOpponent()));
        defender.doDamage(this, attacker.getAttack(this));
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
            Method method = Playable.class.getDeclaredMethod(actionName, GamePlayer.class);
            for (Playable playable : getPlayables())
                method.invoke(playable, this);
            for (Playable playable : getOpponent().getPlayables())
                method.invoke(playable, this);

            clearDeadMinions();
            getOpponent().clearDeadMinions();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //TODO cleaaaaan soon

    public void doCardAction(String actionName, Card input) {
        try {
            Method method = Playable.class.getDeclaredMethod(actionName, GamePlayer.class, Card.class);

            for (Playable playable : getPlayables())
                method.invoke(playable, this, input);
            for (Playable playable : getOpponent().getPlayables())
                method.invoke(playable, this, input);

            clearDeadMinions();
            getOpponent().clearDeadMinions();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Playable> getPlayables() {
        ArrayList<Playable> ret = new ArrayList<>(minionsInGame);
        if (lastSpell != null)
            ret.add(lastSpell);
        if (currentWeapon != null)
            ret.add(currentWeapon);
        ret.add(hero.getHeroPower());
        return ret;
    }

    public void setGraphics(GamePlayerGraphics graphics) {
        this.graphics = graphics;
    }

    public GamePlayerGraphics getGraphics() {
        return graphics;
    }

    public ArrayList<Pair<Element, Node>> getCurrentElementsAndNodes() {
        ArrayList<Pair<Element, Node>> elements = new ArrayList<>();
        graphics.reloadMinionsHBox();
        graphics.reloadHeroImage();
        for (int i = 0; i < minionsInGame.size(); i++)
            elements.add(new Pair<>(minionsInGame.get(i), graphics.getMinionsHBox().getChildren().get(i)));
        if (currentWeapon != null)
            elements.add(new Pair<>(currentWeapon, graphics.getWeaponNode()));
        if (canUseHeroPower())
            elements.add(new Pair<>(hero.getHeroPower(), graphics.getHeroPowerNode()));
        elements.add(new Pair<>(hero, graphics.getHeroImageView()));
        return elements;
    }

    public Passive getPassive() {
        return passive;
    }

    public Hero getHero() {
        return hero;
    }
}
