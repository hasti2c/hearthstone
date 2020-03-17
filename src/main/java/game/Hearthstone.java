package game;

import java.util.*;
import cli.*;
import cards.*;
import heros.*;
import directories.*;

public class Hearthstone {

    private static Player currentPlayer = null;
    private static ArrayList <Player> playersList = new ArrayList<>();
    private static ArrayList <Hero> herosList = new ArrayList<>();
    private static ArrayList <Card> cardsList = new ArrayList<>();
    private static int playerCount = 0;
    private static Console mainConsole;

    public static Player getCurrentPlayer () { return currentPlayer; }
    public static void setCurrentPlayer (Player p) { currentPlayer = p; }
    public static ArrayList <Player> getPlayersList () { return playersList; }
    public static void addPlayersList (Player p) { playersList.add(p); }
    public static ArrayList <Hero> getHerosList () { return herosList; }
    public static ArrayList <Card> getCardsList () { return cardsList; }
    public static int getPlayerCount () { return playerCount; }
    public static void setPlayerCount (int p) { playerCount = p; }
    public static Console getMainConsole () { return mainConsole; }

    public static void removePlayersList (Player p) {
        for (Player player : playersList)
            if (player == p)
                playersList.remove(player);
    }

    public static void addCardsList (Card c) {
        cardsList.add(c);
        for (Player p : playersList)
            for (Directory s : p.getHome().getChildren())
                if (s.toString().equals("store"))
                    s.addContent(c);
    }

    public static void main(String[] args) {
        Weapon serratedTooth = new Weapon ("Serrated Tooth", "Deathrattle: Give your minions Rush.", RarityType.COMMON, HeroClass.ROGUE, null, 10, 1, 3, 1);
        Minion hotAirBalloon = new Minion ("Hot Air Balloon", "At the start of your turn, gain +1 Health.", RarityType.COMMON, HeroClass.NEUTRAL, null, 10, 1, 2, 1);
        Spell polymorph = new Spell ("Polymorph", "Transform a minion into a 1/1 Sheep.", RarityType.RARE, HeroClass.MAGE, null, 20, 4);
        Spell friendlySmith = new Spell ("Friendly Smith", "Discover a weapon from any class. Add it to your Adventure Deck with +2/+2", RarityType.COMMON, HeroClass.ROGUE, null, 10, 1);
        Minion dreadscale = new Minion("Dreadscale", "At the end of your turn deal 1 damage to all other minions.", RarityType.LEGENDARY, HeroClass.WARLOCK, null, 40, 3, 2, 4);
        Minion deathwing = new Minion ("Deathwing", "Battlecry: Destroy all other minions and discard your hand.", RarityType.LEGENDARY, HeroClass.NEUTRAL, null, 40, 10, 12, 12);
        Spell pyroblast = new Spell ("Pyroblast", "Deal 10 damage.", RarityType.EPIC, HeroClass.MAGE, null, 30, 10);

        addCardsList(serratedTooth);
        addCardsList(hotAirBalloon);
        addCardsList(polymorph);
        addCardsList(friendlySmith);
        addCardsList(dreadscale);
        addCardsList(deathwing);

        Player hasti = new Player ("hasti2c", "minamano");
        playersList.add(hasti);
        currentPlayer = hasti;

        hasti.addCardToAll(serratedTooth);
        hasti.addCardToAll(hotAirBalloon);
        hasti.addCardToAll(polymorph);
        hasti.addCardToAll(friendlySmith);
        hasti.addCardToAll(dreadscale);

        herosList.addAll(hasti.getAllHeros());
        addCardsList(pyroblast);

        mainConsole = new Console();
    }
}
