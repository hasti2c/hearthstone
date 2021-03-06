package server;

import elements.*;
import elements.abilities.targets.*;
import elements.cards.*;
import elements.heros.*;
import commands.*;
import commands.types.*;
import shared.*;
import system.game.*;
import system.player.*;

import java.io.*;
import java.util.*;

import static commands.types.ClientCommandType.*;
import static commands.types.ServerCommandType.*;
import static system.game.GameType.*;

public class ServerCommandRunner extends CommandRunner<ServerCommandType> {
    protected final ServerController controller;
    protected final ClientHandler handler;

    public ServerCommandRunner(ServerController controller, ClientHandler handler) {
        this.controller = controller;
        this.handler = handler;
    }

    @Override
    public void run(Command<ServerCommandType> command) {
        System.out.println(command);
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

        if (DELETE.equals(commandType) && input[0] instanceof String password)
            ret = deletePlayer(password);
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
        else if (JOIN_GAME.equals(commandType) && input[0] instanceof GameType gameType)
            ret = joinGame(gameType);

        if (handler.getGame() == null) {
            update(commandType, ret);
            return;
        }

        if (START_GAME.equals(commandType) && Methods.isArrayOfType(Card.class, input))
            ret = startGame(Methods.getListOfType(Card.class, input));
        else if (LEAVE_GAME.equals(commandType))
            ret = leaveGame();

        if (!handler.isMyTurn()) {
            update(commandType, ret);
            return;
        }

        if (PLAY.equals(commandType) && input[0] instanceof Card card) {
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
        } else if (game.isFinished()) {
            endGame(false);
            return;
        }

        GameType type = game.getType();
        int id = game.getId();

        if (type.isMultiPlayer()) {
            GameHandler gameHandler = handler.getGameHandler();
            int index = gameHandler.indexOf(handler);
            HeroClass[] heroClasses = gameHandler.getHeroClasses();
            String[] jsons = game.getJsons(index);
            handler.respond(new Command<>(UPDATE_GAME, type, id, heroClasses[0], heroClasses[1], jsons[0], jsons[1], index));
        } else {
            HeroClass[] heroClasses = new HeroClass[]{game.getCharacters()[0].getHero().getHeroClass(), game.getCharacters()[1].getHero().getHeroClass()};
            String[] jsons = game.getJsons();
            if (type == OFFLINE_MULTIPLAYER)
                handler.respond(new Command<>(UPDATE_GAME, type, id, heroClasses[0], heroClasses[1], jsons[0], jsons[1]));
            else
                handler.respond(new Command<>(UPDATE_GAME, type, id, heroClasses[0], heroClasses[1], jsons[0], jsons[1], 0));
        }
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
            endGame(true);
        if (player != null)
            player.getLogger().log("logout", "");
        handler.setCurrentPlayer(null);
        return true;
    }

    private boolean deletePlayer(String password) {
        Player p = handler.getCurrentPlayer();
        if (!p.loginAttempt(password))
            return false;
        logout();
        p.getLogger().log("");
        p.getLogger().log("DELETED_AT:", "");
        boolean ret = (new File("src/main/resources/database/players/" + p + ".json")).delete();
        ret &= (new File("src/main/resources/database/players/" + p + "-inventory.json")).delete();
        return ret && p.deleteDeckDirectory();
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

    private boolean joinGame(GameType gameType) {
        if (gameType.isMultiPlayer()) {
            if (!gameType.canJoin(handler))
                return false;
            return handler.joinGame(gameType);
        }
        return createGame(gameType);
    }

    private boolean createGame(GameType gameType) {
        if (!gameType.canJoin(handler))
            return false;
        controller.setGameCount(controller.getGameCount() + 1);
        try {
            return gameType.createGame(controller.getGameCount(), handler) != null;
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean startGame(ArrayList<Card> cards) {
        if (handler.getGame() == null)
            return false;
        return handler.startGame(cards);
    }

    private boolean leaveGame() {
        if (handler.getGame() == null)
            return false;
        return endGame(true);
    }

    private boolean playCard(Card card) {
        return playCard(card, null);
    }

    //TODO bug in turns (offline multiplayer at least)
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
        return true;
    }

    private boolean heroPower() {
        Game game = handler.getGame();
        boolean ret = game.getCurrentCharacter().useHeroPower();
        if (ret)
            game.log("hero_power", "");
        return ret;
    }

    private boolean attack(Attackable attacker, Attackable defender) {
        Game game = handler.getGame();
        boolean ret = game.getCurrentCharacter().attack(attacker, defender);
        System.out.println("runner: " + ret);
        if (ret)
            game.log("attack", attacker + " -> " + defender);
        return ret;
    }

    private boolean endGame(boolean surrender) {
        Player player = handler.getCurrentPlayer();
        Game game = player.getGame();
        handler.endGame(surrender);
        player.getLogger().log("end_game", "game id: " + game.getId());
        game.logEndGame();
        return true;
    }
}
