package elements.cards;

import elements.abilities.*;

import static elements.ElementType.*;

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

    @Override
    public int compareTo(Card card) {
        int n = mana - card.getMana();
        if (n != 0)
            return n;
        if (card instanceof Minion)
            return -1;
        if (!(card instanceof Spell))
            return 1;
        return 0;
    }
}
