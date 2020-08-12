package system.updater;

import com.google.gson.stream.*;
import elements.abilities.*;
import elements.cards.*;
import system.configor.*;
import system.player.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class Updater <O extends Updatable> {
    private UpdatableFields fields;
    private O object;
    private String name;
    private JsonWriter jsonWriter;
    private boolean compact;
    private String initPlayerName;

    public Updater(String name, O object, boolean compact) throws FileNotFoundException {
        initialize(name, object, compact);
        try {
            jsonWriter = new JsonWriter(new FileWriter("src/main/resources/database/" + object.getJsonPath(name, initPlayerName) + name + ".json"));
            jsonWriter.setIndent("  ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Updater(String name, O object, JsonWriter jsonWriter, boolean compact) throws FileNotFoundException {
        initialize(name, object, compact);
        this.jsonWriter = jsonWriter;
    }

    public Updater(String name, O object) throws FileNotFoundException {
        this(name, object, true);
    }

    public Updater(String name, O object, JsonWriter jsonWriter) throws FileNotFoundException {
        this(name, object, jsonWriter, true);
    }

    private void initialize(String name, O object, boolean compact) throws FileNotFoundException {
        this.name = name;
        this.compact = compact;
        this.object = object;

        Configor<UpdatableFields> configor = new Configor<>(object.getClass().getSimpleName(), UpdatableFields.class);
        fields = configor.getConfigedObject();
        fields.setUpdatableClass(object.getClass());
        if (object instanceof Player)
            initPlayerName = name;
    }

    public void doUpdate() {
        update();
        try {
            jsonWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void update() {
        try {
            jsonWriter.beginObject();

            HashMap<String, Field> fieldsMap = fields.getFields();
            for (String name : fieldsMap.keySet()) {
                jsonWriter.name(name);
                Field field = fieldsMap.get(name);
                field.setAccessible(true);
                update(field.get(object));
                field.setAccessible(false);
            }

            jsonWriter.endObject();
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void update(Object value) {
        if (value == null) {
            try {
                jsonWriter.nullValue();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        if (!updateList(value))
            updateObject(value);
    }

    private boolean updateList(Object value) {
        if (!List.class.isAssignableFrom(value.getClass()))
            return false;

        try {
            jsonWriter.beginArray();
            List<?> list = (List<?>) value;
            for (Object o : list)
                updateObject(o);
            jsonWriter.endArray();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateObject(Object value) {
        if (!updatePrimitive(value))
            updateConfigable(value);
    }

    private boolean updatePrimitive(Object value) {
        try {
            Class<?> fieldType = value.getClass();
            if (Number.class.isAssignableFrom(fieldType) || int.class.isAssignableFrom(fieldType) || double.class.isAssignableFrom(fieldType) || long.class.isAssignableFrom(fieldType) || short.class.isAssignableFrom(fieldType))
                jsonWriter.value((Number) value);
            else if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType))
                jsonWriter.value((Boolean) value);
            else if (String.class.isAssignableFrom(fieldType))
                jsonWriter.value((String) value);
            else if (fieldType.isEnum())
                jsonWriter.value(value.toString());
            else
                return false;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateConfigable(Object value) {
        try {
            Class<?> fieldType = value.getClass();
            if (!Configable.class.isAssignableFrom(fieldType))
                return;

            Configable configable = (Configable) value;
            if ((!Updatable.class.isAssignableFrom(fieldType) || compact || Card.class.isAssignableFrom(fieldType)) && !Ability.class.isAssignableFrom(fieldType)) {
                jsonWriter.value(configable.getName());
                return;
            }

            Updatable updatable = (Updatable) configable;
            Updater<?> updater = new Updater<>(updatable.getName(), updatable, jsonWriter, compact);
            updater.update();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

