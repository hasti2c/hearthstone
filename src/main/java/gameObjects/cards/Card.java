package gameObjects.cards;

import controllers.game.*;
import gameObjects.*;
import gameObjects.cards.abilities.targets.Targetable;
import gameObjects.heros.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public abstract class Card extends Playable implements Targetable {
    private int price;
    private RarityType rarity;

    @Override
    public String getJsonPath(GameController controller, String name) {
        return "cards/" + getCardClass(name).getSimpleName() + "/";
    }

    public int getPrice() {
        return this.price;
    }

    public RarityType getRarity() {
        return this.rarity;
    }

    private int getRarityNum() {
        if (RarityType.COMMON.equals(rarity))
            return 0;
        else if (RarityType.RARE.equals(rarity))
            return 1;
        else if (RarityType.EPIC.equals(rarity))
            return 2;
        else
            return 3;
    }

    @Override
    public Card clone() {
        Card c = cloneHelper();
        c.name = name;
        c.description = description;
        c.mana = mana;
        c.price = price;
        c.heroClass = heroClass;
        c.rarity = rarity;
        c.elementType = elementType;
        c.abilities = abilities;
        return c;
    }

    public Card copy() {
        Card c = copyHelper();
        c.name = name;
        c.description = description;
        c.mana = mana;
        c.price = price;
        c.heroClass = heroClass;
        c.rarity = rarity;
        c.elementType = elementType;
        return c;
    }

    abstract Card cloneHelper();

    abstract Card copyHelper();

    @Override
    protected String getImagePath() {
        return "cards/normal/" + name + ".png";
    }

    @Override
    protected String getFullImagePath() {
        return "cards/full/" + name + ".jpg";
    }

    public int compareTo(Card c, Deck deck) {
        if (deck.getUses(this) != deck.getUses(c))
            return deck.getUses(this) - deck.getUses(c);
        else if (getRarityNum() != c.getRarityNum())
            return getRarityNum() - c.getRarityNum();
        else if (mana > c.mana)
            return mana - c.mana;
        else if (this instanceof Minion && !(c instanceof Minion))
            return 1;
        else if (c instanceof Minion && !(this instanceof Minion))
            return -1;
        return 0;
    }

    private static void tryToGetCard(Class<? extends Card> cardClass, String name) throws NoSuchFileException {
        String path = "src/main/resources/database/cards/" + cardClass.getSimpleName() + "/" + name + ".json";
        File file = new File(path);
        if (!file.isFile())
            throw new NoSuchFileException(path);
    }

    private static Class<? extends Card> getSubclass(String name) throws NoSuchFileException {
        try {
            tryToGetCard(Minion.class, name);
            return Minion.class;
        } catch (NoSuchFileException e1) {
            try {
                tryToGetCard(Spell.class, name);
                return Spell.class;
            } catch (NoSuchFileException e2) {
                try {
                    tryToGetCard(Weapon.class, name);
                    return Weapon.class;
                } catch (NoSuchFileException e3) {
                    tryToGetCard(QuestAndReward.class, name);
                    return QuestAndReward.class;
                }
            }
        }
    }

    public static Class<? extends Configable> getCardClass(String cardName) {
        try {
            return getSubclass(cardName);
        } catch (NoSuchFileException e) {
            return switch (cardName) {
                case "Minion" -> Minion.class;
                case "Spell" -> Spell.class;
                case "Weapon" -> Weapon.class;
                case "QuestAndReward" -> QuestAndReward.class;
                default -> null;
            };
        }
    }

    public static String getRandomCardName(Class<? extends Card> cardClass, String name) {
        try {
            getSubclass(name);
            return name;
        } catch (NoSuchFileException e) {
            ArrayList<Card> cards = new ArrayList<>();
            for (Card card : GameController.getCardsList())
                if (cardClass.isAssignableFrom(card.getClass()))
                    cards.add(card);
            int n = cards.size();
            return cards.get((int) ((Math.random() * n) % n)).toString();
        }
    }
}