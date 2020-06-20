package gameObjects.cards;

import directories.collections.*;
import directories.*;
import cli.*;
import gameObjects.heros.*;
import gameObjects.player.Player;

public class Minion extends Card {
    private int hp;
    private int attack;

    Card cloneHelper() {
        Minion c = new Minion();
        c.hp = hp;
        c.attack = attack;
        return c;
    }

    public int getHP() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    @Override
    public String[][] longPrint(Player currentPlayer) {
        Player.Inventory currentInventory = currentPlayer.getInventory();
        String[][] ret = new String[16][3];
        Directory d = currentPlayer.getCurrentDirectory();
        for (int i = 0; i < 16; i++)
            switch (i) {
                case 0:
                    if (d instanceof Store && currentInventory.getAllCards().contains(this)) {
                        ret[i][0] = Console.BLUE;
                        ret[i][1] = "owned";
                        ret[i][2] = Console.RESET;
                        break;
                    } else if (d instanceof Store && currentPlayer.canBuy(this)) {
                        ret[i][0] = Console.GREEN;
                        ret[i][1] = "can buy";
                        ret[i][2] = Console.RESET;
                        break;
                    } else if (d instanceof Store) {
                        ret[i][0] = Console.RED;
                        ret[i][1] = "can't buy";
                        ret[i][2] = Console.RESET;
                        break;
                    }
                    Deck deck;
                    if (d instanceof DeckDirectory)
                        deck = ((DeckDirectory) d).getDeck();
                    else if (d instanceof HeroDirectory)
                        deck = ((HeroDirectory) d).getHero().getCurrentDeck();
                    else if (d instanceof Collections && currentInventory.getCurrentHero() != null)
                        deck = currentInventory.getCurrentHero().getCurrentDeck();
                    else
                        break;
                    int cnt = 0;
                    if (deck != null)
                        for (Card c : deck.getCards())
                            if (c == this)
                                cnt++;
                    if (cnt > 0) {
                        ret[i][0] = Console.GREEN;
                        ret[i][1] = "in deck (" + cnt + ")";
                        ret[i][2] = Console.RESET;
                    } else {
                        ret[i][0] = Console.RED;
                        ret[i][1] = "not in deck";
                        ret[i][2] = Console.RESET;
                    }
                    break;
                case 1:
                    ret[i][0] = Console.LIGHT_PINK;
                    ret[i][1] = toString();
                    ret[i][2] = Console.RESET;
                    break;
                case 2:
                    ret[i][1] = "minion card";
                    break;
                case 3:
                    ret[i][1] = getHeroClass().toString().toLowerCase();
                    break;
                case 5:
                    if (d instanceof Store)
                        ret[i][1] = getPrice() + "";
                    break;
                case 6:
                    ret[i][1] = getRarity().toString().toLowerCase();
                    break;
                case 7:
                    ret[i][1] = hp + "";
                    break;
                case 8:
                    ret[i][1] = getMana() + "";
                    break;
                case 9:
                    ret[i][1] = attack + "";
                    break;
                case 10:
                    ret[i][1] = getDescription() + "";
                    break;
            }
        return ret;
    }
}
