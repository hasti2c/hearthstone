package commands.types;

public enum ServerCommandType implements CommandType {
    //TODO add to client: DELETE, DESELECT
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
    CREATE_GAME,
    DECK_READER,
    START_GAME,
    PLAY,
    HERO_POWER,
    ATTACK,
    END_TURN;

    private static final ServerCommandType[] gameCommands = {PLAY, HERO_POWER, END_TURN, ATTACK, START_GAME};

    public static ServerCommandType[] getGameCommands() {
        return gameCommands;
    }
}
