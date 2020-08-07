package elements.abilities;

import elements.*;
import elements.cards.*;
import system.game.Character;

import java.util.*;

public enum AbilityType {
    DRAW,
    PLAY,
    END_TURN,
    BATTLE_CRY,
    TAKES_DAMAGE,
    HERO_POWER,
    QUEST;

    public void doActionOnRandomAbility(ArrayList<Ability> abilities, Character actionPerformer, Playable caller, Card played, Card damaged, Card quest, Element selected) {
        Ability ability = getRandomAbility(getValidAbilities(abilities, caller, played, damaged, quest));
        if (ability == null)
            return;
        ability.callDoAction(actionPerformer, caller, played, selected);
    }

    private ArrayList<Ability> getValidAbilities(ArrayList<Ability> abilities, Playable caller, Card played, Card damaged, Card quest) {
        ArrayList<Ability> ret = new ArrayList<>();
        for (Ability ability : abilities)
            if (isValid(ability, caller, played, damaged, quest))
                ret.add(ability);
        return ret;
    }

    private boolean isValid(Ability ability, Playable caller, Card played, Card damaged, Card quest) {
        if (ability.getAbilityType() != this)
            return false;
        return switch (this) {
            case BATTLE_CRY: yield played == caller;
            case TAKES_DAMAGE: yield damaged == caller;
            case QUEST: yield quest == caller;
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
