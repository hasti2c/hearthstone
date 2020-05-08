package controllers.commands;


import java.io.*;
import java.util.*;
import cli.*;
import cli.Console;
import controllers.game.*;
import directories.collections.*;
import directories.collections.Collections;
import directories.game.GameCards;
import directories.game.PlayGround;
import graphics.*;
import graphics.directories.*;
import gameObjects.*;
import gameObjects.heros.*;
import directories.*;
import gameObjects.cards.*;
import javafx.util.*;


public class CommandRunner {
    //TODO singleton
    //TODO use string methods
    //TODO handle directories (and cd) better
    private GameController game;
    private Console console;
    private GraphicsController graphics;
    private static ArrayList<Character> usernameChars = new ArrayList<>();

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

    public CommandRunner(GameController game, GraphicsController graphics) {
        this.game = game;
        this.graphics = graphics;
    }

    public CommandRunner(GameController game, Console console) {
        this.game = game;
        this.console = console;
    }

    public boolean run(Command cmd) {
        CommandType commandType = cmd.getCommandType();
        String word = cmd.getWord();
        ArrayList<Character> options = cmd.getOptions();
        boolean ret = false;
        Directory d = null;
        if (game.getCurrentPlayer() != null)
            d = game.getCurrentPlayer().getCurrentDirectory();

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

        if (game.getCurrentPlayer() == null)
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
        } else if (CommandType.SELECT.equals(commandType) && word != null) {
            if ((d instanceof Collections))
                ret = selectHero(word) && cd(word, true);
            else if (d instanceof HeroDirectory)
                ret = selectDeck(word) && cd(word, true);
        } else if (CommandType.DESELECT.equals(commandType)) {
            Hero h = game.getCurrentPlayer().getCurrentHero();
            if (d instanceof Collections && (word == null || (h != null && word.equals(h.toString()))))
                ret = deselectHero();
            else if (d instanceof HeroDirectory && (word == null || (h != null && h.getCurrentDeck() != null && word.equals(h.getCurrentDeck().toString()))))
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

        d = game.getCurrentPlayer().getCurrentDirectory();
        if (d != null)
            d.config();
        if (ret && game.getCurrentPlayer() != null)
            game.getCurrentPlayer().updateJson();
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
            game.readFile("src/main/resources/database/players/" + username + ".json");
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
            Player p = new Player(username);
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
        if (!game.getCurrentPlayer().loginAttempt(password))
            return false;
        String sure = console.getInput("Are you sure you want to delete " + game.getCurrentPlayer().toString() + "? (y/n) ");
        if (sure.equals("n"))
            return true;
        if (!sure.equals("y"))
            return false;
        return deletePlayer();
    }

    private Player signUp(String username, String password) {
        game.setPlayerCount(game.getPlayerCount() + 1);
        Player p = new Player(game, username, password);
        try {
            (new File("src/main/resources/database/decks/" + username)).mkdir();
            (new File(p.getLogPath())).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        game.writeFile(p.getLogPath(), "USER: " + username + "\nPASSWORD: " + password + "\nCREATED_AT: " + game.getTime() + "\n\n");
        p.log("signup", "");
        return p;
    }

    private boolean login(Player p, String password) {
        if (!p.loginAttempt(password))
            return false;
        game.setCurrentPlayer(p);
        p.log("login", "");
        return true;
    }

    private boolean logout() {
        if (game.getCurrentPlayer() != null)
            game.getCurrentPlayer().log("logout", "");
        game.setCurrentPlayer(null);
        return true;
    }

    private boolean deletePlayer() {
        Player p = game.getCurrentPlayer();
        logout();
        p.log("\nDELETED_AT:", "");
        return (new File(p.getJsonPath())).delete();
    }

    private boolean cd(String path, boolean log) {
        Directory current = game.getCurrentPlayer().getCurrentDirectory();
        ArrayList<Directory> list = current.getList(path);
        for (Directory d : list)
            if (d == null)
                return false;
        Directory destination = list.get(list.size() - 1);

        /*if (destination instanceof PlayGround && !destination.getChildren().contains(current)) {
            game.getCurrentPlayer().getHome().createPlayGround();
            Hero h = game.getCurrentPlayer().getCurrentHero();
            assert h != null;
            Deck d = h.getCurrentDeck();
            assert d != null;
            d.setGames(d.getGames() + 1);
        }*/

        game.getCurrentPlayer().setCurrentDirectory(destination);
        if (log)
            game.getCurrentPlayer().log("cd", destination.toString());
        return true;
    }

    private boolean ls(ArrayList<Character> options) {
        boolean l = options.contains('l');
        if (l)
            options.remove(options.indexOf('l'));

        ArrayList<Printable> objects = game.getCurrentPlayer().getCurrentDirectory().getPrintables(options, l);
        if (objects == null)
            return false;
        if (l)
            console.longPrint(objects);
        else
            console.normalPrint(objects);
        return true;
    }

    private boolean selectHero(String name) {
        for (Hero h : game.getCurrentPlayer().getAllHeros())
            if (h.toString().equals(name)) {
                game.getCurrentPlayer().setCurrentHero(h);
                game.getCurrentPlayer().log("select", "hero: " + h.toString());
                return true;
            }
        return false;
    }

    private boolean deselectHero() {
        game.getCurrentPlayer().deselectCurrentHero();
        return true;
    }

    private boolean selectDeck(String name) {
        Hero h = ((HeroDirectory) game.getCurrentPlayer().getCurrentDirectory()).getHero();
        for (Deck d : h.getDecks())
            if (d.toString().equals(name)) {
                h.setCurrentDeck(d);
                return true;
            }
        return false;
    }

    private boolean deselectDeck() {
        Hero h = ((HeroDirectory) (game.getCurrentPlayer().getCurrentDirectory())).getHero();
        h.deselectCurrentDeck();
        return true;
    }

    private boolean addCard(String name) {
        Card card = null;
        DeckDirectory d = ((DeckDirectory) game.getCurrentPlayer().getCurrentDirectory());
        for (Printable c : d.getContent())
            if (c.toString().equals(name))
                card = (Card) c;
        if (card == null || !d.getDeck().canAddCard(card))
            return false;
        d.getDeck().addCard(card);
        //game.getCurrentPlayer().log("add", "card: " + card.toString() + " -> hero: " + d.getHero().toString());
        return true;
    }

    private boolean removeCard(String name) {
        Deck d = ((DeckDirectory) game.getCurrentPlayer().getCurrentDirectory()).getDeck();
        for (Card c : d.getCards())
            if (c.toString().equals(name)) {
                d.removeCard(c);
                //game.getCurrentPlayer().log("remove", "card: " + c.toString() + " -> hero: " + h.toString());
                return true;
            }
        return false;
    }

    private boolean addDeck(String name) {
        Hero h = ((HeroDirectory) game.getCurrentPlayer().getCurrentDirectory()).getHero();
        return validDeckName(name) && h.addNewDeck(name);
    }

    private boolean removeDeck(String name) {
        Hero h = ((HeroDirectory) game.getCurrentPlayer().getCurrentDirectory()).getHero();
        return h.removeDeck(name);
    }

    //TODO add move to graphics
    private boolean moveDeck(String paths) {
        Pair<String, String> pathPair = seperateStrings(paths);

        Directory init = game.getCurrentPlayer().getCurrentDirectory();

        boolean ret = cd(pathPair.getKey(), false);
        Directory d = game.getCurrentPlayer().getCurrentDirectory();
        if (!(d instanceof DeckDirectory)) {
            game.getCurrentPlayer().setCurrentDirectory(init);
            return false;
        }
        Deck deck = ((DeckDirectory) d).getDeck();

        game.getCurrentPlayer().setCurrentDirectory(init);
        ret &= cd(getPathAndDeck(pathPair.getValue()).getKey(), false);
        d = game.getCurrentPlayer().getCurrentDirectory();
        if (!(d instanceof HeroDirectory)) {
            game.getCurrentPlayer().setCurrentDirectory(init);
            return false;
        }
        Hero hero = ((HeroDirectory) d).getHero();

        String newName = getPathAndDeck(pathPair.getValue()).getValue();
        game.getCurrentPlayer().setCurrentDirectory(init);
        return ret && validDeckName(newName) && deck.move(hero, newName);
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
        Player p = game.getCurrentPlayer();
        for (Card c : game.getCardsList())
            if (c.toString().equals(name)) {
                card = c;
                break;
            }
        if (card == null || !p.canBuy(card))
            return false;
        p.addCardToAll(card);
        p.setBalance(p.getBalance() - card.getPrice());
        game.getCurrentPlayer().log("buy", "card: " + card.toString());
        return true;
    }

    private boolean sellCard(String name) {
        Card card = null;
        Player p = game.getCurrentPlayer();
        for (Card c : p.getAllCards())
            if (c.toString().equals(name))
                card = c;
        if (card == null || !p.canSell(card))
            return false;
        if (console != null && !console.getInput("are you sure? (y/n) ").equalsIgnoreCase("y"))
            return true;
        p.removeCardFromAll(card);
        p.setBalance(p.getBalance() + card.getPrice());
        game.getCurrentPlayer().log("sell", "card: " + card.toString());
        return true;
    }

    private boolean wallet() {
        Console.print("balance: " + game.getCurrentPlayer().getBalance() + " coins");
        game.getCurrentPlayer().log("wallet", "");
        return true;
    }

    private boolean status() {
        Directory d = game.getCurrentPlayer().getCurrentDirectory();
        if (d instanceof PlayGround) {
            Game g = ((PlayGround) d).getGame();
            Hero h = g.getHero();
            Console.print(Console.LIGHT_PINK + "hero: " + Console.RESET + h);
            Console.print(Console.LIGHT_PINK + "health: " + Console.RESET + h.getHealth());
            Console.print(Console.LIGHT_PINK + "mana: " + Console.RESET + g.getMana());
        } else {
            assert d instanceof DeckDirectory;
            Deck deck = ((DeckDirectory) d).getDeck();
            Console.print(Console.LIGHT_PINK + "wins: " + Console.RESET + deck.getWins());
            Console.print(Console.LIGHT_PINK + "games: " + Console.RESET + deck.getGames());
        }
        return true;
    }

    private boolean help() {
        Directory d = game.getCurrentPlayer().getCurrentDirectory();
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
        game.getCurrentPlayer().log("help", "");
        return true;
    }

    private boolean playCard(String name) {
        Game g;
        Directory d = game.getCurrentPlayer().getCurrentDirectory();
        if (d instanceof PlayGround)
            g = ((PlayGround) d).getGame();
        else if (d instanceof GameCards && ("cards in game".equals(d.toString()) || "hand".equals(d.toString())))
            g = ((PlayGround) d.getParent()).getGame();
        else
            return false;
        return g.playCard(GameController.getCard(name));
    }

    private boolean endTurn() {
        Game g;
        Directory d = game.getCurrentPlayer().getCurrentDirectory();
        if (d instanceof PlayGround)
            g = ((PlayGround) d).getGame();
        else if (d instanceof GameCards)
            return ((PlayGround) d.getParent()).getGame().endTurn();
        else
            return false;
        return g.endTurn();
    }
}