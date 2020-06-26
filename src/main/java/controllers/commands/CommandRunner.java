package controllers.commands;


import java.io.*;
import java.util.*;
import cli.*;
import cli.Console;
import controllers.game.*;
import directories.collections.*;
import directories.collections.Collections;
import directories.game.PlayGround;
import graphics.*;
import graphics.directories.*;
import gameObjects.*;
import gameObjects.heros.*;
import directories.*;
import gameObjects.cards.*;
import javafx.util.*;


public class CommandRunner {
    private final GameController controller;
    private Console console;
    private GraphicsController graphics;
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

    public CommandRunner(GameController controller, Console console) {
        this.controller = controller;
        this.console = console;
    }

    public boolean run(Command cmd) {
        CommandType commandType = cmd.getCommandType();
        String word = cmd.getWord();
        ArrayList<Character> options = cmd.getOptions();
        boolean ret = false;
        Directory d = null;
        if (controller.getCurrentPlayer() != null)
            d = controller.getCurrentPlayer().getCurrentDirectory();

        if (CommandType.SIGNUP.equals(commandType) && word == null)
            ret = runSignup();
        else if (CommandType.LOGIN.equals(commandType) && word == null)
            ret = runLogin();
        else if (CommandType.EXIT.equals(commandType) && word == null) {
            if (options.size() == 0)
                ret = logout();
            else if (options.size() == 1 && options.get(0) == 'a') {
                logout();
                if (console != null)
                    console.setQuit(true);
                ret = true;
            }
        }

        if (controller.getCurrentPlayer() == null)
            return ret;

        if (CommandType.DELETE.equals(commandType) && word == null && options.size() == 1 && options.get(0) == 'p')
            ret = runDeletePlayer();
        else if (CommandType.CD.equals(commandType) && word != null)
            ret = cd(word, true);
        else if (CommandType.LS.equals(commandType)) {
            if (word == null)
                ret = ls(options);
            else {
                String initPath = d.getPath();
                ret = cd(word, false) && ls(options) && cd(initPath, false);
            }
        } else if (CommandType.SELECT.equals(commandType) && word != null && d instanceof HeroDirectory)
                ret = selectDeck(word) && cd(word, true);
        else if (CommandType.DESELECT.equals(commandType) && d instanceof HeroDirectory && word == null) {
            ret = deselectDeck();
        } else if (CommandType.ADD.equals(commandType) && word != null) {
            if (d instanceof DeckDirectory)
                ret = addCard(word);
            else if (d instanceof HeroDirectory)
                ret = addDeck(word);
        } else if (CommandType.REMOVE.equals(commandType) && word != null) {
            if (d instanceof DeckDirectory)
                ret = removeCard(word);
            else if (d instanceof HeroDirectory)
                ret = removeDeck(word);
        } else if (CommandType.MV.equals(commandType) && word != null && d instanceof HeroDirectory)
            ret = moveDeck(word);
        else if (CommandType.BUY.equals(commandType) && word != null && d instanceof Store)
            ret = buyCard(word);
        else if (CommandType.SELL.equals(commandType) && word != null && d instanceof Store)
            ret = sellCard(word);
        else if (CommandType.WALLET.equals(commandType) && word == null)
            ret = wallet();
        else if (CommandType.HELP.equals(commandType) && (word == null || word.equals("HearthStone")) )
            ret = help();
        else if (CommandType.STATUS.equals(commandType) && word == null && (d instanceof PlayGround || d instanceof DeckDirectory))
            ret = status();
        else if (CommandType.PLAY.equals(commandType) && word != null)
            ret = playCard(word);
        else if (CommandType.ENDTURN.equals(commandType) && word == null)
            ret = endTurn();
        else if (CommandType.HEROPOWER.equals(commandType) && word == null)
            ret = heroPower();

        d = controller.getCurrentPlayer().getCurrentDirectory();
        if (d != null)
            d.config();
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
        return true;
    }

    private boolean runSignup() {
        StartPageGraphics startPage = null;
        if (console == null) {
            assert graphics != null;
            startPage = graphics.getStartPage();
        }

        String username;
        if (console != null)
            username = console.getInput("Username: ");
        else
            username = startPage.getUsername();
        if (username.length() < 4 || !validUsername(username))
            return false;

        try {
            GameController.readFile("src/main/resources/database/players/" + username + ".json");
            return false;
        } catch (FileNotFoundException e) {
            String password;
            if (console != null)
                password = console.getPassword("Password: ");
            else
                password = startPage.getPassword();
            if (password.length() < 8)
                return false;

            if (console != null) {
                String passwordAgain = console.getPassword("Repeat Password: ");
                if (!passwordAgain.equals(password))
                    return false;
            }

            return login(signUp(username, password), password);
        }
    }

    private boolean runLogin() {
        StartPageGraphics startPage = null;
        if (console == null) {
            assert graphics != null;
            startPage = graphics.getStartPage();
        }

        String username;
        if (console != null)
            username = console.getInput("Username: ");
        else
            username = startPage.getUsername();

        try {
            Player p = Player.getExistingPlayer(controller, username);
            String password;
            if (console != null)
                password = console.getPassword("Password: ");
            else
                password = startPage.getPassword();
            return login(p, password);
        } catch (IOException e) {
            return false;
        }
    }

    private boolean runDeletePlayer() {
        String password = console.getPassword("Password: ");
        if (!controller.getCurrentPlayer().loginAttempt(password))
            return false;
        String sure = console.getInput("Are you sure you want to delete " + controller.getCurrentPlayer().toString() + "? (y/n) ");
        if (sure.equals("n"))
            return true;
        if (!sure.equals("y"))
            return false;
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
        if (player != null && player.getCurrentDirectory().isInGame())
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

    private boolean cd(String path, boolean log) {
        Player player = controller.getCurrentPlayer();
        Directory current = player.getCurrentDirectory();
        ArrayList<Directory> list = current.getList(path);
        for (Directory d : list)
            if (d == null)
                return false;
        Directory destination = list.get(list.size() - 1);
        player.setCurrentDirectory(destination);

        if (current.startsGame(destination))
            return startGame();
        else if (current.endsGame(destination))
            return endGame();

        if (log && !current.isInGame())
            controller.getCurrentPlayer().log("cd", destination.toString());
        return true;
    }

    private boolean ls(ArrayList<Character> options) {
        boolean l = options.contains('l');
        if (l)
            options.remove('l');

        ArrayList<Printable> objects = controller.getCurrentPlayer().getCurrentDirectory().getPrintables(options, l);
        if (objects == null)
            return false;
        if (l)
            console.longPrint(objects);
        else
            console.normalPrint(objects);
        return true;
    }

    private boolean selectDeck(String name) {
        Player player = controller.getCurrentPlayer();
        for (Deck d : player.getAllDecks())
            if (d.toString().equals(name)) {
                player.setCurrentDeck(d);
                player.log("select", "deck: " + name);
                return true;
            }
        return false;
    }

    private boolean deselectDeck() {
        Player player = controller.getCurrentPlayer();
        Hero hero = ((HeroDirectory) (player.getCurrentDirectory())).getHero();
        if (player.getCurrentHero() != hero || player.getCurrentDeck() == null)
            return false;
        player.deselectCurrentDeck();
        player.log("deselect", "deck");
        return true;
    }

    private boolean addCard(String name) {
        Card card = null;
        DeckDirectory dd = ((DeckDirectory) controller.getCurrentPlayer().getCurrentDirectory());
        Deck deck = dd.getDeck();
        for (Printable c : dd.getContent())
            if (c.toString().equals(name))
                card = (Card) c;
        if (card == null || !deck.canAddCard(card))
            return false;
        dd.getDeck().addCard(card);
        controller.getCurrentPlayer().log("add", "card: " + card + " -> deck: " + deck.getHero() + "-" + deck);
        return true;
    }

    private boolean removeCard(String name) {
        Deck d = ((DeckDirectory) controller.getCurrentPlayer().getCurrentDirectory()).getDeck();
        for (Card c : d.getCards())
            if (c.toString().equals(name)) {
                d.removeCard(c);
                controller.getCurrentPlayer().log("remove", "card: " + c + " -> deck: " + d.getHero() + "-" + d);
                return true;
            }
        return false;
    }

    private boolean addDeck(String name) {
        Player player = controller.getCurrentPlayer();
        Hero h = ((HeroDirectory) player.getCurrentDirectory()).getHero();
        boolean ret = validDeckName(name) && player.addNewDeck(h.getHeroClass(), name);
        if (ret)
            player.log("add", "deck: " + name + " -> hero: " + h);
        return ret;
    }

    private boolean removeDeck(String name) {
        Player player = controller.getCurrentPlayer();
        boolean ret = player.removeDeck(name);
        if (ret)
            player.log("remove", "deck: " + name);
        return ret;
    }

    private boolean moveDeck(String paths) {
        Player player = controller.getCurrentPlayer();
        Pair<String, String> pathPair = seperateStrings(paths);

        Directory init = player.getCurrentDirectory();

        boolean ret = cd(pathPair.getKey(), false);
        Directory d = player.getCurrentDirectory();
        if (!(d instanceof DeckDirectory)) {
            player.setCurrentDirectory(init);
            return false;
        }
        Deck deck = ((DeckDirectory) d).getDeck();
        Hero initialHero = deck.getHero();

        player.setCurrentDirectory(init);
        ret &= cd(getPathAndDeck(pathPair.getValue()).getKey(), false);
        d = player.getCurrentDirectory();
        if (!(d instanceof HeroDirectory)) {
            player.setCurrentDirectory(init);
            return false;
        }
        Hero finalHero = ((HeroDirectory) d).getHero();

        String newName = getPathAndDeck(pathPair.getValue()).getValue();
        player.setCurrentDirectory(init);
        ret &= validDeckName(newName) && deck.move(finalHero.getHeroClass(), newName);
        if (ret)
            player.log("move", "deck: " + initialHero + "-" + deck + " -> hero: " + finalHero);
        return ret;
    }

    private Pair<String, String> getPathAndDeck(String path) {
        path = "./" + path;
        int i = path.length() - 1;
        while (path.charAt(i) != '/') {
            assert i > 0;
            i--;
        }
        return new Pair<>(path.substring(0, i), path.substring(i + 1));
    }

    private static Pair<String, String> seperateStrings(String s) {
        int i = 0;
        while (s.charAt(i) != ':') {
            i++;
            assert i < s.length();
        }
        return new Pair<>(s.substring(0, i), s.substring(i + 1));
    }

    private boolean buyCard(String name) {
        Card card = null;
        Player p = controller.getCurrentPlayer();
        for (Card c : GameController.getCardsList())
            if (c.toString().equals(name)) {
                card = c;
                break;
            }
        if (card == null || !p.canBuy(card))
            return false;
        p.addCard(card);
        p.setBalance(p.getBalance() - card.getPrice());
        controller.getCurrentPlayer().log("buy", "card: " + card.toString());
        return true;
    }

    private boolean sellCard(String name) {
        Card card = null;
        Player p = controller.getCurrentPlayer();
        for (Card c : p.getAllCards())
            if (c.toString().equals(name))
                card = c;
        if (card == null || !p.canSell(card))
            return false;
        if (console != null && !console.getInput("are you sure? (y/n) ").equalsIgnoreCase("y"))
            return true;
        p.removeCard(card);
        p.setBalance(p.getBalance() + card.getPrice());
        controller.getCurrentPlayer().log("sell", "card: " + card.toString());
        return true;
    }

    private boolean wallet() {
        Console.print("balance: " + controller.getCurrentPlayer().getBalance() + " coins");
        controller.getCurrentPlayer().log("wallet", "");
        return true;
    }

    private boolean status() {
        Player player = controller.getCurrentPlayer();
        Directory d = player.getCurrentDirectory();
        if (d instanceof PlayGround) {
            Game g = d.getGame();
            Hero h = g.getHero();
            Console.print(Console.LIGHT_PINK + "hero: " + Console.RESET + h);
            Console.print(Console.LIGHT_PINK + "health: " + Console.RESET + h.getHealth());
            Console.print(Console.LIGHT_PINK + "mana: " + Console.RESET + g.getMana());
            if (g.getCurrentWeapon() != null)
                Console.print(Console.LIGHT_PINK + "current weapon: " + Console.RESET + g.getCurrentWeapon());
        } else {
            assert d instanceof DeckDirectory;
            Deck deck = ((DeckDirectory) d).getDeck();
            Console.print(Console.LIGHT_PINK + "wins: " + Console.RESET + deck.getWins());
            Console.print(Console.LIGHT_PINK + "games: " + Console.RESET + deck.getGames());
        }
        player.log("status", d.toString());
        return true;
    }

    private boolean help() {
        Directory d = controller.getCurrentPlayer().getCurrentDirectory();
        String help = "";
        if (d instanceof Home) {
            help += "cd: you can use cd to move to other directories.\n\n";
            help += "                   Home\n";
            help += "                 /     \\\n";
            help += "       Collections     Store\n";
            help += "            |\n";
            help += "gameObjects/heros\n\n";
            help += "   . : current directory\n";
            help += "   .. : parrent directory\n";
            help += "   ~ : home\n\n";
            help += "-----------------------------------------------------------\n\n";
            help += "ls: see a list of child directories.";
        } else if (d instanceof Collections) {
            help += "ls OPTIONS:\n";
            help += "   -a OR nothing: see your gameObjects.heros and gameObjects.cards\n";
            help += "   -m: see current hero and current deck\n\n";
            help += "   -h: only gameObjects.heros\n";
            help += "   -c: only card\n\n";
            help += "   -l: long printing format\n\n";
            help += "NOTE: you can use the options at the same time.\n";
            help += "you can specify directory to use command.\n\n";
            help += "-----------------------------------------------------------\n\n";
            help += "select: choose hero as your current hero";
        } else if (d instanceof HeroDirectory) {
            help += "ls OPTIONS:\n";
            help += "   -a OR -c OR nothing: see all gameObjects.cards for this hero (special and neutral)\n";
            help += "   -m: see gameObjects.cards in this hero's deck\n";
            help += "   -n: see gameObjects.cards not in this hero's deck that can be added to it.\n\n";
            help += "   -l: long printing format\n\n";
            help += "NOTE: you can use the options at the same time.\n";
            help += "you can specify directory to use command.\n\n";
            help += "-----------------------------------------------------------\n\n";
            help += "add: add card to this hero's deck\n";
            help += "remove: remove card from this hero's deck";
        } else if (d instanceof Store) {
            help += "ls OPTIONS:\n";
            help += "   -a OR nothing: see all gameObjects.cards available in the controllers.game\n";
            help += "   -b: see gameObjects.cards you can buy\n";
            help += "   -n: see gameObjects.cards you can sell\n\n";
            help += "   -l: long printing format\n\n";
            help += "NOTE: you can use the options at the same time.\n";
            help += "you can specify directory to use command.\n\n";
            help += "-----------------------------------------------------------\n\n";
            help += "buy: buy card\n";
            help += "sell: sell card\n";
            help += "wallet: get your balance";
        }
        Console.print(help);
        controller.getCurrentPlayer().log("help", "");
        return true;
    }

    private boolean playCard(String name) {
        Directory d = controller.getCurrentPlayer().getCurrentDirectory();
        Game g = d.getGame();
        boolean ret = g != null && !"left in deck".equals(d.toString()) && g.playCard(GameController.getCard(name));
        if (ret)
            g.log("p1:play_card", name);
        return ret;
    }

    private boolean endTurn() {
        Directory d = controller.getCurrentPlayer().getCurrentDirectory();
        Game g = d.getGame();
        boolean ret = g != null && g.endTurn();
        if (ret)
            g.log("p1:end_turn", "");
        return ret;
    }

    private boolean heroPower() {
        Directory d = controller.getCurrentPlayer().getCurrentDirectory();
        Game g = d.getGame();
        boolean ret =  g != null && g.useHeroPower();
        if (ret)
            g.log("p1:hero_power", "");
        return ret;
    }

    private boolean startGame() {
        Player player = controller.getCurrentPlayer();
        Directory d = player.getCurrentDirectory();
        Game g = d.getGame();
        if (g == null)
            return false;
        g.startGame();
        player.log("start_game", "game id: " + g.getId());
        try {
            (new File(g.getLogPath())).createNewFile();
            g.log("GAME_ID: " + g.getId());
            g.log("STARTED_AT: ", "");
            g.log("");
            g.log("p1: " + player);
            g.log("p1_hero: " + player.getCurrentHero());
            g.log("p1_deck: " + player.getCurrentDeck());
            g.log("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean endGame() {
        Player player = controller.getCurrentPlayer();
        Directory d = player.getCurrentDirectory();
        Game g = d.getGame();
        if (g == null)
            return false;
        player.log("end_game", "game id: " + g.getId());
        g.log("");
        g.log("ENDED_AT: ", "");
        return true;
    }
}