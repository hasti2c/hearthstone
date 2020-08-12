package system.updater;

import system.configor.*;

import java.lang.reflect.*;
import java.util.*;

public class UpdatableFields implements Configable {
    private Class<?> updatableClass;
    private ArrayList<String> fieldNames;
    private final HashMap<String, Field> fields = new HashMap<>();

    public UpdatableFields() {}

    @Override
    public void initialize(String initPlayerName) { }

    @Override
    public String getJsonPath(String name, String initPlayerName) {
        return "config/";
    }

    @Override
    public String getName() {
        return updatableClass.getSimpleName().toLowerCase();
    }

    public void setUpdatableClass(Class<?> updatableClass) {
        this.updatableClass = updatableClass;
        for (String name : fieldNames)
            try {
                fields.put(name, findField(name));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
    }

    private Field findField(String fieldName) throws NoSuchFieldException {
        Class<?> currentClass = updatableClass;
        while (!Object.class.equals(currentClass)) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        throw new NoSuchFieldException();
    }

    public HashMap<String, Field> getFields() {
        return fields;
    }
}
