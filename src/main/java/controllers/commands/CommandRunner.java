package controllers.commands;


import java.io.*;
import java.util.*;
import controllers.game.*;
import gameObjects.Player.Player;
import graphics.*;
import graphics.directories.*;
import gameObjects.*;
import gameObjects.heros.*;
import gameObjects.cards.*;


public class CommandRunner {
    private final GameController controller;
    private final GraphicsController graphics;
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

    public CommandRunner(GameController controller, GraphicsController graphics) {
        this.controller = controller;
        this.graphics = graphics;
    }

    public boolean run(Command cmd) {
        CommandType commandType = cmd.getCommandType();
        Object[] input = cmd.getInput();

        boolean ret = false;
        if (CommandType.SIGN_UP.equals(commandType))
            ret = runSignup();
        else if (CommandType.LOGIN.equals(commandType))
            ret = runLogin();
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
        else if (CommandType.START_GAME.equals(commandType))
            ret = startGame();
        else if (CommandType.DECK_READER.equals(commandType))
            ret = deckReader();
        else if (CommandType.PLAY.equals(commandType) && input[0] instanceof Card card)
            ret = playCard(card);
        else if (CommandType.END_TURN.equals(commandType))
            ret = endTurn();
        else if (CommandType.HERO_POWER.equals(commandType))
            ret = heroPower();
        else if (CommandType.ATTACK.equals(commandType) && input[0] instanceof Targetable attacker && input[1] instanceof Targetable defender)
            ret = attack(attacker, defender);

        if (ret && controller.getCurrentPlayer() != null)
            controller.getCurrentPlayer().updateJson();
        return ret;
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

    private boolean runSignup() {
        StartPage startPage = graphics.getStartPage();
        String username = startPage.getUsername();
        if (username.length() < 4 || !validUsername(username))
            return false;

        try {
            GameController.readFile("src/main/resources/database/players/" + username + ".json");
            return false;
        } catch (FileNotFoundException e) {
            String password = startPage.getPassword();
            if (password.length() < 8)
                return false;
            return login(signUp(username, password), password);
        }
    }

    private boolean runLogin() {
        StartPage startPage = graphics.getStartPage();
        String username = startPage.getUsername();
        try {
            Player p = Player.getExistingPlayer(controller, username);
            String password = startPage.getPassword();
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
        try {
            (new File(p.getDeckJsonPath())).mkdir();
            (new File(p.getLogPath())).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.log("USER: " + username);
        p.log("PASSWORD: " + password);
        p.log("CREATED_AT: ", "");
        p.log("");
        p.log("signup", "");
        return p;
    }

    private boolean login(Player p, String password) {
        if (!p.loginAttempt(password))
            return false;
        controller.setCurrentPlayer(p);
        p.log("login", "");
        return true;
    }

    private boolean logout() {
        Player player = controller.getCurrentPlayer();
        if (player != null && player.getGame() != null)
            endGame();
        if (player != null)
            player.log("logout", "");
        controller.setCurrentPlayer(null);
        return true;
    }

    private boolean deletePlayer() {
        Player p = controller.getCurrentPlayer();
        logout();
        p.log("");
        p.log("DELETED_AT:", "");
        return (new File(p.getJsonPath())).delete() && p.deleteDeckDirectory();
    }

    private boolean selectDeck(Deck deck) {
        Player player = controller.getCurrentPlayer();
        if (!player.getInventory().getAllDecks().contains(deck))
            return false;
        player.setCurrentDeck(deck);
        player.log("select", "deck: " + deck);
        return true;
    }

    private boolean deselectDeck(Deck deck) {
        Player player = controller.getCurrentPlayer();
        if (player.getInventory().getCurrentDeck() != deck)
            return false;
        player.deselectCurrentDeck();
        player.log("deselect", "deck");
        return true;
    }

    private boolean addCard(Deck deck, Card card) {
        if (!deck.canAddCard(card))
            return false;
        deck.addCard(card);
        controller.getCurrentPlayer().log("add", "card: " + card + " -> deck: " + deck.getHeroClass().toString().toLowerCase() + "-" + deck);
        return true;
    }

    private boolean removeCard(Deck deck, Card card) {
        deck.removeCard(card);
        controller.getCurrentPlayer().log("remove", "card: " + card + " -> deck: " + deck.getHeroClass().toString().toLowerCase() + "-" + deck);
        return true;
    }

    private boolean addDeck(HeroClass heroClass, String name) {
        Player player = controller.getCurrentPlayer();
        boolean ret = validDeckName(name) && player.addNewDeck(heroClass, name);
        if (ret)
            player.log("add", "deck: " + name + " -> hero: " + heroClass);
        return ret;
    }

    private boolean removeDeck(Deck deck) {
        Player player = controller.getCurrentPlayer();
        boolean ret = player.getInventory().removeDeck(deck);
        if (ret)
            player.log("remove", "deck: " + deck);
        return ret;
    }

    private boolean moveDeck(Deck deck, HeroClass heroClass) {
        boolean ret = deck.move(heroClass);
        if (ret)
            controller.getCurrentPlayer().log("move", "deck:" + deck + " -> hero: " + heroClass);
        return ret;
    }

    private boolean renameDeck(Deck deck, String deckName) {
        boolean ret = validDeckName(deckName);
        deck.setName(deckName);
        if (ret)
            controller.getCurrentPlayer().log("rename", "deck: -> name: " + deckName);
        return ret;
    }

    private boolean buyCard(Card card) {
        Player player = controller.getCurrentPlayer();
        if (!player.canBuy(card))
            return false;
        player.getInventory().addCard(card);
        player.setBalance(player.getBalance() - card.getPrice());
        controller.getCurrentPlayer().log("buy", "card: " + card.toString());
        return true;
    }

    private boolean sellCard(Card card) {
        Player p = controller.getCurrentPlayer();
        if (!p.canSell(card))
            return false;
        p.getInventory().removeCard(card);
        p.setBalance(p.getBalance() + card.getPrice());
        controller.getCurrentPlayer().log("sell", "card: " + card.toString());
        return true;
    }

    private boolean startGame() {
        Player player = controller.getCurrentPlayer();
        if (player.getInventory().getCurrentDeck() == null)
            return false;
        Game game = new Game(controller);
        player.setGame(game);
        game.startGame();
        player.log("start_game", "game id: " + game.getId());
        try {
            (new File(game.getLogPath())).createNewFile();
            game.log("GAME_ID: " + game.getId());
            game.log("STARTED_AT: ", "");
            game.log("");
            game.log("p1: " + player);
            game.log("p1_hero: " + player.getInventory().getCurrentHero());
            game.log("p1_deck: " + player.getInventory().getCurrentDeck());
            game.log("");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        boolean ret = game.getCurrentPlayer().playCard(card);
        if (ret)
            game.log("p1:play_card", card.toString());
        return ret;
    }

    private boolean endTurn() {
        Game game = controller.getCurrentPlayer().getGame();
        game.nextTurn();
            game.log("p1:end_turn", "");
        return true;
    }

    private boolean heroPower() {
        Game game = controller.getCurrentPlayer().getGame();
        boolean ret =  game.getCurrentPlayer().useHeroPower();
        if (ret)
            game.log("p1:hero_power", "");
        return ret;
    }

    private boolean attack(Targetable attacker, Targetable defender) {
        Game game = controller.getCurrentPlayer().getGame();
        return game.getCurrentPlayer().attack(attacker, defender);
    }

    private boolean endGame() {
        Player player = controller.getCurrentPlayer();
        Game game = player.getGame();
        player.log("end_game", "game id: " + game.getId());
        game.log("");
        game.log("ENDED_AT: ", "");
        return true;
    }
}