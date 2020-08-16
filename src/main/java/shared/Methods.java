package shared;

import elements.cards.*;
import javafx.scene.image.*;
import system.game.GameConfigor;

import java.net.*;
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

    public static <C extends Card> ArrayList<C> getCards(Class<C> cardClass, ArrayList<? extends Card> cardsList) {
        ArrayList<C> ret = new ArrayList<>();
        for (Card card : cardsList)
            if (cardClass.isAssignableFrom(card.getClass()))
                ret.add((C) card);
        return ret;
    }

    public static Class<? extends GameConfigor> loadClass(String className) throws ClassNotFoundException {
        String path = "src/main/resources/" + className + ".jar";
        URL url = null;
        try {
            url = new URL("jar:file:" + path + "!/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLClassLoader loader = URLClassLoader.newInstance(new URL[]{url});
        return (Class<? extends GameConfigor>) loader.loadClass(className);
    }
}
