package gameObjects;

import controllers.game.GameController;
import gameObjects.cards.Card;
import gameObjects.cards.Element;
import gameObjects.cards.ElementType;
import gameObjects.cards.Minion;
import gameObjects.cards.abilities.*;
import gameObjects.cards.abilities.targets.Targetable;
import gameObjects.heros.HeroClass;
import gameObjects.player.GamePlayer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static gameObjects.cards.abilities.AbilityType.*;
import static gameObjects.cards.abilities.targets.TargetType.DISCOVER;
import static gameObjects.cards.abilities.targets.TargetType.SELECTED;

public abstract class Playable extends Element {
    protected String description;
    protected int mana;
    protected Image image, fullImage;
    protected ArrayList<Ability> abilities = new ArrayList<>();
    private ChangeStats changeStatsAbility;
    private Attack attackAbility;
    private AddCard addCardAbility;
    private Remove removeAbility;

    @Override
    public void initialize(GameController controller) {
        abilities = new ArrayList<>();
        if (changeStatsAbility != null)
            abilities.add(changeStatsAbility);
        if (attackAbility != null)
            abilities.add(attackAbility);
        if (addCardAbility != null)
            abilities.add(addCardAbility);
        if (removeAbility != null)
            abilities.add(removeAbility);
    }

    public int getMana() {
        return mana;
    }

    public HeroClass getHeroClass() {
        return this.heroClass;
    }

    public boolean needsTarget() {
        for (Ability ability : abilities)
            if (SELECTED.equals(ability.getTargetType()) || DISCOVER.equals(ability.getTargetType()))
                return true;
        return false;
    }

    protected abstract String getImagePath();

    private void configImage() {
        try {
            FileInputStream input = new FileInputStream("src/main/resources/assets/" + getImagePath());
            image = new Image(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ImageView getImageView(int width, int height) {
        if (image == null)
            configImage();
        ImageView iv = new ImageView(image);
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

    protected abstract String getFullImagePath();

    private void configFullImage() {
        try {
            FileInputStream input = new FileInputStream("src/main/resources/assets/" + getFullImagePath());
            this.fullImage = new Image(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ImagePattern getFullImagePattern() {
        if (fullImage == null)
            configFullImage();
        return new ImagePattern(fullImage);
    }

    public void doActionOnDraw(GamePlayer actionPerformer) {
        for (Ability ability : abilities)
            if (DRAW.equals(ability.getAbilityType()))
                ability.callDoAction(actionPerformer,(Card) this, null);
    }

    public void doActionOnPlay(GamePlayer actionPerformer, Card played) {
        for (Ability ability : abilities)
            if (PLAY.equals(ability.getAbilityType()))
                ability.callDoAction(actionPerformer, this, played);
            else if (BATTLE_CRY.equals(ability.getAbilityType()) && this == played)
                ability.callDoAction(actionPerformer, this, played);
    }

    public void doActionOnEndTurn(GamePlayer actionPerformer) {
        for (Ability ability : abilities)
            if (END_TURN.equals(ability.getAbilityType()))
                ability.callDoAction(actionPerformer, this, null);
    }

    public void doActionOnDamaged(GamePlayer actionPerformer, Card damaged) {
        for (Ability ability : abilities)
            if (TAKES_DAMAGE.equals(ability.getAbilityType()) && this == damaged)
                ability.callDoAction(actionPerformer, this, null);
    }

    public void doActionOnHeroPower(GamePlayer actionPerformer) {
        for (Ability ability : abilities)
            if (HERO_POWER.equals(ability.getAbilityType()))
                ability.callDoAction(actionPerformer, this, null);
    }
}
