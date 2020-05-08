package controllers.commands;

import java.util.*;

public class Command {
    private CommandType commandType;
    private String word;
    private ArrayList<Character> options;

    public Command(CommandType commandType, String word, ArrayList<Character> options) {
        this.commandType = commandType;
        this.word = word;
        this.options = options;
    }

    public Command(CommandType commandType, String word) {
        this(commandType, word, new ArrayList<>());
    }

    public Command(CommandType commandType, ArrayList<Character> options) {
        this(commandType, null, options);
    }

    public Command(CommandType commandType) {
        this(commandType, null, new ArrayList<>());
    }

    CommandType getCommandType() {
        return commandType;
    }

    String getWord() {
        return word;
    }

    public ArrayList<Character> getOptions() {
        return options;
    }
}
