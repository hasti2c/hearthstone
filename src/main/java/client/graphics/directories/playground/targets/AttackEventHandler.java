package client.graphics.directories.playground.targets;

import client.graphics.directories.playground.*;
import commands.*;
import elements.abilities.targets.*;
import javafx.scene.*;

import static commands.types.ServerCommandType.*;

public class AttackEventHandler extends TargetEventHandler {
    private final PlayGround playGround;
    private final GamePlayerGraphics gamePlayer;

    public AttackEventHandler(GamePlayerGraphics gamePlayer, Targetable targetable, Node node) {
        super(targetable, node);
        this.gamePlayer = gamePlayer;
        playGround = gamePlayer.getPlayGround();
        initialize();
    }

    @Override
    protected void setSelectedTargetable(Targetable targetable) {
        gamePlayer.setSelectedAttackable((Attackable) targetable);
    }

    @Override
    protected boolean isEnough() {
        return playGround.getCurrentCharacter() != gamePlayer;
    }

    @Override
    protected void deselectedMode() {
        System.out.println("deselected mode");
        playGround.getCurrentCharacter().attackMode();
        playGround.getCurrentCharacter().getOpponent().attackMode();
    }

    @Override
    protected void oneSelectedMode() {
        System.out.println("one selected mode: " + targetable);
        playGround.getCurrentCharacter().defenseMode((Attackable) targetable);
        playGround.getCurrentCharacter().getOpponent().defenseMode((Attackable) targetable);
    }

    @Override
    protected void doAction() {
        System.out.println("do action: " + gamePlayer.getSelectedAttackable() + " " + gamePlayer.getOpponent().getSelectedAttackable());
        gamePlayer.getClient().request(new Command<>(ATTACK, gamePlayer.getOpponent().getSelectedAttackable(), gamePlayer.getSelectedAttackable()));
    }
}
