package gameObjects.heros;

import java.io.*;
import controllers.game.*;
import gameObjects.*;
import gameObjects.cards.Element;
import gameObjects.cards.ElementType;
import gameObjects.cards.abilities.targets.Attackable;
import gameObjects.player.*;
import javafx.scene.image.*;

import static gameObjects.cards.ElementType.HERO;

public class Hero extends Element implements Attackable {
    private int health = 30;
    private HeroPower heroPower;
    private boolean hasAttacked = false;
    private Image gameImage;

    public Hero() {
        elementType = HERO;
    }

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


    //TODO takes damage
    public void doDamage(int damage) {
        health -= damage;
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

    public Hero clone() {
        Hero h = new Hero();
        h.name = name;
        h.heroClass = heroClass;
        h.health = health;
        h.gameImage = gameImage;
        h.heroPower = heroPower.clone();
        return h;
    }

    public HeroPower getHeroPower() {
        return heroPower;
    }

    public ImageView getGameImageView(int width, int height) {
        if (gameImage == null)
            configGameImage();
        ImageView iv = new ImageView(gameImage);
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
}
