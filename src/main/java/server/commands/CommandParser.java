package server.commands;

import elements.heros.HeroClass;
import server.Controller;
import shared.Pair;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {
    private final Controller controller;

    public CommandParser(Controller controller) {
        this.controller = controller;
    }

    public Command parse(String message) {
        String[] words = message.split("-");

        CommandType commandType = CommandType.valueOf(words[0]);
        Object[] input = new Object[words.length - 1];

        ArrayList<Pair<String, String>> namePairs = new ArrayList<>();
        for (int i = 1; i < words.length; i++)
            namePairs.add(getNamePair(words[i]));

        for (int i = 0; i < namePairs.size(); i++)
            input[i] = getObject(namePairs.get(i));

        return new Command(commandType, input);
    }

    private Pair<String, String> getNamePair(String word) {
        String[] names = word.split(":", 2);
        return new Pair<>(names[0], names[1]);
    }

    private Object getObject(Pair<String, String> namePair) {
        String name = namePair.getSecond();
        return switch (namePair.getFirst()) {
            case "String": yield name;
            case "Integer": yield Integer.valueOf(name);
            case "HeroClass": yield HeroClass.valueOf(name);
            case "Deck": yield getObject(controller.getCurrentPlayer().getInventory().getAllDecks(), name);
            case "Card": yield getObject(Controller.getCardsList(), name);
            case "Attackable|mine": yield getObject(controller.getCurrentPlayer().getGame().getCharacters()[0].getAttackables(), name);
            case "Attackable|opponent": yield getObject(controller.getCurrentPlayer().getGame().getCharacters()[1].getAttackables(), name);
            default: yield null;
        };
    }

    private Object getObject(List<?> objects, String name) {
        for (Object object : objects)
            if (name.equals(object.toString()))
                return object;
        return null;
    }
}
