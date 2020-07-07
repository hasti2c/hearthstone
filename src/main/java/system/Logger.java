package system;

import controllers.game.GameController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Logger {
    private String path;
    private Writer writer;
    private String events = "";

    public Logger(String path) {
        this.path = path;
        try {
            writer = new FileWriter(path, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createFile() {
        try {
            (new File(path)).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String line) {
        try {
            writer.write(line + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String type, String details) {
        try {
            if (!"STARTED_AT: ".equals(type) && !"ENDED_AT: ".equals(type))
                events += type + " " + details + "\n";
            writer.write(type + " " + GameController.getTime() + " " + details + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getEvents() {
        return events;
    }
}
