package gameObjects.heros;

import controllers.game.*;
import gameObjects.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class HeroPower extends Playable {
    private static Image closedImage;

    @Override
    public String getJsonPath(GameController controller, String name) {
        return "hero powers/";
    }

    @Override
    protected String getImagePath() {
        return null;
    }

    @Override
    protected String getFullImagePath() {
        return "hero powers/full/" + name + ".jpg";
    }

    public static ImageView getClosedImageView() {
        if (closedImage == null) {
            try {
                FileInputStream input = new FileInputStream("src/main/resources/assets/templates/hero_power_closed.png");
                closedImage = new Image(input);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        ImageView closedImageView = new ImageView(closedImage);
        closedImageView.setPreserveRatio(true);
        closedImageView.setFitWidth(125);
        return closedImageView;
    }
}
