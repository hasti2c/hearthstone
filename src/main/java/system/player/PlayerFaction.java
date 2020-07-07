package system.player;

public enum PlayerFaction {
    FRIENDLY (0),
    ENEMY (1);

    private final int playerNumber;

    PlayerFaction(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }
}
