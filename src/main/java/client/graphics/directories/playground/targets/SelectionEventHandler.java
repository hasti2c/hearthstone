package client.graphics.directories.playground.targets;

import client.*;
import client.graphics.directories.playground.*;
import commands.*;
import commands.types.*;
import elements.*;
import elements.abilities.*;
import elements.abilities.targets.*;
import elements.cards.*;
import elements.heros.*;

import java.util.*;

import static elements.ElementType.*;

public class SelectionEventHandler extends TargetEventHandler {
    private final Client client;
    private final CharacterGraphics<?> character;
    private final Playable caller;
    private final ArrayList<ElementType> targetElementTypes;

    public SelectionEventHandler(Client client, CharacterGraphics<?> character, Playable caller, Targetable targetable, Ability ability) {
        super(targetable, character.getNode((Element) targetable));
        this.client = client;
        this.character = character;
        this.caller = caller;
        targetElementTypes = ability.getTargetElementTypes();
        initialize();
    }

    @Override
    protected void setSelectedTargetable(Targetable targetable) {
    }

    @Override
    protected boolean isEnough() {
        return true;
    }

    @Override
    protected void deselectedMode() {
        if (targetElementTypes.contains(MINION)) {
            character.enableMinions();
            character.getOpponent().enableMinions();
        }
        if (targetElementTypes.contains(HERO)) {
            character.enableHero();
            character.getOpponent().enableHero();
        }
    }

    @Override
    protected void oneSelectedMode() {}

    @Override
    protected void doAction() {
        if (caller instanceof HeroPower)
            client.request(new Command<>(ServerCommandType.HERO_POWER, targetable));
        else if (caller instanceof Card)
            client.request(new Command<>(ServerCommandType.PLAY, caller, targetable));
    }
}