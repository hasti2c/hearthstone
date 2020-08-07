package commands;

import elements.heros.*;
import shared.*;
import commands.types.*;

import java.util.*;

public class CommandParser <T extends CommandType> {
    private final Controller<T> controller;
    private final Class<T> commandTypeClass;

    public CommandParser(Controller<T> controller, Class<T> commandTypeClass) {
        this.controller = controller;
        this.commandTypeClass = commandTypeClass;
    }

    public Command<T> parse(String message) {
        String[] words = message.split("\\|");

        T commandType;
        if (ServerCommandType.class.isAssignableFrom(commandTypeClass))
            commandType = (T) ServerCommandType.valueOf(words[0]);
        else if (ClientCommandType.class.isAssignableFrom(commandTypeClass))
            commandType = (T) ClientCommandType.valueOf(words[0]);
        else
            return null;
        Object[] input = new Object[words.length - 1];

        ArrayList<Pair<String, String>> namePairs = new ArrayList<>();
        for (int i = 1; i < words.length; i++)
            namePairs.add(getNamePair(words[i]));

        for (int i = 0; i < namePairs.size(); i++)
            input[i] = getObject(namePairs.get(i));

        return new Command<>(commandType, input);
    }

    private Pair<String, String> getNamePair(String word) {
        String[] names = word.split(":", 2);
        return new Pair<>(names[0], names[1]);
    }

    private Object getObject(Pair<String, String> namePair) {
        String className = namePair.getFirst(), name = namePair.getSecond();
        return switch (className) {
            case "String": yield name;
            case "Integer": yield Integer.valueOf(name);
            case "Boolean": yield Boolean.valueOf(name);
            case "HeroClass": yield HeroClass.valueOf(name);
            case "ServerCommandType": yield ServerCommandType.valueOf(name);
            default: yield getObject(controller.getObjectsList(className), name);
        };
    }

    private Object getObject(List<?> objects, String name) {
        for (Object object : objects)
            if (name.equals(object.toString()))
                return object;
        return null;
    }
}
