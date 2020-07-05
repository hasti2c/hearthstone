package gameObjects.heros;

import java.io.*;
import controllers.game.*;
import gameObjects.*;
import gameObjects.cards.abilities.targets.Attackable;
import gameObjects.player.*;
import javafx.scene.image.*;

public class Hero implements Configable, Attackable {
    private int health = 30;
    private String name;
    private HeroClass heroClass;
    private HeroPower heroPower;
    private boolean hasAttacked = false;
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

    public boolean getHasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttack(GamePlayer gamePlayer) {
        if (gamePlayer.getCurrentWeapon() == null || !gamePlayer.canAttack(this))
            return 0;
        return gamePlayer.getCurrentWeapon().getAttack();
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
