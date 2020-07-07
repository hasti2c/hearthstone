package elements.cards;

import controllers.game.GameController;
import elements.ElementType;
import elements.Playable;
import elements.heros.HeroPower;

import java.util.ArrayList;

import static elements.ElementType.PASSIVE;

public enum Passive {
    TWICE_DRAW,
    OFF_CARDS,
    NURSE,
    FREE_POWER,
    MANA_JUMP;

    private ElementType elementType = PASSIVE;

    public String toString() {
        return switch (this) {
            case TWICE_DRAW: yield "Twice Draw";
            case OFF_CARDS: yield "Off Cards";
            case NURSE: yield "Nurse";
            case FREE_POWER: yield "Free Power";
            case MANA_JUMP: yield "Mana Jump";
        };
    }

    public static Passive getRandomPassive() {
        int n = values().length;
        int i = (int) (Math.floor(Math.random() * n) % n);
        return values()[i];
    }

    public int getDrawCap() {
        if (this == TWICE_DRAW)
            return 2;
        return 1;
    }

    public int getManaReduction(Playable playable) {
        if (this == OFF_CARDS || (this == FREE_POWER && playable instanceof HeroPower))
            return Math.min(1, playable.getMana());
        return 0;
    }

    public int getHeroPowerCap() {
        if (this == FREE_POWER)
            return 2;
        return 1;
    }

    public int getTurnManaPromotion(int myTurn) {
        if (this == MANA_JUMP && myTurn < 10)
            return 1;
        return 0;
    }

    public void doEndTurnAction(ArrayList<Minion> minionsInGame) {
        if (this != NURSE)
            return;
        for (Minion minion : minionsInGame)
            minion.restore();
    }
}
