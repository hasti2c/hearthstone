package elements.heros;

import controllers.game.*;
import elements.abilities.*;
import elements.Playable;
import system.player.Character;
import system.player.GamePlayer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static elements.ElementType.HERO_POWER;
import static elements.heros.HeroClass.ROGUE;

public class HeroPower extends Playable {
    private static Image closedImage;
    private int healthCost = 0;

    public HeroPower() {
        elementType = HERO_POWER;
    }

    @Override
    public void initialize(GameController controller) {
        super.initialize(controller);
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
    public String getJsonPath(GameController controller, String name) {
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
        ImageView closedImageView = new ImageView(closedImage);
        closedImageView.setPreserveRatio(true);
        closedImageView.setFitWidth(125);
        return closedImageView;
    }

    public boolean isPassive() {
        for (Ability ability : abilities)
            if (ability.getAbilityType().equals(AbilityType.HERO_POWER))
                return false;
        return true;
    }

    public void reduceCost(Character character) {
        character.setMana(character.getMana() - getGameMana(character));
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
