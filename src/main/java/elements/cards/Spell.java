package elements.cards;

import elements.abilities.Ability;
import elements.abilities.ChangeStats;

import static elements.ElementType.SPELL;

public class Spell extends Card {
    public Spell() {
        elementType = SPELL;
    }

    Card cloneHelper() {
        return new Spell();
    }

    Card copyHelper() {
        return new Spell();
    }

    public boolean isHealingSpell() {
        if (abilities.size() == 0)
            return false;
        for (Ability ability : abilities)
            if (!(ability instanceof ChangeStats changeStats))
                return false;
            else if (!changeStats.onlyHeals())
                return false;
        return true;
    }
}
