package directories;

import java.util.*;
import cli.*;
import gameObjects.*;
import gameObjects.player.Player;

public abstract class Directory implements Printable {
    protected String name;
    protected Directory parent;
    protected Player player;
    protected Player.Inventory inventory;
    protected ArrayList<Directory> children = new ArrayList<>();
    protected ArrayList<Printable> content = new ArrayList<>();

    public Directory(String name, Directory parent, Player player) {
        this.name = name;
        this.parent = parent;
        this.player = player;
        inventory = player.getInventory();
    }

    protected void clear() {
        children = new ArrayList<>();
        content = new ArrayList<>();
    }

    public abstract void config();

    protected void addChild(Directory child) {
        children.add(child);
    }

    public void addContent(Printable o) {
        content.add(o);
    }

    public String toString() {
        return this.name;
    }

    public Directory getParent() {
        return parent;
    }

    public ArrayList<Directory> getChildren() {
        return this.children;
    }

    public ArrayList<Printable> getContent() {
        return this.content;
    }

    public boolean removeContent(Printable object) {
        boolean b = false;
        for (int i = 0; i < content.size(); i++)
            if (content.get(i) == object) {
                content.remove(i--);
                b = true;
            }
        return b;
    }

    public String getPath() {
        StringBuilder ret = new StringBuilder(name);
        Directory d = this;
        while (d.parent != null) {
            d = d.parent;
            ret.insert(0, d.name + "/");
        }
        return ret.toString();
    }

    public ArrayList<Directory> getList(String path) {
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
                    ret.add(player.getHome());
                    d = player.getHome();
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

    @Override
    public String[] normalPrint(Player currentPlayer) {
        return normalPrint();
    }

    @Override
    public String[][] longPrint(Player currentPlayer) {
        return longPrint();
    }

    protected String[] normalPrint() {
        String[] ret = new String[3];
        ret[1] = toString();
        return ret;
    }

    protected String[][] longPrint() {
        String[][] ret = new String[16][3];
        for (int i = 0; i < 16; i++)
            switch (i) {
                case 1:
                    ret[i][0] = Console.LIGHT_PINK;
                    ret[i][1] = toString();
                    ret[i][2] = Console.RESET;
                    break;
                case 2:
                    ret[i][1] = "directory";
                    break;
                case 4:
                    ret[i][1] = children.size() + content.size() + "";
            }
        return ret;
    }

    public ArrayList<Printable> getPrintables(ArrayList<Character> options, boolean l) {
        ArrayList<Printable> objects = new ArrayList<>();
        objects.addAll(children);
        objects.addAll(content);
        if (options.contains('a'))
            options.remove('a');
        if (options.size() > 0)
            return null;
        if (l)
            player.log("long_list", "directories + content: all");
        else
            player.log("list", "directories + content: all");
        return objects;
    }

    public boolean startsGame(Directory destination) {
        return !isInGame() && destination.isInGame();
    }

    public boolean endsGame(Directory destination) {
        return isInGame() && !destination.isInGame();
    }

    public abstract Game getGame();

    public boolean isInGame() {
        return getGame() != null;
    }
}
