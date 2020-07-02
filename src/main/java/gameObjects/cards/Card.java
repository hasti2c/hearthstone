package gameObjects.cards;

import controllers.game.GameController;
import gameObjects.*;
import gameObjects.heros.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

public abstract class Card implements Configable {
    private String name, description;
    private int mana, price;
    private HeroClass heroClass;
    private RarityType rarity;
    private CardType cardType;
    private transient Image image, fullImage;

    @Override
    public void initialize(GameController controller) {
        configImage();
        configFullImage();
    }

    @Override
    public String getJsonPath(GameController controller, String name) {
        return "cards/" + getCardClass(name).getSimpleName() + "/";
    }

    public String toString() {
        return this.name;
    }

    String getDescription() {
        return this.description;
    }

    public int getMana() {
        return this.mana;
    }

    public int getPrice() {
        return this.price;
    }

    public CardType getCardType() {
        return this.cardType;
    }

    public HeroClass getHeroClass() {
        return this.heroClass;
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
        c.cardType = cardType;
        return c;
    }

    abstract Card cloneHelper();

    private void configImage() {
        try {
            FileInputStream input = new FileInputStream("src/main/resources/assets/cards/normal/" + name + ".png");
            image = new Image(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ImageView getImageView(int width, int height) {
        if (image == null)
            configImage();
        ImageView iv = new ImageView(image);
        if (width == -1) {
            iv.setPreserveRatio(true);
            iv.setFitHeight(height);
        } else if (height == -1) {
            iv.setPreserveRatio(true);
            iv.setFitWidth(width);
        } else {
            iv.setFitHeight(height);
            iv.setFitWidth(width);
        }
        return iv;
    }

    private void configFullImage() {
        try {
            FileInputStream input = new FileInputStream("src/main/resources/assets/cards/full/" + name + ".jpg");
            this.fullImage = new Image(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ImagePattern getFullImagePattern() {
        if (fullImage == null)
            configFullImage();
        return new ImagePattern(fullImage);
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