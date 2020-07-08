package elements.cards;

import elements.Element;
import elements.ElementType;

import static elements.ElementType.QUEST_AND_REWARD;

public class QuestAndReward extends Card {
    private int questMana, usedMana;
    private ElementType questElementType;

    public QuestAndReward() {
        elementType = QUEST_AND_REWARD;
    }

    public Card cloneHelper() {
        QuestAndReward quest = new QuestAndReward();
        quest.questMana = questMana;
        quest.questElementType = questElementType;
        return quest;
    }

    public Card copyHelper() {
        QuestAndReward quest = new QuestAndReward();
        quest.questMana = questMana;
        quest.usedMana = usedMana;
        quest.questElementType = questElementType;
        return quest;
    }

    @Override
    public int compareTo(Card card) {
        if (!(card instanceof QuestAndReward))
            return -1;
        return 0;
    }

    public void addManaUse(Element element, int mana) {
        if (questElementType == element.getElementType())
            usedMana += mana;
    }

    public boolean isDone() {
        return usedMana >= questMana;
    }
}
