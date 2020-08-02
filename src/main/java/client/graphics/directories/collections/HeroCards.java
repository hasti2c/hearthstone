package client.graphics.directories.collections;

import client.Client;
import elements.cards.*;
import elements.heros.*;
import client.graphics.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

public class HeroCards extends CardsList {
    private final HeroClass heroClass;

    HeroCards(HeroClass heroClass, GraphicsController controller, Client client) {
        super(controller, client);
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
