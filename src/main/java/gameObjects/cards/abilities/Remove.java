package gameObjects.cards.abilities;

import gameObjects.Playable;
import gameObjects.cards.Card;
import gameObjects.cards.Minion;
import gameObjects.player.GamePlayer;

public class Remove extends Ability {
    @Override
    protected void doAction(GamePlayer actionPerformer, Playable caller, Card target) {
        if (!(target instanceof Minion minion))
            return;
        minion.setHealth(0);
    }
}
