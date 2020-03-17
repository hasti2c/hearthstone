package cli;

import heros.*;
import directories.*;
import game.*;

import java.util.ArrayList;
import cards.*;

class Command {
    static Player signup (String username, String password) {
        Player newPlayer = new Player (username, password);
        Hearthstone.addPlayersList(newPlayer);
        return newPlayer;
    }

    static boolean login (Player p, String password) {
        if (!p.loginAttempt(password))
            return false;
        Hearthstone.setCurrentPlayer(p);
        return true;
    }
    static boolean logout () {
        Hearthstone.setCurrentPlayer(null);
        return true;
    }

    static boolean deletePlayer () {
        Hearthstone.removePlayersList(Hearthstone.getCurrentPlayer());
        return logout();
    }

    static boolean cd (String path) {
        ArrayList<Directory> list = Hearthstone.getCurrentPlayer().getCurrentDirectory().getList(path);
        for (Directory d : list)
            if (d == null)
                return false;
        Hearthstone.getCurrentPlayer().setCurrentDirectory(list.get(list.size() - 1));
        return true;
    }

    static boolean ls (ArrayList<Character> options) {
        ArrayList <Printable> objects = new ArrayList<>();
        Directory d = Hearthstone.getCurrentPlayer().getCurrentDirectory();
        if (d instanceof Collections) {
            if (options.contains('m') && !options.contains('a'))
                objects.add(Hearthstone.getCurrentPlayer().getCurrentHero());
            else
                objects.addAll(d.getChildren());
        } else if (d instanceof HeroDirectory) {
            ArrayList<Card> deck = ((HeroDirectory) d).getMyHero().getHeroDeck();
            if (options.contains('m') && options.contains('n') && !options.contains('a')) {
                for (Printable c : d.getContent())
                    if (deck.contains(c) || (!deck.contains(c) && ((HeroDirectory) d).getMyHero().canAddCard((Card) c)))
                        objects.add(c);
                options.remove(options.indexOf('m'));
                options.remove(options.indexOf('n'));
            } else if (options.contains('m') && !options.contains('a')) {
                objects.addAll(deck);
                options.remove(options.indexOf('m'));
            } else if (options.contains('n') && !options.contains('a')) {
                for (Printable c : d.getContent())
                    if (((HeroDirectory) d).getMyHero().canAddCard((Card) c))
                        objects.add(c);
                options.remove(options.indexOf('n'));
            } else {
                objects.addAll(d.getContent());
                if (options.contains('a'))
                    options.remove(options.indexOf('a'));
            }
        } else if (d instanceof Store) {
            if (options.contains('b') && options.contains('s') && !options.contains('a')) {
                for (Card c : Hearthstone.getCardsList())
                    if (!Hearthstone.getCurrentPlayer().getAllCards().contains(c) || Hearthstone.getCurrentPlayer().canSell(c))
                        objects.add(c);
                options.remove(options.indexOf('b'));
                options.remove(options.indexOf('s'));
            } else if (options.contains('b') && !options.contains('a')) {
                objects.addAll(d.getContent());
                options.remove(options.indexOf('b'));
            } else if (options.contains('s') && !options.contains('a')) {
                for (Card c : Hearthstone.getCurrentPlayer().getAllCards())
                    if (Hearthstone.getCurrentPlayer().canSell(c))
                        objects.add(c);
                options.remove(options.indexOf('s'));
            } else {
                objects.addAll(Hearthstone.getCardsList());
                if (options.contains('a'))
                    options.remove(options.indexOf('a'));
            }
        } else {
            objects.addAll(d.getChildren());
            objects.addAll(d.getContent());
            if (options.contains('a'))
                options.remove(options.indexOf('a'));
        }
        if (options.size() > 1 || (options.size() == 1 && options.get(0) != 'l'))
            return false;

        if (options.size() == 0) {
            Console.normalPrint(objects);
            return true;
        }
        Console.longPrint(objects);
        return true;
    }


    static boolean selectHero (String name) {
        for (Hero h : Hearthstone.getCurrentPlayer().getAllHeros())
            if (h.toString().equals(name)) {
                Hearthstone.getCurrentPlayer().setCurrentHero(h);
                return true;
            }
        return false;
    }

    static boolean addCard (String name) {
        Card card = null;
        HeroDirectory d = ((HeroDirectory) Hearthstone.getCurrentPlayer().getCurrentDirectory());
        for (Printable c : d.getContent())
            if (c.toString().equals(name))
                card = (Card) c;
        if (card == null || !d.getMyHero().canAddCard(card))
            return false;
        d.getMyHero().addCard(card);
        return true;
    }

    static boolean removeCard (String name) {
        ArrayList <Card> deck = ((HeroDirectory) Hearthstone.getCurrentPlayer().getCurrentDirectory()).getMyHero().getHeroDeck();
        for (Card c : deck)
            if (c.toString().equals(name)) {
                deck.remove(c);
                return true;
            }
        return false;
    }

    static boolean buyCard (String name) {
        Card card = null;
        Player p = Hearthstone.getCurrentPlayer();
        for (Card c : Hearthstone.getCardsList())
            if (c.toString().equals(name)) {
                card = c;
                break;
            }
        if (!p.canBuy(card))
            return false;
        p.addCardToAll(card);
        p.setBalance(p.getBalance() - card.getPrice());
        return true;
    }

    static boolean sellCard (String name) {
        Card card = null;
        Player p = Hearthstone.getCurrentPlayer();
        for (Card c : p.getAllCards())
            if (c.toString().equals(name))
                card = c;
        if (card == null || !p.canSell(card))
            return false;
        p.removeCardFromAll(card);
        p.setBalance(p.getBalance() + card.getPrice());
        return true;
    }

    static boolean wallet () {
        System.out.println("balance: " + Hearthstone.getCurrentPlayer().getBalance() + " coins");
        return true;
    }
}
