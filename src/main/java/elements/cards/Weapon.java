package elements.cards;

import javafx.scene.image.*;
import shared.*;

import java.io.*;

import static elements.ElementType.*;

public class Weapon extends Card {
    private int durability;
    private int attack;
    private static Image closedImage;

    public Weapon() {
        elementType = WEAPON;
    }

    Card cloneHelper() {
        Weapon c = new Weapon();
        c.durability = durability;
        c.attack = attack;
        return c;
    }

    Card copyHelper() {
        Weapon c = new Weapon();
        c.durability = durability;
        c.attack = attack;
        return c;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public static ImageView getClosedImageView() {
        if (closedImage == null) {
            try {
                FileInputStream input = new FileInputStream("src/main/resources/assets/templates/inplay_weapon_closed.png");
                closedImage = new Image(input);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        ImageView closedImageView = Methods.getImageView(closedImage, 125, -1);
        closedImageView.setLayoutY(2);
        return closedImageView;
    }

    @Override
    public int compareTo(Card card) {
        int n = mana - card.getMana();
        if (n != 0)
            return n;
        if (card instanceof QuestAndReward)
            return -1;
        if (!(card instanceof Weapon weapon))
            return 1;
        n = attack - weapon.attack;
        if (n != 0)
            return n;
        n = durability - weapon.durability;
        return n;
    }
}
