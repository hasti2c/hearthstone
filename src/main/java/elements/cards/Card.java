package elements.cards;

import elements.abilities.targets.*;
import elements.*;
import elements.heros.*;
import javafx.scene.image.*;
import shared.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public abstract class Card extends Playable implements Targetable, Comparable<Card> {
    private int price;
    private RarityType rarity;
    private static Image backImage;

    @Override
    public String getJsonPath(String name, String initPlayerName) {
        try {
            return "cards/" + getSubclass(name).getSimpleName() + "/";
        } catch (NoSuchFileException e) {
            e.printStackTrace();
            return null;
        }
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

    public static Class<? extends Card> getSubclass(String name) throws NoSuchFileException {
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

    public static Card getCard(String name) {
        GameData data = GameData.getInstance();
        Card card = data.getCard(name);
        if (card != null)
            return card;
        switch (name.toLowerCase()) {
            case "minion": return getRandomCard(Minion.class);
            case "spell": return getRandomCard(Spell.class);
            case "weapon": return getRandomCard(Weapon.class);
            case "quest and reward": return getRandomCard(QuestAndReward.class);
            case "special hero card": return getRandomHeroCard();
        }
        if (name.contains("->")) {
            int i = name.indexOf("->");
            Card quest = data.getCard(name.substring(0, i).trim()), reward = data.getCard(name.substring(i + 2).trim());
            if (quest == null)
                return null;
            quest = quest.clone();
            if (quest.abilities.size() > 0)
                quest.abilities.get(0).setSpecificTarget(reward);
            return quest;
        }
        return null;
    }

    private static <C> C getRandomCard(Class<C> cardClass) {
        ArrayList<Card> possibleCards = new ArrayList<>();
        for (Card card : GameData.getInstance().getCardsList())
            if (card.getClass() == cardClass)
                possibleCards.add(card);
        return (C) getRandomElement(possibleCards);
    }

    private static Card getRandomHeroCard() {
        ArrayList<Card> possibleCards = new ArrayList<>();
        for (Card card : GameData.getInstance().getCardsList())
            if (card.getHeroClass() != HeroClass.NEUTRAL)
                possibleCards.add(card);
        return getRandomElement(possibleCards);
    }

    private static void configBackImage() {
        try {
            FileInputStream input = new FileInputStream("src/main/resources/assets/templates/inhand_card_back.png");
            backImage = new Image(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ImageView getImageView(int width, int height, boolean front) {
        if (front)
            return getImageView(width, height);
        if (backImage == null)
            configBackImage();
        return Methods.getImageView(backImage, width, height);
    }

    //TODO clean image methods
}