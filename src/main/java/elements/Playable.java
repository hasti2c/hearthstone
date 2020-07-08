package elements;

import controllers.game.GameController;
import elements.cards.Card;
import elements.Element;
import elements.abilities.*;
import elements.cards.QuestAndReward;
import elements.heros.HeroClass;
import system.player.Character;
import system.player.GamePlayer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static elements.abilities.AbilityType.*;
import static elements.abilities.targets.TargetType.DISCOVER;
import static elements.abilities.targets.TargetType.SELECTED;

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
    public void initialize(GameController controller) {
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

    public void doActionOnDraw(Character actionPerformer) {
        DRAW.doActionOnRandomAbility(abilities, actionPerformer, this, null, null, null);
    }

    public void doActionOnPlay(Character actionPerformer, Card played) {
        PLAY.doActionOnRandomAbility(abilities, actionPerformer, this, played, null, null);
        BATTLE_CRY.doActionOnRandomAbility(abilities, actionPerformer, this, played, null, null);
    }

    public void doActionOnEndTurn(Character actionPerformer) {
        END_TURN.doActionOnRandomAbility(abilities, actionPerformer, this, null, null, null);
    }

    public void doActionOnDamaged(Character actionPerformer, Card damaged) {
        TAKES_DAMAGE.doActionOnRandomAbility(abilities, actionPerformer, this, null, damaged, null);
    }

    public void doActionOnHeroPower(Character actionPerformer) {
        HERO_POWER.doActionOnRandomAbility(abilities, actionPerformer, this, null, null, null);
    }

    public void doActionOnQuest(Character actionPerformer, Card quest) {
        QUEST.doActionOnRandomAbility(abilities, actionPerformer, this, null, null, quest);
    }
}