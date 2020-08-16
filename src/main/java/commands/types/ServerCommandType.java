package commands.types;

public enum ServerCommandType implements CommandType {
    //TODO add to client: DESELECT
    SIGN_UP,
    LOGIN,
    EXIT,
    LOGOUT,
    DELETE,
    SELECT,
    DESELECT,
    ADD_CARD,
    ADD_DECK,
    REMOVE_CARD,
    REMOVE_DECK,
    MOVE,
    RENAME,
    BUY,
    SELL,
    JOIN_GAME,
    START_GAME,
    LEAVE_GAME,
    END_TURN,
    PLAY,
    HERO_POWER,
    ATTACK;

    private static final ServerCommandType[] gameCommands = {PLAY, HERO_POWER, END_TURN, ATTACK, START_GAME};

    public static ServerCommandType[] getGameCommands() {
        return gameCommands;
    }
}
