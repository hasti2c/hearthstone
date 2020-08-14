package elements;

import elements.cards.*;
import elements.heros.*;
import system.updater.*;

import java.util.*;

public abstract class Element extends Updatable {
    protected String name;
    protected ElementType elementType;
    protected HeroClass heroClass;

    public static <T extends Element> T getRandomElement(ArrayList<T> elements) {
        if (elements.size() == 0)
            return null;
        int n = elements.size(), i = (int) (Math.floor(n * Math.random())) % n;
        return elements.get(i);
    }

    public static <T extends Element> T getElement(ArrayList<T> list, String name) {
        for (T element : list)
            if (element.toString().equals(name))
                return element;
        return null;
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
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
