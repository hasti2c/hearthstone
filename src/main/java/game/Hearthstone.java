package game;

import java.io.*;
import cli.Console;
import com.google.gson.*;

public class Hearthstone {

    private static Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
    private static Player currentPlayer = null;
    private static Game game;

    public static Gson getGson () { return gson; }
    public static Game getGame () { return game; }
    public static Player getCurrentPlayer () { return currentPlayer; }
    public static void setCurrentPlayer (Player p) { currentPlayer = p; }

    public static String readFile (String path) throws IOException {
        String ret = "";
        Reader reader = new FileReader(path);
        int data = reader.read();
        while(data != -1) {
            ret = ret.concat((char) data + "");
            data = reader.read();
        }
        reader.close();
        return ret;
    }

    public static void writeFile (String path, String text) throws IOException {
        Writer writer = new FileWriter(path);
        writer.write(text);
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        game = Game.getInstance();
        new Console();
    }
}
