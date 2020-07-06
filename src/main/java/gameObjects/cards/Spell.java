package gameObjects.cards;

import static gameObjects.cards.ElementType.SPELL;

public class Spell extends Card {
    public Spell() {
        elementType = SPELL;
    }

    Card cloneHelper() {
        return new Spell();
    }
}
