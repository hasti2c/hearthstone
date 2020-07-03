package gameObjects.cards.abilities;

import controllers.game.GameController;
import gameObjects.Configable;
import gameObjects.cards.Card;

public abstract class Ability implements Configable {
    protected Card card;
    private AbilityType abilityType;
    private TargetType targetType;

    @Override
    public void initialize(GameController controller) {}

    @Override
    public String getJsonPath(GameController controller, String name) {
        return null;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public void doAction() {
        doAction(getTarget());
    }

    protected abstract void doAction(Card target);

    private Card getTarget() {
        return switch (targetType) {
            case SELF: yield card;
        };
    }

    public AbilityType getAbilityType() {
        return abilityType;
    }
}
