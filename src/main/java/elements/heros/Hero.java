package elements.heros;

import java.io.*;

import elements.*;
import elements.abilities.targets.*;
import shared.*;
import system.game.characters.Character;
import javafx.scene.image.*;

import static elements.ElementType.*;

public class Hero extends Element implements Attackable {
    private int health = 30;
    private HeroPower heroPower;
    private boolean hasAttacked = false;
    private Image gameImage;

    public Hero() {
        elementType = HERO;
    }

    @Override
    public void initialize(String initPlayerName) {}

    @Override
    public String getJsonPath(String name, String initPlayerName) {
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

    public void doDamage(Character character, int damage) {
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

    public int getAttack(Character character) {
        if (character.getState().getCurrentWeapon() == null || !character.canAttack(this))
            return 0;
        return character.getState().getCurrentWeapon().getAttack();
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
        return Methods.getImageView(gameImage, width, height);
    }
}
