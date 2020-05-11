package graphics.directories.collections;

import controllers.commands.*;
import gameObjects.cards.*;
import gameObjects.heros.*;
import graphics.*;
import javafx.scene.image.ImageView;

public class HeroCardsGraphics extends CardsListGraphics {
    private HeroClass heroClass = null;

    HeroCardsGraphics(HeroClass heroClass, GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
        border.setId("heroCards-bg");
        this.heroClass = heroClass;
        initTopHBox();
    }

    protected void initTopHBox() {
        super.initTopHBox();
        for (int i = 1; i < 5; i++)
            topHBox.getChildren().get(i).setVisible(false);
    }

    protected ImageView getNode(Card card) {
        return card.getImageView(-1, 300);
    }

    @Override
    protected boolean validCard(Card card) {
        return heroClass == null || heroClass.equals(card.getHeroClass());
    }

    @Override
    protected boolean validHero(HeroClass heroClass) {
        return this.heroClass == null || this.heroClass.equals(heroClass);
    }

    @Override
    protected void runCd() {}
}
