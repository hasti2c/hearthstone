package gameObjects;

import controllers.game.GameController;
import gameObjects.cards.*;
import gameObjects.heros.Deck;
import gameObjects.heros.Hero;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Game {
    private GameController controller;
    private Player player;
    private Hero hero;
    private Deck deck;
    private ArrayList<Card> leftInDeck, hand = new ArrayList<>(), minionsInGame = new ArrayList<>();
    private Weapon currentWeapon;
    private int id, turn = 1, playerCount = 1, playerNumber = 0, mana = 1;
    private boolean usedHeroPower;
    private FileWriter logWriter;
    private String gameEvents = "";
    private Passive passive;

    public Game(GameController controller, Deck deck) {
        this.controller = controller;
        this.deck = deck;
        this.hero = deck.getHero().clone();
        this.player = hero.getPlayer();
        leftInDeck = new ArrayList<>(deck.getCards());
    }

    public void startGame() {
        id = controller.getGameCount() + 1;
        try {
            logWriter = new FileWriter(getLogPath(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller.setGameCount(id);
        for (int i = 0; i < 3; i++)
            draw();
    }

    public Hero getHero() {
        return hero;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Card> getMinionsInGame() {
        return minionsInGame;
    }

    public ArrayList<Card> getHand() { return hand; }

    public ArrayList<Card> getLeftInDeck() { return leftInDeck; }

    private boolean draw() {
        if (leftInDeck.size() == 0)
            return false;

        ArrayList<Card> questAndReward = new ArrayList<>();
        for (Card c : leftInDeck)
            if (c instanceof gameObjects.cards.QuestAndReward)
                questAndReward.add(c);

        Card card;
        if (questAndReward.size() > 0)
            card = getRandomCard(questAndReward);
        else if (leftInDeck.size() > 0)
            card = getRandomCard(leftInDeck);
        else
            return false;

        leftInDeck.remove(card);
        if (hand.size() < 12)
            hand.add(card);
        return true;
    }

    private Card getRandomCard(ArrayList<Card> cards) {
        int n = cards.size(), i = (int) (Math.floor(n * Math.random())) % n;
        return cards.get(i);
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
        if (usedHeroPower || mana < hero.getHeroPower().getMana())
            return false;
        usedHeroPower = true;
        mana -= hero.getHeroPower().getMana();
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

    public void setPassive(Passive passive) {
        this.passive = passive;
    }

    public String getLogPath() {
        return "src/main/resources/logs/games/game-" + id + ".txt";
    }

    public void log(String line) {
        try {
            logWriter.write(line + "\n");
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String type, String details) {
        try {
            if (!"STARTED_AT: ".equals(type) && !"ENDED_AT: ".equals(details))
                gameEvents += type + " " + details + "\n";
            logWriter.write(type + " " + GameController.getTime() + " " + details + "\n");
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getGameEvents() {
        return gameEvents;
    }
}
