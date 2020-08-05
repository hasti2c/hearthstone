package server;

import com.google.gson.stream.JsonWriter;
import commands.types.ServerCommandType;
import shared.Controller;
import shared.GameData;
import system.Configable;
import system.Configor;

import java.io.*;
import java.time.format.DateTimeFormatter;

public class ServerController extends Controller<ServerCommandType> implements Configable {
    private int playerCount, gameCount;
    private String initPlayerName;

    private ServerController() {
        GameData.getInstance();
    }

    public static ServerController getInstance() {
        Configor<ServerController> configor = null;
        try {
            configor = new Configor<>("defaults", ServerController.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return configor.getConfigedObject();
    }

    @Override
    public void initialize() {}

    @Override
    public String getJsonPath(String name) {
        return "";
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
        updateJson();
    }

    public int getGameCount() {
        return gameCount;
    }

    public void setGameCount(int gameCount) {
        this.gameCount = gameCount;
        updateJson();
    }

    public static String readFile(String path) throws FileNotFoundException {
        String ret = "";
        try {
            Reader reader = new FileReader(path);
            int data;
            data = reader.read();
            while (data != -1) {
                ret = ret.concat((char) data + "");
                data = reader.read();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void updateJson() {
        try {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter("src/main/resources/database/defaults.json"));
            jsonWriter.setIndent("  ");

            jsonWriter.beginObject();
            jsonWriter.name("playerCount").value(playerCount);
            jsonWriter.name("gameCount").value(gameCount);

            jsonWriter.endObject();
            jsonWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getInitPlayerName() {
        return initPlayerName;
    }

    public void setInitPlayerName(String initPlayerName) {
        this.initPlayerName = initPlayerName;
    }
}