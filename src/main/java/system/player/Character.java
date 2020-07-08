package system.player;

import elements.Element;
import elements.Playable;
import elements.abilities.targets.Attackable;
import elements.cards.*;
import elements.heros.Deck;
import elements.heros.Hero;
import elements.heros.HeroPower;
import graphics.directories.playground.CharacterGraphics;
import graphics.directories.playground.GamePlayerGraphics;
import javafx.scene.Node;
import javafx.util.Pair;
import system.Game;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class Character {
    protected final Hero hero;
    protected final Deck deck;
    protected final ArrayList<Card> leftInDeck, hand = new ArrayList<>();
    protected final ArrayList<Minion> minionsInGame = new ArrayList<>();
    protected Spell lastSpell;
    protected Weapon currentWeapon;
    protected Passive passive;
    protected ArrayList<QuestAndReward> questAndRewards = new ArrayList<>();
    private final int playerNumber;
    protected int mana = 1, drawCap = 1, heroPowerCap = 1, heroPowerCount = 0;
    protected boolean randomDraw = true;
    private final PlayerFaction playerFaction;
    protected final Game game;
    protected CharacterGraphics<? extends Character> graphics;

    public Character(Hero hero, Deck deck, Game game, PlayerFaction playerFaction) {
        this.hero = hero;
        this.deck = deck;
        leftInDeck = deck.getCardClones();
        this.game = game;
        this.playerFaction = playerFaction;
        playerNumber = playerFaction.getPlayerNumber();
    }

    public void initialize() {
        initializeHelper();
        if (passive != null) {
            mana = 1 + passive.getTurnManaPromotion(1);
            drawCap = passive.getDrawCap();
            heroPowerCap = passive.getHeroPowerCap();
        }

        for (int i = 0; i < 3; i++)
            draw();
    }

    public void initialize(ArrayList<Card> cards) {
        initializeHelper();
        if (passive != null) {
            mana = 1 + passive.getTurnManaPromotion(1);
            drawCap = passive.getDrawCap();
            heroPowerCap = passive.getHeroPowerCap();
        }

        for (int i = 0; i < 3; i++)
            draw(cards.get(i));
    }

    protected abstract void initializeHelper();

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

        startTurnHelper();
    }

    protected abstract void startTurnHelper();

    public void endTurn() {
        doCardAction("doActionOnEndTurn");
        if (passive != null)
            passive.doEndTurnAction(minionsInGame);
        for (Minion minion : getMinionsInGame())
            minion.setAsleep(false);
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

    public void setMana(Element element, int mana) {
        int manaUse = this.mana - mana;
        this.mana = mana;
        if (manaUse > 0)
            checkQuests(element, manaUse);
    }

    private void checkQuests(Element element, int mana) {
        for (QuestAndReward quest : questAndRewards) {
            quest.addManaUse(element, mana);
            if (quest.isDone())
                doCardAction("doActionOnQuest", quest);
        }
        questAndRewards.removeIf(QuestAndReward::isDone);
    }

    public void setPassive(Passive passive) {
        this.passive = passive;
    }

    private boolean draw(Card card) {
        leftInDeck.remove(card);
        if (hand.size() < 12) {
            hand.add(card);
            doCardAction("doActionOnDraw");
        }
        return true;
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

        return draw(card);
    }

    private Card getNextCard(ArrayList<Card> cards) {
        if (randomDraw)
            return Element.getRandomElement(cards);
        return leftInDeck.get(0);
    }

    public boolean playCard(Card card) {
        if (!isMyTurn() || !hand.contains(card) || mana < card.getGameMana(this) || (card instanceof Minion && minionsInGame.size() >= 7))
            return false;

        setMana(card, mana - card.getGameMana(this));
        hand.remove(card);
        if (card instanceof Minion minion) {
            hero.getHeroClass().doHeroAction(minion);
            minionsInGame.add(minion);
            minion.setAsleep(true);
        } else if (card instanceof Weapon weapon)
            setCurrentWeapon(weapon);
        else if (card instanceof Spell spell)
            lastSpell = spell;
        else if (card instanceof QuestAndReward questAndReward)
            questAndRewards.add(questAndReward);
        playCardHelper(card);
        getGraphics().getPlayGround().config();
        doCardAction("doActionOnPlay", card);
        return true;
    }

    protected abstract void playCardHelper(Card card);

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
        ret |= lastSpell == element || currentWeapon == element || questAndRewards.contains(element);
        ret |= hero.getHeroPower() == element;
        ret |= hero == element;
        return ret;
    }

    public Character getOpponent() {
        if (game.getCurrentCharacter() == this)
            return game.getOtherCharacter();
        return game.getCurrentCharacter();
    }

    public boolean canAttack(Attackable attacker) {
        boolean ret = this == game.getCurrentCharacter() && !attacker.getHasAttacked();
        if (attacker instanceof Hero)
            return ret && currentWeapon != null;
        Minion minion = (Minion) attacker;
        ret &= minionsInGame.contains(minion) && (!minion.getAsleep() || minion.getRush());
        return ret;
    }

    public boolean canBeAttacked(Attackable attacker, Attackable target) {
        boolean ret = this != game.getCurrentCharacter();
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

    protected void doCardAction(String actionName) {
        try {
            Method method = Playable.class.getDeclaredMethod(actionName, Character.class);
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
            Method method = Playable.class.getDeclaredMethod(actionName, Character.class, Card.class);

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

    protected ArrayList<Playable> getPlayables() {
        ArrayList<Playable> ret = new ArrayList<>(minionsInGame);
        if (lastSpell != null)
            ret.add(lastSpell);
        if (currentWeapon != null)
            ret.add(currentWeapon);
        ret.addAll(questAndRewards);
        ret.add(hero.getHeroPower());
        return ret;
    }

    public Passive getPassive() {
        return passive;
    }

    public Hero getHero() {
        return hero;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setGraphics(CharacterGraphics<? extends Character> graphics) {
        this.graphics = graphics;
    }

    public CharacterGraphics<? extends Character> getGraphics() {
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

    public abstract void addWin();
}
