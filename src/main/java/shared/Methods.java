package shared;

import java.util.*;

public class Methods {
    public static <T> boolean isArrayOfType(Class<T> type, Object[] input) {
        for (Object o : input)
            if (!type.isAssignableFrom(o.getClass()))
                return false;
        return true;
    }

    public static <T> ArrayList<T> getListOfType(Class<T> type, Object[] input) {
        if (!isArrayOfType(type, input))
            return null;
        ArrayList<T> ret = new ArrayList<>();
        for (Object o : input)
            ret.add((T) o);
        return ret;
    }
}
