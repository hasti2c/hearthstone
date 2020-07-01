package gameObjects.heros;

import java.io.*;

import controllers.game.GameController;
import gameObjects.*;
import javafx.scene.image.Image;

public class Hero implements Configable {
    private int health = 30;
    private String name;
    private HeroClass heroClass;
    private HeroPower heroPower;
    private Image gameImage;

    @Override
    public void initialize(GameController controller) {
        configGameImage();
    }

    @Override
    public String getJsonPath(GameController controller, String name) {
        return "heros/";
    }

    private void configGameImage() {
        try {
            FileInputStream input = new FileInputStream("src/main/resources/assets/heros/game/" + name + ".png");
            gameImage = new Image(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getHealth() {
        return health;
    }

    public String toString() {
        return this.name;
    }

    public HeroClass getHeroClass() {
        return this.heroClass;
    }

    public Hero clone() {
        Hero h = new Hero();
        h.name = name;
        h.heroClass = heroClass;
        h.health = health;
        h.gameImage = gameImage;
        h.heroPower = heroPower;
        return h;
    }

    public HeroPower getHeroPower() {
        return heroPower;
    }

    public Image getGameImage() {
        if (gameImage == null)
            configGameImage();
        return gameImage;
    }
}
