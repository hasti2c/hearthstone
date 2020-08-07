package commands.types;

public enum ServerCommandType implements CommandType {
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
    PLAY,
    DECK_READER,
    HERO_POWER,
    END_TURN,
    CREATE_GAME,
    START_GAME,
    ATTACK;

    private static final ServerCommandType[] gameCommands = {PLAY, HERO_POWER, END_TURN, ATTACK, START_GAME};

    public static ServerCommandType[] getGameCommands() {
        return gameCommands;
    }
}
