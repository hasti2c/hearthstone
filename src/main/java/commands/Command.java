package commands;

import elements.abilities.targets.*;
import elements.cards.*;
import elements.heros.*;
import commands.types.*;

import java.util.Arrays;

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
        System.out.println(commandType + " " + Arrays.toString(input));
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
