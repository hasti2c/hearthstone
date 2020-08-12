package elements;

import elements.cards.*;
import elements.abilities.*;
import elements.heros.*;
import system.game.Character;
import javafx.scene.image.*;
import javafx.scene.paint.*;

import java.io.*;
import java.util.*;

import static elements.abilities.AbilityType.*;
import static elements.abilities.targets.TargetType.*;

public abstract class Playable extends Element {
    protected String description;
    protected int mana;
    protected Image image, fullImage;
    protected ArrayList<Ability> abilities = new ArrayList<>();
    private ChangeStats changeStatsAbility;
    private Attack attackAbility;
    private AddCard addCardAbility;
    private RemoveCard removeCardAbility;

    @Override
    public void initialize(String initPlayerName) {
        abilities = new ArrayList<>();
        if (changeStatsAbility != null)
            abilities.add(changeStatsAbility);
        if (attackAbility != null)
            abilities.add(attackAbility);
        if (addCardAbility != null)
            abilities.add(addCardAbility);
        if (removeCardAbility != null)
            abilities.add(removeCardAbility);
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getGameMana(Character character) {
        HeroClass heroClass = character.getHero().getHeroClass();
        int ret = mana - heroClass.getManaReduction(this);
        if (character.getPassive() != null)
            ret -= character.getPassive().getManaReduction(this);
        return ret;
    }

    public HeroClass getHeroClass() {
        return this.heroClass;
    }

    public boolean needsTarget() {
        return needsSelection() || needsDiscover();
    }

    public boolean needsSelection() {
        for (Ability ability : abilities)
            if (SELECTED.equals(ability.getTargetType()))
                return true;
        return false;
    }

    public boolean needsDiscover() {
        for (Ability ability : abilities)
            if (DISCOVER.equals(ability.getTargetType()))
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

    public ArrayList<Ability> getAbilities() {
        return abilities;
    }

    public void doActionOnDraw(Character actionPerformer) {
        DRAW.doActionOnRandomAbility(abilities, actionPerformer, this, null, null, null, null);
    }

    public void doActionOnPlay(Character actionPerformer, Element played) {
        doActionOnPlay(actionPerformer, played, null);
    }

    public void doActionOnPlay(Character actionPerformer, Element played, Element selected) {
        PLAY.doActionOnRandomAbility(abilities, actionPerformer, this, (Card) played, null, null, selected);
        BATTLE_CRY.doActionOnRandomAbility(abilities, actionPerformer, this, (Card) played, null, null, selected);
    }

    public void doActionOnEndTurn(Character actionPerformer) {
        END_TURN.doActionOnRandomAbility(abilities, actionPerformer, this, null, null, null, null);
    }

    public void doActionOnDamaged(Character actionPerformer, Element damaged) {
        TAKES_DAMAGE.doActionOnRandomAbility(abilities, actionPerformer, this, null, (Card) damaged, null, null);
    }

    public void doActionOnHeroPower(Character actionPerformer) {
        HERO_POWER.doActionOnRandomAbility(abilities, actionPerformer, this, null, null, null, null);
    }

    public void doActionOnHeroPower(Character actionPerformer, Element selected) {
        HERO_POWER.doActionOnRandomAbility(abilities, actionPerformer, this, null, null, null, selected);
    }

    public void doActionOnQuest(Character actionPerformer, Element quest) {
        QUEST.doActionOnRandomAbility(abilities, actionPerformer, this, null, null, (Card) quest, null);
    }
}
