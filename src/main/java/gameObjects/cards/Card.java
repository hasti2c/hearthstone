package gameObjects.cards;

import cli.*;
import controllers.game.GameController;
import directories.collections.*;
import directories.game.PlayGround;
import gameObjects.*;
import gameObjects.heros.*;
import directories.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public abstract class Card implements Printable, Configable {
    private String name, description;
    private int mana, price;
    private HeroClass heroClass;
    private RarityType rarity;
    private CardType cardType;
    private transient Image image;

    @Override
    public void initialize(GameController controller) {
        configImage();
    }

    @Override
    public String getJsonPath(GameController controller, String name) {
        return "cards/";
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
            FileInputStream input = new FileInputStream("src/main/resources/assets/cards/" + name + ".png");
            Image image = new Image(input);
            this.image = image;
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

    public String[] normalPrint(Player currentPlayer) {
        String[] ret = new String[3];
        Directory d = currentPlayer.getCurrentDirectory();
        if (d instanceof Store) {
            if (currentPlayer.canSell(this)) {
                ret[0] = Console.BLUE;
                ret[2] = Console.RESET;
            } else if (currentPlayer.canBuy(this)) {
                ret[0] = Console.GREEN;
                ret[2] = Console.RESET;
            } else if (!currentPlayer.getAllCards().contains(this) && !currentPlayer.canBuy(this)) {
                ret[0] = Console.RED;
                ret[2] = Console.RESET;
            }
            ret[1] = toString();
            return ret;
        } else if (d instanceof PlayGround) {
            ret[1] = toString();
            return ret;
        }

        Deck deck = currentPlayer.getCurrentDeck();
        int cnt = 0;
        if (deck != null)
            for (Card c : deck.getCards())
                if (c == this)
                    cnt++;
        if (cnt > 0) {
            ret[0] = Console.GREEN;
            ret[1] = toString() + " (" + cnt + ")";
            ret[2] = Console.RESET;
        } else
            ret[1] = toString();
        return ret;
    }

    public abstract String[][] longPrint(Player currentPlayer);

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

    public static Class<? extends Configable> getSubclass(String name) {
        int i = 0;
        while (i < name.length() && name.charAt(i) != '/')
            i++;
        String className = name.substring(0, i);
        return switch (className) {
            case "Minion": yield Minion.class;
            case "Spell": yield Spell.class;
            case "Weapon": yield Weapon.class;
            case "QuestAndReward": yield QuestAndReward.class;
            default: yield null;
        };
    }
}
