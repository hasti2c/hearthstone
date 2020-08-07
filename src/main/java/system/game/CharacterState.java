package system.game;

import com.google.gson.stream.*;
import elements.cards.*;
import elements.heros.*;
import system.*;

import java.io.*;
import java.util.*;

public class CharacterState implements Configable {
    private Deck deck;
    private ArrayList<Card> leftInDeck, hand = new ArrayList<>();
    private ArrayList<Minion> minionsInGame = new ArrayList<>();
    private Spell lastSpell;
    private Weapon currentWeapon;
    private ArrayList<QuestAndReward> questAndRewards = new ArrayList<>();
    private int mana = 1, heroPowerCount = 0;

    public CharacterState() {}

    public CharacterState(Deck deck) {
        this.deck = deck.cloneCards();
        leftInDeck = new ArrayList<>(this.deck.getCards());
    }

    public static CharacterState getInstance(String name, String json, Deck deck) {
        JsonReader jsonReader = new JsonReader(new StringReader(json));
        Configor<CharacterState> configor = new Configor<>(name, CharacterState.class, jsonReader);
        CharacterState state = configor.getConfigedObject();
        state.deck = deck.cloneCards();
        state.replaceCards();
        return state;
    }

    public void initialize() {}

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

    public String getJson() {
        StringWriter json = new StringWriter();
        updateJson(new JsonWriter(json));
        return json.toString();
    }

    private void updateJson(JsonWriter jsonWriter) {
        try {
            jsonWriter.beginObject();

            writeArray(jsonWriter, "leftInDeck", leftInDeck);
            writeArray(jsonWriter, "hand", hand);
            writeArray(jsonWriter, "minionsInGame", minionsInGame);
            if (lastSpell != null)
                jsonWriter.name("lastSpell").value(lastSpell.toString());
            if (currentWeapon != null)
                jsonWriter.name("currentWeapon").value(currentWeapon.toString());
            writeArray(jsonWriter, "questAndRewards", questAndRewards);
            jsonWriter.name("mana").value(mana);
            jsonWriter.name("heroPowerCount").value(heroPowerCount);

            jsonWriter.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeArray(JsonWriter jsonWriter, String name, ArrayList<? extends Card> cards) {
        try {
            jsonWriter.name(name).beginArray();
            for (Card card : cards)
                jsonWriter.value(card.toString());
            jsonWriter.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getJsonPath(String name) {
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
}