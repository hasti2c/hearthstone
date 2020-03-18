package cli;

import directories.Collections;
import game.*;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import directories.*;

class CommandMaker {
    private Console myConsole;
    private ArrayList<String> words = new ArrayList<>();
    private ArrayList<Character> options = new ArrayList<>();
    private boolean valid;
    private static ArrayList<Character> usernameChars = new ArrayList<>();

    static {
        for (char c = 'a'; c <= 'z'; c++)
            usernameChars.add(c);
        for (char c = 'A'; c <= 'Z'; c++)
            usernameChars.add(c);
        for (char c = '0'; c <= '9'; c++)
            usernameChars.add(c);
        usernameChars.add ('_');
        usernameChars.add ('.');
    }

    CommandMaker(String input, Console myConsole) throws IOException {
        this.myConsole = myConsole;
        int i = 0;
        while (i < input.length()) {
            int j = i;
            while (j < input.length() && (input.charAt(j) != ' ' || (j > 0 && input.charAt(j - 1) == '\\')))
                j++;
            if (j - i > 1 && input.charAt(i) == '-') {
                for (int k = i + 1; k < j; k++)
                    if (!options.contains(input.charAt(k)))
                        options.add(input.charAt(k));
            } else if (j - i > 0)
                words.add(input.substring(i, j));
            i = j;
            while(i < input.length() && input.charAt(i) == ' ' && !(i > 0 && input.charAt(i - 1) == '\\'))
                i++;
        }
        for (int j = 0; j < words.size(); j++)
            for (int k = 0; k < words.get(j).length() - 1; k++)
                if (words.get(j).charAt(k) == '\\' && words.get(j).charAt(k + 1) == ' ')
                    words.set(j, words.get(j).substring(0, k) + words.get(j).substring(k + 1));
        this.valid = runCommand();
    }

    private boolean runCommand () throws IOException {
        boolean ret = false;
        words.set(0, words.get(0).toLowerCase());
        Directory d = null;
        if (Hearthstone.getCurrentPlayer() != null)
            d = Hearthstone.getCurrentPlayer().getCurrentDirectory();
        if (words.size() == 2 && words.get(0).equals("signup") && words.get(1).toLowerCase().equals("n"))
            ret = runSignup();
        else if (words.size() == 2 && words.get(0).equals("signup") && words.get(1).toLowerCase().equals("y"))
            ret = runLogin();
        else if (words.size() == 1 && words.get(0).equals("delete") && options.size() == 1 && options.get(0) == 'p')
            ret = runDeletePlayer();

        else if (words.size() == 1 && words.get(0).equals("exit")) {
            if (options.size() == 0)
                ret = Command.logout();
            else if (options.size() == 1 && options.get(0) == 'a') {
                myConsole.setQuit(true);
                ret = true;
            }
        }

        else if (words.size() == 2 && words.get(0).equals("cd"))
            ret = Command.cd(words.get(1));
        else if (words.size() == 1 && words.get(0).equals("ls"))
            ret = Command.ls(options);
        else if (words.size() == 2 && words.get(0).equals("ls")) {
            String initPath = d.getPath();
            ret = Command.cd(words.get(1)) && Command.ls(options) && Command.cd(initPath);
        }
        else if (words.size() == 2 && words.get(0).equals("select") && d instanceof Collections)
            ret = Command.selectHero(words.get(1));
        else if (words.size() == 2 && words.get(0).equals("add") && d instanceof HeroDirectory)
            ret = Command.addCard(words.get(1));
        else if (words.size() == 2 && words.get(0).equals("remove") && d instanceof HeroDirectory)
            ret = Command.removeCard(words.get(1));
        else if (words.size() == 2 && words.get(0).equals("buy") && d instanceof Store)
            ret = Command.buyCard(words.get(1));
        else if (words.size() == 2 && words.get(0).equals("sell") && d instanceof Store)
            ret = Command.sellCard(words.get(1));
        else if (words.size() == 1 && words.get(0).equals("wallet"))
            ret = Command.wallet();
        if (ret && Hearthstone.getCurrentPlayer() != null)
            Hearthstone.getCurrentPlayer().updateJson();
        return ret;
    }

    private boolean runSignup () throws IOException {
        String username = myConsole.getInput("Username: ");
        if (username.length() < 4)
            return false;

        for (int i = 0; i < username.length(); i++)
            if(!usernameChars.contains(username.charAt(i)))
                return false;

        try {
            Hearthstone.readFile("database/players/" + username + ".json");
            return false;
        } catch (FileNotFoundException e) {
            String password = myConsole.getPassword("Password: ");
            if (password.length() < 8)
                return false;

            String passwordAgain = myConsole.getPassword("Repeat Password: ");
            if (!passwordAgain.equals(password))
                return false;

            boolean ret = Command.login (Command.signup (username, password), password);
            //Hearthstone.writeFile(Hearthstone.defaultPath, Hearthstone.getGson.toJson(game));
            return ret;
        }
    }

    private boolean runLogin () throws IOException {
        String username = myConsole.getInput("Username: ");
        try {
            Player p = Player.getInstance("database/players/" + username + ".json");
            String password = myConsole.getPassword("Password: ");
            return Command.login (p, password);
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private boolean runDeletePlayer () {
        String password = myConsole.getPassword("Password: ");
        if (!Hearthstone.getCurrentPlayer().loginAttempt(password))
            return false;
        String sure = myConsole.getInput("Are you sure you want to delete " + Hearthstone.getCurrentPlayer().toString() + "? (y/n) ");
        if (sure.equals("n"))
            return true;
        if (!sure.equals("y"))
            return false;
        return Command.deletePlayer();
    }

    boolean getValid() { return this.valid; }
}
