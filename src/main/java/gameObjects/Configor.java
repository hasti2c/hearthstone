package gameObjects;

import com.google.gson.stream.*;
import controllers.game.GameController;
import gameObjects.cards.Card;
import gameObjects.heros.Hero;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class Configor<O extends Configable> {
    private static final Map<Class, Map<String, ?>> instances = new HashMap<>();

    private final String name;
    private final JsonReader jsonReader;
    private O object;
    private GameController controller;

    public Configor(GameController controller, String name, Class<O> objectClass, JsonReader jsonReader) {
        this.controller = controller;
        this.name = name;
        this.jsonReader = jsonReader;
        try {
            object = objectClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Configor(GameController controller, String name, Class<O> objectClass) throws FileNotFoundException {
        this.controller = controller;
        this.name = name;
        try {
        object = objectClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        jsonReader = new JsonReader(new FileReader("src/main/resources/database/" + object.getJsonPath(controller, name) + name + ".json"));
    }

    public O getConfigedObject() {
        if (!mapContainsObject())
            config();
        else
            object = getObjectFromMap();
        object.initialize(controller);
        return object;
    }

    private void config() {
        try {
            jsonReader.beginObject();
            while (JsonToken.NAME.equals(jsonReader.peek())) {
                Field field = findField(jsonReader.nextName());
                field.setAccessible(true);
                fillField(field);
                field.setAccessible(false);
                System.out.println(field);
            }
            jsonReader.endObject();
        } catch (IOException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        putInMap();
    }

    private Field findField(String fieldName) throws NoSuchFieldException {
        Class currentClass = object.getClass();
        while (!currentClass.equals(Object.class)) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        System.out.println(fieldName);
        throw new NoSuchFieldException();
    }

    private void fillField(Field field) {
        try {
            if (JsonToken.BEGIN_ARRAY.equals(jsonReader.peek())) {
                jsonReader.beginArray();
                field.set(object, readList((Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]));
                jsonReader.endArray();
            } else
                field.set(object, readValue(field.getType()));
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    private <T> List<T> readList(Class<T> readType) {
        ArrayList<T> list = new ArrayList<>();
        try {
            while(!JsonToken.END_ARRAY.equals(jsonReader.peek()))
                list.add(readValue(readType));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private <T> T readValue(Class<T> readType) {
        try {
            if (JsonToken.NULL.equals(jsonReader.peek())) {
                jsonReader.nextNull();
                return null;
            } else {
                try {
                    if (readType.isEnum())
                        return (T) readEnum((Class<? extends Enum>)readType);
                    else {
                        return readPrimitive(readType);
                    }
                } catch (NoSuchMethodException e1) {
                    String objectName = jsonReader.nextString();
                    Configable object = getNewObject((Class<? extends Configable>) readType, objectName);
                    return (T) readObject(object.getClass(), objectName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Configable getNewObject(Class<? extends Configable> readType, String objectName) {
        try {
            System.out.println(readType);
            return readType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            try {
                assert readType.equals(Card.class);
                System.out.println(objectName);
                return Card.getSubclass(objectName).getDeclaredConstructor().newInstance();
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
                ex.printStackTrace();
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T extends Configable> T readObject(Class<T> readType, String objectName) {
        try {
            Configor<T> objectConfigor = new Configor<>(controller, objectName, readType);
            return objectConfigor.getConfigedObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T extends Enum<T>> T readEnum(Class<T> readType) {
        String objectName = null;
        try {
            objectName = jsonReader.nextString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Enum.valueOf(readType, objectName);
    }

    private <T> T readPrimitive(Class<T> readType) throws NoSuchMethodException {
        T ret = null;
        try {
            ret = (T) getPrimitiveMethod(readType).invoke(jsonReader);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private Method getPrimitiveMethod(Class readType) throws NoSuchMethodException {
        String methodName;
        if (boolean.class.equals(readType) || Boolean.class.equals(readType))
            methodName = "nextBoolean";
        else if (long.class.equals(readType) || Long.class.equals(readType))
            methodName = "nextLong";
        else if (byte.class.equals(readType) || short.class.equals(readType) || int.class.equals(readType) || char.class.equals(readType) || Number.class.isAssignableFrom(readType) || Character.class.isAssignableFrom(readType))
            methodName = "nextInt";
        else if (String.class.equals(readType))
            methodName = "nextString";
        else if (readType.isEnum())
            methodName = "nextString";
        else
            throw new NoSuchMethodException();
        return JsonReader.class.getMethod(methodName);
    }

    private void putInMap() {
        Map<String, O> classMap;
        if (instances.containsKey(object.getClass()))
            classMap = (Map<String, O>) instances.get(object.getClass());
        else {
            classMap = new HashMap<>();
            instances.put(object.getClass(), classMap);
        }
        classMap.put(name, object);
    }

    private boolean mapContainsObject() {
        return instances.containsKey(object.getClass()) && instances.get(object.getClass()).containsKey(name);
    }

    private O getObjectFromMap() {
        return (O) instances.get(object.getClass()).get(name);
    }

    public static <O> void putInMap(O object, String name) {
        Map<String, O> classMap;
        if (instances.containsKey(object.getClass()))
            classMap = (Map<String, O>) instances.get(object.getClass());
        else {
            classMap = new HashMap<>();
            instances.put(object.getClass(), classMap);
        }
        classMap.put(name, object);
    }
}