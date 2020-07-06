package gameObjects.cards;

import javafx.scene.image.*;
import java.io.*;

import static gameObjects.cards.ElementType.WEAPON;

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
        ImageView closedImageView = new ImageView(closedImage);
        closedImageView.setPreserveRatio(true);
        closedImageView.setFitWidth(125);
        closedImageView.setLayoutY(2);
        return closedImageView;
    }
}
