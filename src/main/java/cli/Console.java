package cli;

import directories.Collections;
import game.*;
import directories.*;
import heros.*;
import java.util.*;
import cards.*;

public class Console {
    private Scanner sc = new Scanner(System.in);
    private String lastInput = "";
    boolean quit = false;

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\033[0;31m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String GREEN = "\u001b[32m";
    public static final String LIGHT_PINK = "\u001b[38;5;219m";
    public static final String DARK_PINK = "\u001b[38;5;126m";
    public static final String BLACK = "\u001B[30m";
    public static final String BLACK_BG = "\u001B[40m";

    public Console () {
        while (!quit)
            nextCommand();
    }

    private void nextCommand () {
        if(Hearthstone.getCurrentPlayer() == null) {
            System.out.print(BLUE + "hearthstone" + RESET + ":" + PURPLE + "~" + RESET + "$ Do you already have an account? (y/n) ");
            lastInput = sc.nextLine();
            if (lastInput.length() > 1) {
                CommandMaker cm = new CommandMaker (lastInput, this);
                if (quit)
                    return;
            }
            CommandMaker cm = new CommandMaker ("signup " + lastInput, this);
            if (!cm.getValid())
                System.out.println("invalid input");
            return;
        }

        System.out.print(BLUE + Hearthstone.getCurrentPlayer().toString() + "@hearthstone" + RESET + ":" + PURPLE + Hearthstone.getCurrentPlayer().getCurrentDirectory().getPath() + RESET + "$ ");
        lastInput = sc.nextLine();
        CommandMaker cm = new CommandMaker (lastInput, this);
        if (!cm.getValid())
            System.out.println("invalid input");
    }

    String getInput (String request) {
        System.out.print(request);
        return sc.nextLine();
    }

    String getPassword (String request) {
        System.out.print(request + BLACK + BLACK_BG);
        String password = sc.nextLine();
        System.out.print(RESET);
        return password;
    }
    /*static void printList (ArrayList<String> names) {
        int maxLength = 0;
        for (String name : names)
            maxLength = Math.max(maxLength, name.length());
        int k = 0;
        for (int i = 0; i < names.size(); i++) {
            System.out.print(names.get(i));
            k += names.get(i).length();
            while (k % (maxLength + 3) != 0) {
                System.out.print(" ");
                k++;
            }
            if (k > 200 - maxLength || i == names.size() - 1) {
                System.out.println();
                k = 0;
            }
        }
    }*/

    static void normalPrint (ArrayList<Printable> objects) {
        int maxLength = 0;
        ArrayList <String[]> names = new ArrayList<>();
        for (Printable o : objects)
            names.add(o.normalPrint());
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

    static void longPrint (ArrayList<Printable> objects) {
        int n = objects.size();
        if (n == 0)
            return;

        ArrayList<String[][]> print = new ArrayList<>();
        boolean[] mark = new boolean[12];
        String[] title = {"", "", "", "name", "type", "content", "price", "rarity", "HP/dur", "mana", "attack", "description"};
        int[] maxLength = new int[12];
        for (int i = 0; i < 12; i++)
            maxLength[i] = title[i].length();
        
        for(Printable o : objects) {
            String[][] tmp = o.longPrint();
            print.add(tmp);
            for (int i = 0; i < 12; i++) {
                if (tmp[i][1] != null && !tmp[i][1].equals("")) {
                    mark[i] = true;
                    maxLength[i] = Math.max(maxLength[i], tmp[i][1].length());
                }
            }
        }
        
        for (int i = -1; i < n; i++) {
            for (int j = 0; j < 12; j++) {
                if (!mark[j])
                    continue;;
                String s = "";
                int length = 0;
                if (i == -1) {
                    s = DARK_PINK + title[j];
                    length = title[j].length();
                }
                else {
                    for (int k = 0; k < 3; k++)
                        if (print.get(i)[j][k] != null)
                            s += print.get(i)[j][k];
                        else if (k == 1)
                            s += "--";
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