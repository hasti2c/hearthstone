package commands;

import elements.Element;
import elements.abilities.targets.*;
import elements.cards.*;
import elements.heros.*;
import commands.types.*;

import java.util.Arrays;
import java.util.Collections;

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
        for (Object o : input) s.append("-").append(toString(o));
        return s.toString();
    }

    private String toString(Object object) {
        String className = "";
        if (isAssignable(String.class, object))
            className = "String";
        else if (isAssignable(int.class, object) || isAssignable(Integer.class, object))
            className = "Integer";
        else if (isAssignable(HeroClass.class, object))
            className = "HeroClass";
        else if (isAssignable(Deck.class, object))
            className = "Deck";
        else if (isAssignable(Card.class, object) && !Arrays.asList(ServerCommandType.getGameCommands()).contains(commandType))
            className = "Card";
        else if (isAssignable(Element.class, object) && (input.length != 2 || input[0] == object))
            className = "Element|mine";
        else if (isAssignable(Element.class, object))
            className = "Element|opponent";
        return className + ":" + object;
    }

    private boolean isAssignable(Class<?> cls, Object object) {
        return cls.isAssignableFrom(object.getClass());
    }
}
