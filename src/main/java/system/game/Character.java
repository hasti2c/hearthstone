package system.game;

import elements.*;
import elements.abilities.targets.*;
import elements.cards.*;
import elements.heros.*;
import system.player.*;

import java.lang.reflect.*;
import java.util.*;

public abstract class Character {
    protected final Hero hero;
    protected final Deck deck;
    private final int playerNumber;
    protected Passive passive;
    protected int drawCap = 1, heroPowerCap = 1;
    protected boolean randomDraw = true;
    private final PlayerFaction playerFaction;
    protected Game game;
    protected CharacterState state;

    public Character(Hero hero, Deck deck, PlayerFaction playerFaction) {
        this.hero = hero.clone();
        this.deck = deck;
        state = new CharacterState(deck);
        this.playerFaction = playerFaction;
        playerNumber = playerFaction.getPlayerNumber();
    }

    public void initialize() {
        initializeHelper();
        if (passive != null)
            initPassive();

        for (int i = 0; i < 3; i++)
            draw();
    }

    public void initialize(ArrayList<Card> cards) {
        initializeHelper();
        if (passive != null)
            initPassive();

        for (int i = 0; i < 3; i++)
            draw(cards.get(i));
    }

    private void initPassive() {
        state.setMana(1 + passive.getTurnManaPromotion(1));
        drawCap = passive.getDrawCap();
        heroPowerCap = passive.getHeroPowerCap();
    }

    protected abstract void initializeHelper();

    public void startTurn() {
        if (getMyTurnNumber() != 1)
            for (int i = 0; i < drawCap; i++)
                draw();
        state.resetHeroPowerCount();

        for (Minion minion : state.getMinionsInGame())
            minion.setHasAttacked(false);
        hero.setHasAttacked(false);

        state.setMana(Math.min(getMyTurnNumber(), 10));
        if (passive != null)
            state.setMana(passive.getTurnManaPromotion(getMyTurnNumber()));

        startTurnHelper();
    }

    protected abstract void startTurnHelper();

    public void endTurn() {
        doCardAction("doActionOnEndTurn");
        if (passive != null)
            passive.doEndTurnAction(state.getMinionsInGame());
        for (Minion minion : state.getMinionsInGame())
            minion.setAsleep(false);
    }

    public PlayerFaction getPlayerFaction() {
        return playerFaction;
    }

    public void setMana(Element element, int mana) {
        int manaUse = state.getMana() - mana;
        state.setMana(mana);
        if (manaUse > 0)
            checkQuests(element, manaUse);
    }

    private void checkQuests(Element element, int mana) {
        for (QuestAndReward quest : state.getQuestAndRewards()) {
            quest.addManaUse(element, mana);
            if (quest.isDone())
                doCardAction("doActionOnQuest", quest);
        }
        state.getQuestAndRewards().removeIf(QuestAndReward::isDone);
    }

    public void setPassive(Passive passive) {
        this.passive = passive;
    }

    private boolean draw(Card card) {
        state.getLeftInDeck().remove(card);
        if (state.getHand().size() < 12) {
            state.getHand().add(card);
            doCardAction("doActionOnDraw");
        }
        return true;
    }

    public boolean draw() {
        ArrayList<Card> leftInDeck = state.getLeftInDeck();
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
        return state.getLeftInDeck().get(0);
    }

    public boolean playCard(Card card, Element target) {
        ArrayList<Minion> minionsInGame = state.getMinionsInGame();
        ArrayList<Card> hand = state.getHand();
        int mana = state.getMana();
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
            state.setLastSpell(spell);
        else if (card instanceof QuestAndReward questAndReward)
            state.getQuestAndRewards().add(questAndReward);
        playCardHelper(card);
        if (target != null)
            doCardAction("doActionOnPlay", card, target);
        else
            doCardAction("doActionOnPlay", card);
        return true;
    }

    public boolean playCard(Card card) {
        return playCard(card, null);
    }

    protected abstract void playCardHelper(Card card);

    public void setCurrentWeapon(Weapon weapon) {
        state.setCurrentWeapon(weapon);
        if (weapon != null)
            hero.getHeroPower().promote();
        else
            hero.getHeroPower().demote();
        hero.setHasAttacked(false);
    }

    public boolean useHeroPower(Element target) {
        HeroPower heroPower = hero.getHeroPower();
        if (!isMyTurn() || state.getHeroPowerCount() >= heroPowerCap || state.getMana() < heroPower.getGameMana(this))
            return false;
        state.incrementHeroPowerCount();
        heroPower.reduceCost(this);
        doCardAction("doActionOnHeroPower", target);
        return true;
    }

    public boolean useHeroPower() {
        return useHeroPower(null);
    }

    public boolean canUseHeroPower() {
        return state.getHeroPowerCount() < heroPowerCap && !hero.getHeroPower().isPassive();
    }

    public int getMyTurnNumber() {
        return (int) Math.floor((double) game.getTurn() / (double) game.getPlayerCount()) + 1;
    }

    private boolean isMyTurn() {
        return game.getTurn() % game.getPlayerCount() == playerNumber;
    }

    public boolean owns(Element element) {
        boolean ret = state.getMinionsInGame().contains(element) || state.getHand().contains(element) || state.getLeftInDeck().contains(element);
        ret |= state.getLastSpell() == element || state.getCurrentWeapon() == element || state.getQuestAndRewards().contains(element);
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
            return ret && state.getCurrentWeapon() != null;
        Minion minion = (Minion) attacker;
        ret &= state.getMinionsInGame().contains(minion) && (!minion.getAsleep() || minion.getRush());
        return ret;
    }

    public boolean canBeAttacked(Attackable attacker, Attackable target) {
        boolean ret = this != game.getCurrentCharacter();
        if (target instanceof Minion minion) {
            ret &= state.getMinionsInGame().contains(minion);
            ret &= !hasAnyTaunt() || minion.getTaunt();
        } else {
            ret &= !hasAnyTaunt();
            if (attacker instanceof Minion minion)
                ret &= !minion.getAsleep();
        }
        return ret;
    }

    private boolean hasAnyTaunt() {
        for (Minion minion : state.getMinionsInGame())
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
        Weapon weapon = state.getCurrentWeapon();
        if (attacker instanceof Hero && weapon != null)
            weapon.setDurability(weapon.getDurability() - 1);
    }

    public void clearDeadMinions() {
        ArrayList<Minion> minionsInGame = state.getMinionsInGame();
        for (int i = 0; i < minionsInGame.size(); i++)
            if (!minionsInGame.get(i).isValid())
                minionsInGame.remove(i--);
    }

    public void doCardAction(String actionName, Element... input) {
        Method method = getCardMethod(actionName, input.length);
        for (Playable playable : getPlayables())
            invokeCardMethod(method, playable, input);
        for (Playable playable : getOpponent().getPlayables())
            invokeCardMethod(method, playable, input);

        clearDeadMinions();
        getOpponent().clearDeadMinions();
    }

    private Method getCardMethod(String actionName, int length) {
        try {
            return switch (length) {
                default -> Playable.class.getDeclaredMethod(actionName, Character.class);
                case 1 -> Playable.class.getDeclaredMethod(actionName, Character.class, Element.class);
                case 2 -> Playable.class.getDeclaredMethod(actionName, Character.class, Element.class, Element.class);
            };
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void invokeCardMethod(Method method, Playable playable, Element... input) {
        try {
            switch (input.length) {
                default -> method.invoke(playable, this);
                case 1 -> method.invoke(playable, this, input[0]);
                case 2 -> method.invoke(playable, this, input[0], input[1]);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    protected ArrayList<Playable> getPlayables() {
        ArrayList<Playable> ret = new ArrayList<>(state.getMinionsInGame());
        if (state.getLastSpell() != null)
            ret.add(state.getLastSpell());
        if (state.getCurrentWeapon() != null)
            ret.add(state.getCurrentWeapon());
        ret.addAll(state.getQuestAndRewards());
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

    public abstract void addWin();

    public ArrayList<Element> getElements() {
        ArrayList<Element> ret = new ArrayList<>(state.getMinionsInGame());
        ret.addAll(state.getHand());
        ret.addAll(state.getLeftInDeck());
        if (state.getLastSpell() != null)
            ret.add(state.getLastSpell());
        if (state.getCurrentWeapon() != null)
            ret.add(state.getCurrentWeapon());
        ret.add(hero);
        ret.add(hero.getHeroPower());
        return ret;
    }

    public CharacterState getState() {
        return state;
    }

    public void updateState(String json) {
        state = CharacterState.getInstance("game-" + game.getId() + "-state-" + playerNumber, json, deck);
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
