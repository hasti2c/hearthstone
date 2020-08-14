package shared;

import javafx.scene.image.*;

import java.util.*;

public class Methods {
    public static <T> boolean isArrayOfType(Class<T> type, Object... input) {
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

    public static ImageView getImageView(Image image, int width, int height) {
        ImageView iv = new ImageView(image);
        if (width == -1) {
            iv.setPreserveRatio(true);
            iv.setFitHeight(height);
        } else if (height == -1) {
            iv.setPreserveRatio(true);
            iv.setFitWidth(width);
        } else {
            iv.setFitHeight(height);
            iv.setFitWidth(width);
        }
        return iv;
    }
}
