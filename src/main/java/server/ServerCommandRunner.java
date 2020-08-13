package server;

import elements.*;
import elements.abilities.targets.*;
import elements.cards.*;
import elements.heros.*;
import commands.*;
import commands.types.*;
import shared.Methods;
import system.game.Game;
import system.player.*;

import java.io.*;
import java.util.*;

import static commands.types.ClientCommandType.*;
import static commands.types.ServerCommandType.*;

public class ServerCommandRunner extends CommandRunner<ServerCommandType> {
    protected final ServerController controller;
    protected final ClientHandler handler;

    public ServerCommandRunner(ServerController controller, ClientHandler handler) {
        this.controller = controller;
        this.handler = handler;
    }

    @Override
    public void run(Command<ServerCommandType> command) {
        ServerCommandType commandType = command.getCommandType();
        Object[] input = command.getInput();

        boolean ret = false;
        if (SIGN_UP.equals(commandType) && input[0] instanceof String username && input[1] instanceof String password)
            ret = runSignup(username, password);
        else if (LOGIN.equals(commandType) && input[0] instanceof String username && input[1] instanceof String password)
            ret = runLogin(username, password);
        else if (LOGOUT.equals(commandType) || EXIT.equals(commandType))
            ret = logout();

        if (handler.getCurrentPlayer() == null) {
            update(commandType, ret);
            return;
        }

        if (DELETE.equals(commandType))
            ret = deletePlayer();
        else if (SELECT.equals(commandType) && input[0] instanceof Deck deck)
            ret = selectDeck(deck);
        else if (DESELECT.equals(commandType) && input[0] instanceof Deck deck)
            ret = deselectDeck(deck);
        else if (ADD_DECK.equals(commandType) && input[0] instanceof HeroClass heroClass && input[1] instanceof String name)
            ret = addDeck(heroClass, name);
        else if (ADD_CARD.equals(commandType) && input[0] instanceof Deck deck && input[1] instanceof Card card)
            ret = addCard(deck, card);
        else if (REMOVE_DECK.equals(commandType) && input[0] instanceof Deck deck)
            ret = removeDeck(deck);
        else if (REMOVE_CARD.equals(commandType) && input[0] instanceof Deck deck && input[1] instanceof Card card)
            ret = removeCard(deck, card);
        else if (MOVE.equals(commandType) && input[0] instanceof Deck deck && input[1] instanceof HeroClass heroClass)
            ret = moveDeck(deck, heroClass);
        else if (RENAME.equals(commandType) && input[0] instanceof Deck deck && input[1] instanceof String deckName)
            ret = renameDeck(deck, deckName);
        else if (BUY.equals(commandType) && input[0] instanceof Card card)
            ret = buyCard(card);
        else if (SELL.equals(commandType) && input[0] instanceof Card card)
            ret = sellCard(card);
        else if (JOIN_GAME.equals(commandType) && input[0] instanceof Integer num)
            ret = joinGame(num);
        else if (DECK_READER.equals(commandType))
            ret = deckReader();

        if (handler.getGame() == null) {
            update(commandType, ret);
            return;
        }

        if (START_GAME.equals(commandType) && Methods.isArrayOfType(Card.class, input))
            ret = startGame(Methods.getListOfType(Card.class, input));
        else if (PLAY.equals(commandType) && input[0] instanceof Card card) {
            if (input.length < 2)
                ret = playCard(card);
            else if (input[1] instanceof Element target)
                ret = playCard(card, target);
        } else if (END_TURN.equals(commandType))
            ret = endTurn();
        else if (HERO_POWER.equals(commandType))
            ret = heroPower();
        else if (ATTACK.equals(commandType) && input[0] instanceof Attackable attacker && input[1] instanceof Attackable defender)
            ret = attack(attacker, defender);

        update(commandType, ret);
    }

    public void update(ServerCommandType commandType, boolean ret, boolean updateOpponent) {
        respond(commandType, ret);
        updatePlayer();
        updateGame();

        ClientHandler opponent = handler.getOpponent();
        if (opponent != null && updateOpponent)
            ((ServerCommandRunner) opponent.getRunner()).update(commandType, ret, false);
    }

    public void update(ServerCommandType commandType, boolean ret) {
        update(commandType, ret, true);
    }

    private void respond(ServerCommandType type, boolean ret) {
        handler.respond(new Command<>(RESULT, type, ret));
    }

    private void updatePlayer() {
        if (handler.getCurrentPlayer() != null) {
            handler.getCurrentPlayer().updateJson();
            handler.respond(new Command<>(UPDATE_PLAYER, handler.getCurrentPlayer().toString(), handler.getCurrentPlayer().getJson(false)));
        } else
            handler.respond(new Command<>(UPDATE_PLAYER, "null"));
    }

    private void updateGame() {
        Game game = handler.getGame();
        if (game == null) {
            handler.respond(new Command<>(UPDATE_GAME, "null"));
            return;
        }

        GameHandler gameHandler = handler.getGameHandler();
        handler.respond(new Command<>(UPDATE_GAME, game.getId(), gameHandler.getHeroClasses()[0], gameHandler.getHeroClasses()[1], gameHandler.getJsons()[0], gameHandler.getJsons()[1]));
    }

    private ArrayList<java.lang.Character> getUsernameChars() {
        ArrayList<java.lang.Character> usernameChars = new ArrayList<>();
        for (char c = 'a'; c <= 'z'; c++)
            usernameChars.add(c);
        for (char c = 'A'; c <= 'Z'; c++)
            usernameChars.add(c);
        for (char c = '0'; c <= '9'; c++)
            usernameChars.add(c);
        usernameChars.add('_');
        usernameChars.add('.');
        return usernameChars;
    }

    private boolean validUsername(String name) {
        for (int i = 0; i < name.length(); i++)
            if (!getUsernameChars().contains(name.charAt(i)))
                return false;
        return true;
    }

    private boolean validDeckName(String name) {
        for (int i = 0; i < name.length(); i++)
            if (!getUsernameChars().contains(name.charAt(i)) && name.charAt(i) != ' ')
                return false;
        return handler.getCurrentPlayer().getInventory().getDeck(name) == null;
    }

    private boolean runSignup(String username, String password) {
        if (username.length() < 4 || !validUsername(username))
            return false;

        try {
            ServerController.readFile("src/main/resources/database/players/" + username + ".json");
            return false;
        } catch (FileNotFoundException e) {
            if (password.length() < 8)
                return false;
            signUp(username, password);
            run(new Command<>(LOGIN, username, password));
            return true;
        }
    }

    private boolean runLogin(String username, String password) {
        if (handler.getCurrentPlayer() != null)
            return false;
        try {
            Player p = Player.getExistingPlayer(username);
            return login(p, password);
        } catch (IOException e) {
            return false;
        }
    }

    private Player signUp(String username, String password) {
        controller.setPlayerCount(controller.getPlayerCount() + 1);
        Player p = Player.getNewPlayer(username, password, controller.getPlayerCount());
        (new File(p.getDeckJsonPath())).mkdir();
        p.updateJson();
        p.getLogger().createFile();
        p.logSignup();
        return p;
    }

    private boolean login(Player p, String password) {
        if (!p.loginAttempt(password))
            return false;
        handler.setCurrentPlayer(p);
        p.getLogger().log("login", "");
        return true;
    }

    private boolean logout() {
        Player player = handler.getCurrentPlayer();
        if (player != null && player.getGame() != null)
            endGame();
        if (player != null)
            player.getLogger().log("logout", "");
        handler.setCurrentPlayer(null);
        return true;
    }

    private boolean deletePlayer() {
        Player p = handler.getCurrentPlayer();
        logout();
        p.getLogger().log("");
        p.getLogger().log("DELETED_AT:", "");
        return (new File(p.getJsonPath())).delete() && p.deleteDeckDirectory();
    }

    private boolean selectDeck(Deck deck) {
        Player player = handler.getCurrentPlayer();
        if (!player.getInventory().getAllDecks().contains(deck))
            return false;
        player.setCurrentDeck(deck);
        player.getLogger().log("select", "deck: " + deck);
        return true;
    }

    private boolean deselectDeck(Deck deck) {
        Player player = handler.getCurrentPlayer();
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
        handler.getCurrentPlayer().getLogger().log("add", "card: " + card + " -> deck: " + deck);
        return true;
    }

    private boolean removeCard(Deck deck, Card card) {
        deck.removeCard(card);
        handler.getCurrentPlayer().getLogger().log("remove", "card: " + card + " -> deck: " + deck);
        return true;
    }

    private boolean addDeck(HeroClass heroClass, String name) {
        Player player = handler.getCurrentPlayer();
        boolean ret = validDeckName(name) && player.addNewDeck(heroClass, name);
        if (ret)
            player.getLogger().log("add", "deck: " + name);
        return ret;
    }

    private boolean removeDeck(Deck deck) {
        Player player = handler.getCurrentPlayer();
        boolean ret = player.getInventory().removeDeck(deck);
        if (ret)
            player.getLogger().log("remove", "deck: " + deck);
        return ret;
    }

    private boolean moveDeck(Deck deck, HeroClass heroClass) {
        boolean ret = deck.move(heroClass);
        if (ret)
            handler.getCurrentPlayer().getLogger().log("move", "deck:" + deck + " -> hero class: " + heroClass);
        return ret;
    }

    private boolean renameDeck(Deck deck, String deckName) {
        String oldName = deck.toString();
        boolean ret = validDeckName(deckName);
        if (ret) {
            deck.setName(deckName);
            handler.getCurrentPlayer().getLogger().log("rename", "deck: " + oldName + " -> " + deckName);
        }
        return ret;
    }

    private boolean buyCard(Card card) {
        Player player = handler.getCurrentPlayer();
        if (!player.canBuy(card))
            return false;
        player.getInventory().addCard(card);
        player.setBalance(player.getBalance() - card.getPrice());
        handler.getCurrentPlayer().getLogger().log("buy", "card: " + card.toString());
        return true;
    }

    private boolean sellCard(Card card) {
        Player p = handler.getCurrentPlayer();
        if (!p.canSell(card))
            return false;
        p.getInventory().removeCard(card);
        p.setBalance(p.getBalance() + card.getPrice());
        handler.getCurrentPlayer().getLogger().log("sell", "card: " + card.toString());
        return true;
    }

    private boolean joinGame(int playerCount) {
        return handler.joinGame();
    }

    private boolean deckReader() {
        DeckPair deckPair = DeckPair.getInstance();
        if (deckPair == null)
            return false;
        return createGame(Game.getInstance(handler, deckPair, controller.getGameCount() + 1));
    }

    private boolean createGame(Game game) {
        controller.setGameCount(controller.getGameCount() + 1);
        handler.setGame(game);
        handler.getOpponent().setGame(game);
        return true;
    }

    private boolean startGame(ArrayList<Card> cards) {
        Player player = handler.getCurrentPlayer();
        if (player.getGame() == null)
            return false;
        return handler.startGame(cards);
    }

    private boolean playCard(Card card) {
        return playCard(card, null);
    }

    private boolean playCard(Card card, Element target) {
        Game game = handler.getGame();
        boolean ret = game.getCurrentCharacter().playCard(card, target);
        if (ret)
            game.log( "play_card", card.toString());
        return ret;
    }

    private boolean endTurn() {
        Game game = handler.getGame();
        game.nextTurn();
        game.log("end_turn", "");
        if (game.isFinished())
            return endGame();
        return true;
    }

    private boolean heroPower() {
        Game game = handler.getGame();
        boolean ret = game.getCurrentCharacter().useHeroPower();
        if (ret)
            game.log("hero_power", "");
        if (game.isFinished())
            return endGame();
        return ret;
    }

    private boolean attack(Attackable attacker, Attackable defender) {
        Game game = handler.getGame();
        boolean ret = game.getCurrentCharacter().attack(attacker, defender);
        if (ret)
            game.log("attack", attacker + " -> " + defender);
        if (game.isFinished())
            return endGame();
        return ret;
    }

    private boolean endGame() {
        Player player = handler.getCurrentPlayer();
        Game game = player.getGame();
        player.setGame(null);
        game.endGame();
        handler.respond(new Command<>(END_GAME));
        player.getLogger().log("end_game", "game id: " + game.getId());
        game.logEndGame();
        return true;
    }
}
