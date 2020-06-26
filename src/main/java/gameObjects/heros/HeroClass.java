package gameObjects.heros;

import controllers.game.GameController;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public enum HeroClass {
    NEUTRAL,
    MAGE,
    ROGUE,
    WARLOCK,
    HUNTER,
    PRIEST;

    private Hero hero;

    public Hero getHero() {
        if (hero == null)
            for (Hero h : GameController.getHerosList())
                if (h.toString().equalsIgnoreCase(toString()))
                    hero = h;
        return hero;
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
