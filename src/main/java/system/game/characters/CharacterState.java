package system.game.characters;

import com.google.gson.stream.*;
import elements.Element;
import elements.cards.*;
import elements.heros.*;
import shared.GameData;
import shared.Methods;
import system.configor.*;
import system.updater.*;

import java.io.*;
import java.util.*;

public class CharacterState extends Updatable {
    private Deck deck;
    private ArrayList<Card> leftInDeck, hand = new ArrayList<>();
    private ArrayList<Minion> minionsInGame = new ArrayList<>();
    private ArrayList<Boolean> asleep = new ArrayList<>(), taunt = new ArrayList<>(), rush = new ArrayList<>(), charge = new ArrayList<>(), divineShield = new ArrayList<>();
    private Spell lastSpell;
    private Weapon currentWeapon;
    private ArrayList<QuestAndReward> questAndRewards = new ArrayList<>();
    private int mana = 1, heroPowerCount = 0;

    private CharacterState() {}

    public CharacterState(Deck deck) {
        this.deck = deck;
        leftInDeck = new ArrayList<>(this.deck.getCards());
    }

    public static CharacterState getInstance(String name, String json) {
        JsonReader jsonReader = new JsonReader(new StringReader(json));
        Configor<CharacterState> configor = new Configor<>(name, CharacterState.class, jsonReader, false);
        CharacterState state = configor.getConfigedObject();
        state.deck = state.deck.cloneCards();
        state.replaceCards();
        System.out.println("awerhaioewraejwil" + json);
        state.minionProperties();
        return state;
    }

    @Override
    public void initialize(String initPlayerName) {}

    @Override
    public String getName() {
        return "";
    }

    private void minionProperties() {
        for (int i = 0; i < minionsInGame.size(); i++) {
            Minion minion = minionsInGame.get(i);
            minion.setAsleep(asleep.get(i));
            minion.setTaunt(taunt.get(i));
            minion.setRush(rush.get(i));
            minion.setCharge(charge.get(i));
            minion.setDivineShield(divineShield.get(i));
        }
    }

    private void replaceCards() {
        initCardList(leftInDeck);
        initCardList(hand);
        initCardList(minionsInGame);
        if (lastSpell != null)
            lastSpell = initCard(lastSpell);
        if (currentWeapon != null)
            currentWeapon = initCard(currentWeapon);
        initCardList(questAndRewards);
    }

    private <C extends Card> C initCard(C card) {
        return initCard(card, 1);
    }

    private <C extends Card> C initCard(C card, int num) {
        return deck.getCard(card, num);
    }

    private <C extends Card> void initCardList(ArrayList<C> cards) {
        for (int i = 0; i < cards.size(); i++) {
            C card = cards.get(i);
            cards.remove(i);
            int j = 1;
            C clone = initCard(card);
            while (cards.contains(clone) && clone != null)
                clone = initCard(card, ++j);

            if (clone != null)
                cards.add(i, clone);
            else
                i--;
        }
    }

    @Override
    public String getJsonPath(String name, String initPlayerName) {
        return null;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getHeroPowerCount() {
        return heroPowerCount;
    }

    public void resetHeroPowerCount() {
        heroPowerCount = 0;
    }

    public void incrementHeroPowerCount() {
        heroPowerCount++;
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

    public Spell getLastSpell() {
        return lastSpell;
    }

    public void setLastSpell(Spell lastSpell) {
        this.lastSpell = lastSpell;
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public void setCurrentWeapon(Weapon currentWeapon) {
        this.currentWeapon = currentWeapon;
    }

    public ArrayList<QuestAndReward> getQuestAndRewards() {
        return questAndRewards;
    }

    public Deck getDeck() {
        return deck;
    }

    protected CharacterState clone() {
        CharacterState state = new CharacterState();
        state.deck = deck.cloneCards();
        state.leftInDeck = new ArrayList<>(leftInDeck);
        state.hand = new ArrayList<>(hand);
        state.minionsInGame = new ArrayList<>(minionsInGame);
        if (lastSpell != null)
            state.lastSpell = (Spell) lastSpell.clone();
        if (currentWeapon != null)
            state.currentWeapon = (Weapon) currentWeapon.clone();
        state.questAndRewards = new ArrayList<>(questAndRewards);
        state.mana = mana;
        state.heroPowerCount = heroPowerCount;
        return state;
    }

    public String getHiddenJson(boolean compact) {
        CharacterState state = clone();
        state.randomize(state.deck.getCards(), Card.class, GameData.getInstance().getCardsList());
        state.randomize(state.leftInDeck, Card.class, state.deck.getCards());
        state.randomize(state.hand, Card.class, state.deck.getCards());
        state.randomize(state.questAndRewards, QuestAndReward.class, state.deck.getCards());
        state.lastSpell = Element.getRandomElement(Methods.getCards(Spell.class, state.deck.getCards()));
        return state.getJson(compact);
    }

    private <C extends Card> void randomize(ArrayList<C> cards, Class<C> cardClass, ArrayList<? extends Card> allCards) {
        int n = cards.size();
        cards.clear();
        ArrayList<C> correctCards = Methods.getCards(cardClass, allCards);
        for (int i = 0; i < n; i++)
            cards.add(Element.getRandomElement(correctCards));
    }

    @Override
    public String getJson(boolean compact) {
        asleep.clear();
        taunt.clear();
        rush.clear();
        charge.clear();
        divineShield.clear();
        for (Minion minion : minionsInGame) {
            asleep.add(minion.getAsleep());
            taunt.add(minion.getTaunt());
            rush.add(minion.getRush());
            charge.add(minion.getCharge());
            divineShield.add(minion.getDivineShield());
        }
        return super.getJson(compact);
    }
}