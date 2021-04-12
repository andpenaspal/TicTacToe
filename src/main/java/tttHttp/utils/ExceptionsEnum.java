package tttHttp.utils;

public enum ExceptionsEnum {
    NO_AUTHENTICATED("The token sent is not valid for this Id", 401),
    NO_AUTHORIZED("The Player is not authorized to access this Game", 403),
    NO_CORRECT_TURN("The Player can only modify the Game in his/her Turn", 409),
    NO_PLAYER("No Player found with this Id", 404),
    INVALID_GAME_CONDITIONS("The conditions of the Game do no allow this action", 409),
    NO_VALID_MOVE("The Move is not a valid Move", 409),
    INVALID_INPUT("Invalid Json Input Format", 400),
    INTERNAL_SERVER_ERROR("Unexpected Error", 500);


    private String exceptionMessage;
    private int exceptionCode;

    ExceptionsEnum(String exceptionMessage, int exceptionCode){
        this.exceptionMessage = exceptionMessage;
        this.exceptionCode = exceptionCode;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public int getExceptionCode() {
        return exceptionCode;
    }
}
