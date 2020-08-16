package system.game;

public enum GameEndingType {
    FRIENDLY_WIN (0, 1),
    TIE (-1, -1),
    FRIENDLY_LOSS (1, 0),
    NONE (-1, -1);

    private final int winnerIndex, loserIndex;
    GameEndingType(int winnerIndex, int loserIndex) {
        this.winnerIndex = winnerIndex;
        this.loserIndex = loserIndex;
    }

    public int getWinnerIndex() {
        return winnerIndex;
    }

    public int getLoserIndex() {
        return loserIndex;
    }
}
