package shared.commands;

import elements.abilities.targets.Attackable;
import elements.cards.Card;
import elements.heros.Deck;
import elements.heros.HeroClass;
import shared.commands.types.CommandType;

public class Command <T extends CommandType> {
    private final T commandType;
    private final Object[] input;

    public Command(T commandType, Object ...input) {
        this.commandType = commandType;
        this.input = input;
    }

    public T getCommandType() {
        return commandType;
    }

    public Object[] getInput() {
        return input;
    }

    public String toString() {
        StringBuilder s = new StringBuilder(commandType.toString());
        for (int i = 0; i < input.length; i++)
            s.append("-").append(toString(input[i], i));
        return s.toString();
    }

    private String toString(Object object, int index) {
        String className = "";
        if (isAssignable(String.class, object))
            className = "String";
        else if (isAssignable(int.class, object) || isAssignable(Integer.class, object))
            className = "Integer";
        else if (isAssignable(HeroClass.class, object))
            className = "HeroClass";
        else if (isAssignable(Deck.class, object))
            className = "Deck";
        else if (isAssignable(Card.class, object))
            className = "Card";
        else if (isAssignable(Attackable.class, object) && index == 0)
            className = "Attackable|mine";
        else if (isAssignable(Attackable.class, object) && index == 1)
            className = "Attackable|opponent";
        return className + ":" + object;
    }

    private boolean isAssignable(Class<?> cls, Object object) {
        return cls.isAssignableFrom(object.getClass());
    }
}
