package client.graphics.directories.playground.targets;

import client.graphics.directories.playground.CharacterGraphics;
import client.graphics.directories.playground.GamePlayerGraphics;
import client.graphics.directories.playground.PlayGround;
import commands.Command;
import elements.abilities.targets.Attackable;
import elements.abilities.targets.Targetable;
import javafx.scene.Node;
import system.player.GamePlayer;

import static commands.types.ServerCommandType.ATTACK;

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
        playGround.getCurrentCharacter().attackMode();
        playGround.getOtherCharacter().attackMode();
    }

    @Override
    protected void oneSelectedMode() {
        playGround.getCurrentCharacter().defenseMode((Attackable) targetable);
        playGround.getOtherCharacter().defenseMode((Attackable) targetable);
    }

    @Override
    protected void doAction() {
        gamePlayer.getClient().request(new Command<>(ATTACK, gamePlayer.getSelectedAttackable(), gamePlayer.getOpponent().getSelectedAttackable()));
        playGround.config();
    }
}
