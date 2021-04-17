package tttHttp.httpExceptions;

import tttHttp.utils.ExceptionsEnum;

public class HttpExceptionManager {

    public static void handleExceptions(Exception exception) {
        Class clazz = exception.getClass();
        ExceptionsEnum exceptionsEnum;
        switch (clazz.toString()) {
            case "DAOInvalidMoveException":
                exceptionsEnum = ExceptionsEnum.NO_VALID_MOVE;
            case "DAOInvalidGameConditionsException":
                exceptionsEnum = ExceptionsEnum.INVALID_GAME_CONDITIONS;
            case "DAOInvalidTurnException":
                exceptionsEnum = ExceptionsEnum.NO_CORRECT_TURN;
            case "DAODataNotFoundException":
                exceptionsEnum = ExceptionsEnum.NOT_FOUND;
            case "ClassNotFoundException":
            case "DAOException":
            case "DAODMLException":
            default:
                exceptionsEnum = ExceptionsEnum.INTERNAL_SERVER_ERROR;
        }
        throw new HTTPException(exceptionsEnum);
    }
}
