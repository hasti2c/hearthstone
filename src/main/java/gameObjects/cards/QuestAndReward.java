package gameObjects.cards;

import static gameObjects.cards.ElementType.QUEST_AND_REWARD;

public class QuestAndReward extends Card {
    public QuestAndReward() {
        elementType = QUEST_AND_REWARD;
    }

    public Card cloneHelper() {
        return new QuestAndReward();
    }
}
