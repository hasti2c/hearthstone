package cli;

import java.util.*;
import controllers.commands.*;
import controllers.game.*;
import gameObjects.*;

public class Console {
    private GameController controller;
    private Scanner sc = new Scanner(System.in);
    private boolean quit = false;
    private CommandRunner runner;

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\033[0;31m";
    public static final String BLUE = "\u001B[34m";
    public static final String GREEN = "\u001b[32m";
    public static final String LIGHT_PINK = "\u001b[38;5;219m";
    private static final String PURPLE = "\u001B[35m";
    private static final String DARK_PINK = "\u001b[38;5;126m";
    private static final String BLACK = "\u001B[30m";
    private static final String BLACK_BG = "\u001B[40m";

    public Console (GameController controller) {
        this.controller = controller;
        this.runner = new CommandRunner(controller, this);
    }

    public void setQuit(boolean quit) { this.quit = quit; }

    public static void print(String s) { System.out.println(s); }

    public void run () {
        while (!quit){
            if(controller.getCurrentPlayer() == null)
                signUpOrLogin();
            else {
                System.out.print(makePrompt());
                parseAndRun(sc.nextLine());
            }
        }
    }

    private void signUpOrLogin () {
        if (confirm("Do you already have an account?"))
            parseAndRun("login");
        else
            parseAndRun("signup");
    }

    private void parseAndRun (String line) {
        Command cmd = parse(line);
        boolean valid = false;
        if (cmd != null)
            valid = runner.run(cmd);
        if (!valid)
            System.out.println("invalid input");
    }

    private Command parse (String input) {
        ArrayList <String> words = new ArrayList<>();
        ArrayList <Character> options = new ArrayList<>();
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
        try {
            if (words.size() == 0)
                return null;
            CommandType commandType = CommandType.valueOf(words.get(0).toUpperCase());
            if (CommandType.MV.equals(commandType)) {
                if (words.size() != 3)
                    return null;
                return new Command(commandType, words.get(1) + ":" + words.get(2), options);
            } else {
                if (words.size() == 0 || words.size() > 2)
                    return null;
                String word = null;
                if (words.size() == 2)
                    word = words.get(1);
                return new Command(commandType, word, options);
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    boolean confirm (String question) {
        System.out.print(makePrompt() + question + " (y/n) ");
        String lastInput = sc.nextLine();
        return lastInput.equalsIgnoreCase("y") || lastInput.equalsIgnoreCase("yes");
    }

    private String makePrompt() {
        Player p = controller.getCurrentPlayer();
        if (p == null)
            return BLUE + "hearthstone" + RESET + ":" + PURPLE + "~" + RESET + "$ ";
        return BLUE + p.toString() + "@hearthstone" + RESET + ":" + PURPLE + p.getCurrentDirectory().getPath() + RESET + "$ ";
    }

    public String getInput(String request) {
        System.out.print(request);
        return sc.nextLine();
    }

    public String getPassword(String request) {
        System.out.print(request + BLACK + BLACK_BG);
        String password = sc.nextLine();
        System.out.print(RESET);
        return password;
    }

    public void normalPrint(ArrayList<Printable> objects) {
        int maxLength = 0;
        ArrayList <String[]> names = new ArrayList<>();
        for (Printable o : objects)
            names.add(o.normalPrint(controller.getCurrentPlayer()));
        for (String[] s : names)
            maxLength = Math.max(maxLength, s[1].length());
        int k = 0;
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i)[0] != null)
                System.out.print(names.get(i)[0]);
            System.out.print(names.get(i)[1]);
            if (names.get(i)[2] != null)
                System.out.print(names.get(i)[2]);
            k += names.get(i)[1].length();
            while (k % (maxLength + 5) != 0) {
                System.out.print(" ");
                k++;
            }
            if (k > 200 - maxLength || i == names.size() - 1) {
                System.out.println();
                k = 0;
            }
        }
    }

    public void longPrint(ArrayList<Printable> objects) {
        int n = objects.size();
        if (n == 0)
            return;

        ArrayList<String[][]> print = new ArrayList<>();
        boolean[] mark = new boolean[16];
        String[] title = {"", "name", "type", "hero class", "content", "price", "rarity", "HP/dur", "mana", "attack", "description", "win %", "wins", "games", "price average", "top card"};
        int[] maxLength = new int[16];
        for (int i = 0; i < 16; i++)
            maxLength[i] = title[i].length();
        
        for(Printable o : objects) {
            String[][] tmp = o.longPrint(controller.getCurrentPlayer());
            print.add(tmp);
            for (int i = 0; i < 15; i++) {
                if (tmp[i][1] != null && !tmp[i][1].equals("")) {
                    mark[i] = true;
                    maxLength[i] = Math.max(maxLength[i], tmp[i][1].length());
                }
            }
        }
        
        for (int i = -1; i < n; i++) {
            for (int j = 0; j < 15; j++) {
                if (!mark[j])
                    continue;
                StringBuilder s = new StringBuilder();
                int length = 0;
                if (i == -1) {
                    s = new StringBuilder(DARK_PINK + title[j]);
                    length = title[j].length();
                }
                else {
                    for (int k = 0; k < 3; k++)
                        if (print.get(i)[j][k] != null)
                            s.append(print.get(i)[j][k]);
                        else if (k == 1)
                            s.append("--");
                    if (print.get(i)[j][1] != null)
                        length = print.get(i)[j][1].length();
                    else
                        length = 2;
                }

                int l1 = (maxLength[j] + 5 - length) / 2, l2 = maxLength[j] + 5 - l1 - length;


                for (int k = 0; k < l1; k++)
                    System.out.print(" ");
                System.out.print(s + RESET);
                for (int k = 0; k < l2; k++)
                    System.out.print(" ");
            }
            System.out.println();
        }
    }
}