package directories;

import java.util.*;
import game.*;
import cli.*;

public class Directory implements Printable {
    private String name;
    private Directory parent;
    private Player myPlayer;
    private ArrayList<Directory> children = new ArrayList<>();
    private ArrayList<Printable> content = new ArrayList<>();

    Directory (String name, Directory parent, Player myPlayer) {
        this.name = name;
        this.parent = parent;
        this.myPlayer = myPlayer;
    }

    public void addChild (Directory child) { children.add(child); }
    public void addContent (Printable o) { content.add(o); }
    public String toString () { return this.name; }
    public ArrayList<Directory> getChildren () { return this.children; }
    public ArrayList<Printable> getContent () { return this.content; }
    public Player getMyPlayer () { return this.myPlayer; }

    public boolean removeContent (Printable object) {
        boolean b = false;
        for (int i = 0; i < content.size(); i++)
            if (content.get(i) == object) {
                content.remove(i);
                b = true;
            }
        return b;
    }

    public String getPath () {
        String ret = name;
        Directory d = this;
        while (d.parent != null) {
            d = d.parent;
            ret = d.name + "/" + ret;
        }
        return ret;
    }

    public ArrayList<Directory> getList (String path) {
        ArrayList<Directory> ret = new ArrayList<>();
        Directory d = this;
        int i = 0;
        while (i < path.length()) {
            int j = i;
            while (j < path.length() && path.charAt(j) != '/')
                j++;
            switch (path.substring(i, j)) {
                case ".":
                    ret.add(d);
                    break;
                case "..":
                    ret.add(d.parent);
                    d = d.parent;
                    break;
                case "~":
                    ret = new ArrayList<>();
                    ret.add(myPlayer.getHome());
                    d = myPlayer.getHome();
                    break;
                default:
                    boolean mark = false;
                    for (Directory c : d.children)
                        if (path.substring(i, j).equals(c.name)) {
                            d = c;
                            ret.add(d);
                            mark = true;
                            break;
                        }
                    if (!mark)
                        ret.add(null);
                    break;
            }

            i = j + 1;
            if (ret.size() > 0 && ret.get(ret.size() - 1) == null)
                return ret;
        }
        return ret;
    }

    public String[] normalPrint () {
        String[] ret = new String[3];
        ret[1] = toString();
        return ret;
    }

    public String[][] longPrint () {
        String[][] ret = new String[12][3];
        for (int i = 0; i < 12; i++)
            switch (i) {
                case 3:
                    ret[i][0] = Console.LIGHT_PINK;
                    ret[i][1] = toString();
                    ret[i][2] = Console.RESET;
                    break;
                case 4:
                    ret[i][1] = "directory";
                    break;
                case 5:
                    ret[i][1] = children.size() + content.size() + "";
            }
        return ret;
    }
}
