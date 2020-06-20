package gameObjects;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Configor {
    private static Configor configorInstance;

    public Configor getInstance() {
        if (configorInstance == null)
            configorInstance = new Configor();
        return configorInstance;
    }

    public void config(Object obj, JsonReader jsonReader) {
        Class objClass = obj.getClass();
        try {
            assert JsonToken.BEGIN_OBJECT.equals(jsonReader.peek());
            jsonReader.beginObject();
            while (JsonToken.NAME.equals(jsonReader.peek())) {
                Field field = objClass.getDeclaredField(jsonReader.nextName());
                try {
                    field.set(obj, readPrimitiveValue(obj, jsonReader));
                } catch (NoSuchMethodException e) {
                    if (field.getType().equals(String.class))
                        field.set(obj, jsonReader.nextString());
                    else if (field.getType)
                    else
                        config(field, jsonReader);
                }
            }
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Object readPrimitiveValue(Object obj, JsonReader jsonReader) throws NoSuchMethodException {
        String typeName = obj.getClass().getName();
        typeName = (typeName.charAt(0) + 'A' - 'a') + typeName.substring(1);
        Method method = JsonReader.class.getMethod("next" + typeName);
        try {
            return method.invoke(jsonReader);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
