package graphics.directories.collections;

import controllers.commands.*;
import gameObjects.cards.*;
import gameObjects.heros.*;
import graphics.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class HeroCards extends CardsList {
    private HeroClass heroClass;

    HeroCards(HeroClass heroClass, GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
        border.setId("heroCards-bg");
        this.heroClass = heroClass;
        initTopHBox();
    }

    protected Node getNode(Card card) {
        ImageView imageView = card.getImageView(-1, 300);
        if (owned.contains(card))
            return imageView;

        Button buy = new Button("View In Store");
        buy.setOnAction(e -> controller.viewCardInStore(card));

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(5, 0, 5, 0));
        vBox.setSpacing(5);
        vBox.getChildren().addAll(imageView, buy);
        return vBox;
    }

    @Override
    protected boolean validCard(Card card) {
        return heroClass == null || heroClass.equals(card.getHeroClass());
    }

    @Override
    protected boolean validHero(HeroClass heroClass) {
        return this.heroClass == null || this.heroClass.equals(heroClass);
    }

    protected FXMLLoader getLoader() {
        return new FXMLLoader(HeroCards.class.getResource("/fxml/directories/heroCards.fxml"));
    }
}
