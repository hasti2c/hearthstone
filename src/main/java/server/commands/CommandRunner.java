package server.commands;

import java.io.*;
import java.util.*;

import client.Client;
import server.Controller;
import elements.abilities.targets.Attackable;
import system.Game;
import system.player.Player;
import client.graphics.GraphicsController;
import elements.heros.*;
import elements.cards.*;

public class CommandRunner {
    private final Controller controller;
    private final Client client;
    private static final ArrayList<Character> usernameChars = new ArrayList<>();

    static {
        for (char c = 'a'; c <= 'z'; c++)
            usernameChars.add(c);
        for (char c = 'A'; c <= 'Z'; c++)
            usernameChars.add(c);
        for (char c = '0'; c <= '9'; c++)
            usernameChars.add(c);
        usernameChars.add('_');
        usernameChars.add('.');
    }

    public CommandRunner(Controller controller, Client client) {
        this.controller = controller;
        this.client = client;
    }

    public boolean run(Command command) {
        CommandType commandType = command.getCommandType();
        Object[] input = command.getInput();

        boolean ret = false;
        if (CommandType.SIGN_UP.equals(commandType) && input[0] instanceof String username && input[1] instanceof String password)
            ret = runSignup(username, password);
        else if (CommandType.LOGIN.equals(commandType) && input[0] instanceof String username && input[1] instanceof String password)
            ret = runLogin(username, password);
        else if (CommandType.EXIT.equals(commandType))
            ret = logout();

        if (controller.getCurrentPlayer() == null)
            return ret;

        if (CommandType.DELETE.equals(commandType))
            ret = runDeletePlayer();
        else if (CommandType.SELECT.equals(commandType) && input[0] instanceof Deck deck)
            ret = selectDeck(deck);
        else if (CommandType.DESELECT.equals(commandType) && input[0] instanceof Deck deck)
            ret = deselectDeck(deck);
        else if (CommandType.ADD_DECK.equals(commandType) && input[0] instanceof HeroClass heroClass && input[1] instanceof String name)
            ret = addDeck(heroClass, name);
        else if (CommandType.ADD_CARD.equals(commandType) && input[0] instanceof Deck deck && input[1] instanceof Card card)
            ret = addCard(deck, card);
        else if (CommandType.REMOVE_DECK.equals(commandType) && input[0] instanceof Deck deck)
            ret = removeDeck(deck);
        else if (CommandType.REMOVE_CARD.equals(commandType) && input[0] instanceof Deck deck && input[1] instanceof Card card)
            ret = removeCard(deck, card);
        else if (CommandType.MOVE.equals(commandType) && input[0] instanceof Deck deck && input[1] instanceof HeroClass heroClass)
            ret = moveDeck(deck, heroClass);
        else if (CommandType.RENAME.equals(commandType) && input[0] instanceof Deck deck && input[1] instanceof String deckName)
            ret = renameDeck(deck, deckName);
        else if (CommandType.BUY.equals(commandType) && input[0] instanceof Card card)
            ret = buyCard(card);
        else if (CommandType.SELL.equals(commandType) && input[0] instanceof Card card)
            ret = sellCard(card);
        else if (CommandType.CREATE_GAME.equals(commandType) && input[0] instanceof Integer num)
            ret = createGame(num);
        else if (CommandType.START_GAME.equals(commandType) && isCards(input))
            ret = startGame(new ArrayList<>(Arrays.asList((Card[]) input)));
        else if (CommandType.DECK_READER.equals(commandType))
            ret = deckReader();
        else if (CommandType.PLAY.equals(commandType) && input[0] instanceof Card card)
            ret = playCard(card);
        else if (CommandType.END_TURN.equals(commandType))
            ret = endTurn();
        else if (CommandType.HERO_POWER.equals(commandType))
            ret = heroPower();
        else if (CommandType.ATTACK.equals(commandType) && input[0] instanceof Attackable attacker && input[1] instanceof Attackable defender)
            ret = attack(attacker, defender);

        if (ret && controller.getCurrentPlayer() != null)
            controller.getCurrentPlayer().updateJson();
        return ret;
    }

    private boolean isCards(Object[] input) {
        for (Object object : input)
            if (!(object instanceof Card))
                return false;
        return true;
    }

    private boolean validUsername(String name) {
        for (int i = 0; i < name.length(); i++)
            if (!usernameChars.contains(name.charAt(i)))
                return false;
        return true;
    }

    private boolean validDeckName(String name) {
        for (int i = 0; i < name.length(); i++)
            if (!usernameChars.contains(name.charAt(i)) && name.charAt(i) != ' ')
                return false;
        for (Deck deck : controller.getCurrentPlayer().getInventory().getAllDecks())
            if (deck.toString().equals(name))
                return false;
        return true;
    }

    private boolean runSignup(String username, String password) {
        if (username.length() < 4 || !validUsername(username))
            return false;

        try {
            Controller.readFile("src/main/resources/database/players/" + username + ".json");
            return false;
        } catch (FileNotFoundException e) {
            if (password.length() < 8)
                return false;
            return login(signUp(username, password), password);
        }
    }

    private boolean runLogin(String username, String password) {
        try {
            Player p = Player.getExistingPlayer(controller, username);
            return login(p, password);
        } catch (IOException e) {
            return false;
        }
    }

    private boolean runDeletePlayer() {
        /*String password = console.getPassword("Password: ");
        if (!controller.getCurrentPlayer().loginAttempt(password))
            return false;
        String sure = console.getInput("Are you sure you want to delete " + controller.getCurrentPlayer().toString() + "? (y/n) ");
        if (sure.equals("n"))
            return true;
        if (!sure.equals("y"))
            return false;*/
        return deletePlayer();
    }

    private Player signUp(String username, String password) {
        controller.setPlayerCount(controller.getPlayerCount() + 1);
        Player p = Player.getNewPlayer(controller, username, password);
        (new File(p.getDeckJsonPath())).mkdir();
        p.getLogger().createFile();
        p.logSignup();
        return p;
    }

    private boolean login(Player p, String password) {
        if (!p.loginAttempt(password))
            return false;
        controller.setCurrentPlayer(p);
        p.getLogger().log("login", "");
        return true;
    }

    private boolean logout() {
        Player player = controller.getCurrentPlayer();
        if (player != null && player.getGame() != null)
            endGame();
        if (player != null)
            player.getLogger().log("logout", "");
        controller.setCurrentPlayer(null);
        return true;
    }

    private boolean deletePlayer() {
        Player p = controller.getCurrentPlayer();
        logout();
        p.getLogger().log("");
        p.getLogger().log("DELETED_AT:", "");
        return (new File(p.getJsonPath())).delete() && p.deleteDeckDirectory();
    }

    private boolean selectDeck(Deck deck) {
        Player player = controller.getCurrentPlayer();
        if (!player.getInventory().getAllDecks().contains(deck))
            return false;
        player.setCurrentDeck(deck);
        player.getLogger().log("select", "deck: " + deck);
        return true;
    }

    private boolean deselectDeck(Deck deck) {
        Player player = controller.getCurrentPlayer();
        if (player.getInventory().getCurrentDeck() != deck)
            return false;
        player.deselectCurrentDeck();
        player.getLogger().log("deselect", "deck");
        return true;
    }

    private boolean addCard(Deck deck, Card card) {
        if (!deck.canAddCard(card))
            return false;
        deck.addCard(card);
        controller.getCurrentPlayer().getLogger().log("add", "card: " + card + " -> deck: " + deck);
        return true;
    }

    private boolean removeCard(Deck deck, Card card) {
        deck.removeCard(card);
        controller.getCurrentPlayer().getLogger().log("remove", "card: " + card + " -> deck: " + deck);
        return true;
    }

    private boolean addDeck(HeroClass heroClass, String name) {
        Player player = controller.getCurrentPlayer();
        boolean ret = validDeckName(name) && player.addNewDeck(heroClass, name);
        if (ret)
            player.getLogger().log("add", "deck: " + name);
        return ret;
    }

    private boolean removeDeck(Deck deck) {
        Player player = controller.getCurrentPlayer();
        boolean ret = player.getInventory().removeDeck(deck);
        if (ret)
            player.getLogger().log("remove", "deck: " + deck);
        return ret;
    }

    private boolean moveDeck(Deck deck, HeroClass heroClass) {
        boolean ret = deck.move(heroClass);
        if (ret)
            controller.getCurrentPlayer().getLogger().log("move", "deck:" + deck + " -> hero class: " + heroClass);
        return ret;
    }

    private boolean renameDeck(Deck deck, String deckName) {
        String oldName = deck.toString();
        boolean ret = validDeckName(deckName);
        deck.setName(deckName);
        if (ret)
            controller.getCurrentPlayer().getLogger().log("rename", "deck: " + oldName + " -> " + deckName);
        return ret;
    }

    private boolean buyCard(Card card) {
        Player player = controller.getCurrentPlayer();
        if (!player.canBuy(card))
            return false;
        player.getInventory().addCard(card);
        player.setBalance(player.getBalance() - card.getPrice());
        controller.getCurrentPlayer().getLogger().log("buy", "card: " + card.toString());
        return true;
    }

    private boolean sellCard(Card card) {
        Player p = controller.getCurrentPlayer();
        if (!p.canSell(card))
            return false;
        p.getInventory().removeCard(card);
        p.setBalance(p.getBalance() + card.getPrice());
        controller.getCurrentPlayer().getLogger().log("sell", "card: " + card.toString());
        return true;
    }

    private boolean createGame(int playerCount) {
        Player player = controller.getCurrentPlayer();
        if (player.getInventory().getCurrentDeck() == null)
            return false;
        Game game = new Game(controller, playerCount);
        player.setGame(game);
        return true;
    }

    private boolean startGame(ArrayList<Card> cards) {
        Player player = controller.getCurrentPlayer();
        if (player.getGame() == null)
            return false;
        Game game = player.getGame();
        game.startGame(cards);
        player.getLogger().log("start_game", "game id: " + game.getId());
        game.logStartGame();
        return true;
    }

    private boolean deckReader() {
        DeckPair deckPair = DeckPair.getInstance(controller);
        if (deckPair == null)
            return false;
        Game game = new Game(controller, deckPair);
        controller.getCurrentPlayer().setGame(game);
        game.startGame();
        return true;
    }

    private boolean playCard(Card card) {
        Game game = controller.getCurrentPlayer().getGame();
        boolean ret = game.getCurrentCharacter().playCard(card);
        if (ret)
            game.log( "play_card", card.toString());
        return ret;
    }

    private boolean endTurn() {
        Game game = controller.getCurrentPlayer().getGame();
        game.nextTurn();
        game.log("end_turn", "");
        if (game.isFinished())
            return endGame();
        return true;
    }

    private boolean heroPower() {
        Game game = controller.getCurrentPlayer().getGame();
        boolean ret =  game.getCurrentCharacter().useHeroPower();
        if (ret)
            game.log("hero_power", "");
        if (game.isFinished())
            return endGame();
        return ret;
    }

    private boolean attack(Attackable attacker, Attackable defender) {
        Game game = controller.getCurrentPlayer().getGame();
        boolean ret = game.getCurrentCharacter().attack(attacker, defender);
        if (ret)
            game.log("attack", attacker + " -> " + defender);
        if (game.isFinished())
            return endGame();
        return ret;
    }

    private boolean endGame() {
        Player player = controller.getCurrentPlayer();
        Game game = player.getGame();
        player.setGame(null);
        game.endGame();
        client.endGame();
        player.getLogger().log("end_game", "game id: " + game.getId());
        game.logEndGame();
        return true;
    }
}