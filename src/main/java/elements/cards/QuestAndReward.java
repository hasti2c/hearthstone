package elements.cards;

import static elements.ElementType.QUEST_AND_REWARD;

public class QuestAndReward extends Card {
    public QuestAndReward() {
        elementType = QUEST_AND_REWARD;
    }

    public Card cloneHelper() {
        return new QuestAndReward();
    }

    public Card copyHelper() {
        return new QuestAndReward();
    }
}
