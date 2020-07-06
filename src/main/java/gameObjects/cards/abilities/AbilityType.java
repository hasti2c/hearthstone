package gameObjects.cards.abilities;

import gameObjects.Playable;
import gameObjects.cards.Card;
import gameObjects.player.GamePlayer;

import java.util.ArrayList;

import static gameObjects.cards.abilities.targets.TargetType.DISCOVER;
import static gameObjects.cards.abilities.targets.TargetType.SELECTED;

public enum AbilityType {
    DRAW,
    PLAY,
    END_TURN,
    BATTLE_CRY,
    TAKES_DAMAGE,
    HERO_POWER;

    public void doActionOnRandomAbility(ArrayList<Ability> abilities, GamePlayer actionPerformer, Playable caller, Card played, Card damaged) {
        Ability ability = getRandomAbility(getValidAbilities(abilities, caller, played, damaged));
        if (ability == null)
            return;
        ability.callDoAction(actionPerformer, caller, played);
        if (ability.getTargetType() != SELECTED && ability.getTargetType() != DISCOVER)
            actionPerformer.getGraphics().getPlayGround().config();
    }

    private ArrayList<Ability> getValidAbilities(ArrayList<Ability> abilities, Playable caller, Card played, Card damaged) {
        ArrayList<Ability> ret = new ArrayList<>();
        for (Ability ability : abilities)
            if (isValid(ability, caller, played, damaged))
                ret.add(ability);
        return ret;
    }

    private boolean isValid(Ability ability, Playable caller, Card played, Card damaged) {
        if (ability.getAbilityType() != this)
            return false;
        return switch (this) {
            case PLAY: yield played == caller;
            case TAKES_DAMAGE: yield damaged == caller;
            default: yield true;
        };
    }

    private static Ability getRandomAbility(ArrayList<Ability> abilities) {
        if (abilities.size() == 0)
            return null;
        int n = abilities.size(), i = (int) (Math.floor(n * Math.random())) % n;
        return abilities.get(i);
    }
}
