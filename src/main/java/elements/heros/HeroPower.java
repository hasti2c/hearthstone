package elements.heros;

import elements.abilities.*;
import elements.*;
import javafx.scene.image.*;
import shared.*;
import system.game.characters.Character;

import java.io.*;

import static elements.ElementType.HERO_POWER;
import static elements.heros.HeroClass.ROGUE;

public class HeroPower extends Playable {
    private static Image closedImage;
    private int healthCost = 0;

    public HeroPower() {
        elementType = HERO_POWER;
    }

    @Override
    public void initialize(String initPlayerName) {
        super.initialize(initPlayerName);
        demote();
    }

    public HeroPower clone() {
        HeroPower hp = new HeroPower();
        hp.name = name;
        hp.heroClass = heroClass;
        hp.description = description;
        hp.mana = mana;
        hp.image = image;
        hp.fullImage = fullImage;
        hp.abilities = abilities;
        return hp;
    }

    @Override
    public String getJsonPath(String name, String initPlayerName) {
        return "hero powers/";
    }

    @Override
    protected String getImagePath() {
        return null;
    }

    @Override
    protected String getFullImagePath() {
        return "hero powers/full/" + name + ".jpg";
    }

    public static ImageView getClosedImageView() {
        if (closedImage == null) {
            try {
                FileInputStream input = new FileInputStream("src/main/resources/assets/templates/hero_power_closed.png");
                closedImage = new Image(input);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return Methods.getImageView(closedImage, 125, -1);
    }

    public boolean isPassive() {
        for (Ability ability : abilities)
            if (ability.getAbilityType().equals(AbilityType.HERO_POWER))
                return false;
        return true;
    }

    public void reduceCost(Character character) {
        character.setMana(this, character.getState().getMana() - getGameMana(character));
        Hero hero = character.getHero();
        hero.setHealth(hero.getHealth() - healthCost);
    }

    public void promote() {
        if (heroClass != ROGUE)
            return;
        abilities.get(0).getNextAbility().setNextAbility();
    }

    public void demote() {
        if (heroClass != ROGUE)
            return;
        abilities.get(0).getNextAbility().removeNextAbility();
    }
}
