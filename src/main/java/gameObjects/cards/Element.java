package gameObjects.cards;

import gameObjects.Configable;
import gameObjects.heros.HeroClass;

import java.util.ArrayList;

public abstract class Element implements Configable {
    protected String name;
    protected ElementType elementType;
    protected HeroClass heroClass;

    public static <T extends Element> T getRandomElement(ArrayList<T> elements) {
        if (elements.size() == 0)
            return null;
        int n = elements.size(), i = (int) (Math.floor(n * Math.random())) % n;
        return elements.get(i);
    }

    public String toString() {
        return this.name;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public HeroClass getHeroClass() {
        return this.heroClass;
    }

    public boolean isValid() {
        if (!(this instanceof Minion minion))
            return true;
        return minion.getHealth() > 0;
    }
}
