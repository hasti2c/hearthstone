package commands.types;

public enum ClientCommandType implements CommandType {
    RESULT,
    UPDATE_PLAYER,
    UPDATE_GAME,
    END_GAME,
    FILE_ERROR
}
