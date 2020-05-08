package graphics.directories.collections;

import controllers.commands.*;
import gameObjects.cards.*;
import gameObjects.heros.*;
import graphics.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;

public class HeroCardsGraphics extends CardsListGraphics {
    private HeroClass heroClass = null;

    HeroCardsGraphics(GraphicsController controller, CommandRunner runner) {
        super(controller, runner);
    }

    void setHeroClass(HeroClass heroClass) {
        this.heroClass = heroClass;
    }

    protected Label getNode(Card card) {
        Label name = new Label(card.toString());
        if (notOwned.contains(card))
            name.setTextFill(Color.LIGHTGRAY);
        return name;
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
