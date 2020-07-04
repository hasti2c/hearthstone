package gameObjects.heros;

import gameObjects.player.*;
import javafx.scene.image.*;
import java.io.*;

public enum HeroClass {
    NEUTRAL,
    MAGE,
    ROGUE,
    WARLOCK,
    HUNTER,
    PRIEST;

    public Hero getHero(Inventory inventory) {
        for (Hero h : inventory.getAllHeros())
            if (h.toString().equalsIgnoreCase(toString()))
                return h;
        return null;
    }

    public ImageView getIcon() {
        if (this == NEUTRAL)
            return null;
        try {
            FileInputStream input = new FileInputStream("src/main/resources/assets/heros/icons/" + toString().toLowerCase() + ".png");
            ImageView imageView = new ImageView(new Image(input));
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(75);
            return imageView;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
