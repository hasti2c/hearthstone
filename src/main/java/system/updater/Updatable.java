package system.updater;

import com.google.gson.stream.*;
import system.configor.*;

import java.io.*;

public abstract class Updatable implements Configable {
    public void updateJson() {
        updateJson(true);
    }

    public void updateJson(boolean compact) {
        try {
            Updater<?> updater = new Updater<>(getName(), this, compact);
            updater.doUpdate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getJson() {
        return getJson(true);
    }

    public String getJson(boolean compact) {
        try {
            StringWriter json = new StringWriter();
            Updater<?> updater = new Updater<>(getName(), this, new JsonWriter(json), compact);
            updater.doUpdate();
            return json.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
