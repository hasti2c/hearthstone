package gameObjects.cards;

import gameObjects.cards.abilities.Ability;
import gameObjects.cards.abilities.AddCard;
import gameObjects.cards.abilities.ChangeStats;

import static gameObjects.cards.ElementType.SPELL;

public class Spell extends Card {
    public Spell() {
        elementType = SPELL;
    }

    Card cloneHelper() {
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
