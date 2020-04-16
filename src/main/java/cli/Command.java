package cli;

import heros.*;
import directories.*;
import game.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import cards.*;

class Command {
    static Player signup (String username, String password) throws IOException {
        Game.setPlayerCount(Game.getPlayerCount() + 1);
        Player p = Player.getInstance(username, password);
        (new File(p.getLogPath())).createNewFile();
        Hearthstone.writeFile(p.getLogPath(), "USER: " + username + "\nPASSWORD: " + password + "\nCREATED_AT: " + Hearthstone.getTime() + "\n\n");
        p.log("signup", "");
        return p;
    }

    static boolean login (Player p, String password) throws IOException {
        if (!p.loginAttempt(password))
            return false;
        Hearthstone.setCurrentPlayer(p);
        p.log("login", "");
        return true;
    }

    static boolean logout () throws IOException {
        if (Hearthstone.getCurrentPlayer() != null)
            Hearthstone.getCurrentPlayer().log("logout", "");
        Hearthstone.setCurrentPlayer(null);
        return true;
    }

    static boolean deletePlayer () throws IOException {
        Player p = Hearthstone.getCurrentPlayer();
        logout();
        p.log("\nDELETED_AT:", "");
        return (new File(p.getJsonPath())).delete();
    }

    static boolean cd (String path, boolean log) throws IOException {
        ArrayList<Directory> list = Hearthstone.getCurrentPlayer().getCurrentDirectory().getList(path);
        for (Directory d : list)
            if (d == null)
                return false;
        Directory destination = list.get(list.size() - 1);
        Hearthstone.getCurrentPlayer().setCurrentDirectory(destination);
        if (log)
            Hearthstone.getCurrentPlayer().log("cd", destination.toString());
        return true;
    }

    static boolean ls (ArrayList<Character> options) throws IOException {
        boolean l = options.contains('l');
        if (l)
            options.remove(options.indexOf('l'));

        ArrayList <Printable> objects = Hearthstone.getCurrentPlayer().getCurrentDirectory().getPrintables(options, l);
        if (objects == null)
            return false;
        if (l)
            Console.longPrint(objects);
        else
            Console.normalPrint(objects);
        return true;
    }

    static boolean selectHero (String name) throws IOException {
        for (Hero h : Hearthstone.getCurrentPlayer().getAllHeros())
            if (h.toString().equals(name)) {
                Hearthstone.getCurrentPlayer().setCurrentHero(h);
                Hearthstone.getCurrentPlayer().log("select", "hero: " + h.toString());
                return true;
            }
        return false;
    }

    static boolean addCard (String name) throws IOException {
        Card card = null;
        HeroDirectory d = ((HeroDirectory) Hearthstone.getCurrentPlayer().getCurrentDirectory());
        for (Printable c : d.getContent())
            if (c.toString().equals(name))
                card = (Card) c;
        if (card == null || !d.getMyHero().canAddCard(card))
            return false;
        d.getMyHero().addCard(card);
        Hearthstone.getCurrentPlayer().log("add", "card: " + card.toString() + " -> hero: " + d.getMyHero().toString());
        return true;
    }

    static boolean removeCard (String name) throws IOException {
        Hero h = ((HeroDirectory) Hearthstone.getCurrentPlayer().getCurrentDirectory()).getMyHero();
        for (Card c : h.getHeroDeck())
            if (c.toString().equals(name)) {
                h.getHeroDeck().remove(c);
                Hearthstone.getCurrentPlayer().log("remove", "card: " + c.toString() + " -> hero: " + h.toString());
                return true;
            }
        return false;
    }

    static boolean buyCard (String name) throws IOException {
        Card card = null;
        Player p = Hearthstone.getCurrentPlayer();
        for (Card c : Game.getCardsList())
            if (c.toString().equals(name)) {
                card = c;
                break;
            }
        if (card == null || !p.canBuy(card))
            return false;
        p.addCardToAll(card);
        p.setBalance(p.getBalance() - card.getPrice());
        Hearthstone.getCurrentPlayer().log("buy", "card: " + card.toString());
        return true;
    }

    static boolean sellCard (String name) throws IOException {
        Card card = null;
        Player p = Hearthstone.getCurrentPlayer();
        for (Card c : p.getAllCards())
            if (c.toString().equals(name))
                card = c;
        if (card == null || !p.canSell(card))
            return false;
        p.removeCardFromAll(card);
        p.setBalance(p.getBalance() + card.getPrice());
        Hearthstone.getCurrentPlayer().log("sell", "card: " + card.toString());
        return true;
    }

    static boolean wallet () throws IOException {
        System.out.println("balance: " + Hearthstone.getCurrentPlayer().getBalance() + " coins");
        Hearthstone.getCurrentPlayer().log("wallet", "");
        return true;
    }

    static boolean help () throws IOException {
        Directory d = Hearthstone.getCurrentPlayer().getCurrentDirectory();
        String help = "";
        if (d instanceof Home) {
            help += "cd: you can use cd to move to other directories.\n\n";
            help += "                   Home\n";
            help += "                 /     \\\n";
            help += "       Collections     Store\n";
            help += "            |\n";
            help += "         heros\n\n";
            help += "   . : current directory\n";
            help += "   .. : parrent directory\n";
            help += "   ~ : home\n\n";
            help += "-----------------------------------------------------------\n\n";
            help += "ls: see a list of child directories.";
        } else if (d instanceof Collections) {
            help += "ls OPTIONS:\n";
            help += "   -a OR nothing: see your heros and cards\n";
            help += "   -m: see current hero and current deck\n\n";
            help += "   -h: only heros\n";
            help += "   -c: only card\n\n";
            help += "   -l: long printing format\n\n";
            help += "NOTE: you can use the options at the same time.\n";
            help += "you can specify directory to use command.\n\n";
            help += "-----------------------------------------------------------\n\n";
            help += "select: choose hero as your current hero";
        } else if (d instanceof HeroDirectory) {
            help += "ls OPTIONS:\n";
            help += "   -a OR -c OR nothing: see all cards for this hero (special and neutral)\n";
            help += "   -m: see cards in this hero's deck\n";
            help += "   -n: see cards not in this hero's deck that can be added to it.\n\n";
            help += "   -l: long printing format\n\n";
            help += "NOTE: you can use the options at the same time.\n";
            help += "you can specify directory to use command.\n\n";
            help += "-----------------------------------------------------------\n\n";
            help += "add: add card to this hero's deck\n";
            help += "remove: remove card from this hero's deck";
        } else if (d instanceof Store) {
            help += "ls OPTIONS:\n";
            help += "   -a OR nothing: see all cards available in the game\n";
            help += "   -b: see cards you can buy\n";
            help += "   -n: see cards you can sell\n\n";
            help += "   -l: long printing format\n\n";
            help += "NOTE: you can use the options at the same time.\n";
            help += "you can specify directory to use command.\n\n";
            help += "-----------------------------------------------------------\n\n";
            help += "buy: buy card\n";
            help += "sell: sell card\n";
            help += "wallet: get your balance";
        }
        Console.print(help);
        Hearthstone.getCurrentPlayer().log("help", "");
        return true;
    }
}
