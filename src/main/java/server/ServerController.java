package server;

import shared.*;
import system.configor.*;
import system.updater.*;

import java.io.*;

public class ServerController extends Updatable {
    private int playerCount, gameCount;

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
    public void initialize(String initPlayerName) {}

    @Override
    public String getName() {
        return "defaults";
    }

    @Override
    public String getJsonPath(String name, String initPlayerName) {
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
}