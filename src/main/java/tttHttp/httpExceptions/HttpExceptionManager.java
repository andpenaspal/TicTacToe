package tttHttp.httpExceptions;

import tttHttp.DAO.exceptions.DAODataNotFoundException;
import tttHttp.DAO.exceptions.DAOInvalidGameConditionsException;
import tttHttp.DAO.exceptions.DAOInvalidMoveException;
import tttHttp.DAO.exceptions.DAOInvalidTurnException;
import tttHttp.utils.ExceptionsEnum;

public class HttpExceptionManager {

    public static void handleExceptions(Exception exception) {
        ExceptionsEnum exceptionsEnum;

        if(exception instanceof DAOInvalidMoveException){
            exceptionsEnum = ExceptionsEnum.NO_VALID_MOVE;
        }else if(exception instanceof DAOInvalidGameConditionsException){
            exceptionsEnum = ExceptionsEnum.INVALID_GAME_CONDITIONS;
        }else if(exception instanceof DAOInvalidTurnException){
            exceptionsEnum = ExceptionsEnum.NO_CORRECT_TURN;
        }else if(exception instanceof DAODataNotFoundException){
            exceptionsEnum = ExceptionsEnum.NOT_FOUND;
        }else {
            exceptionsEnum = ExceptionsEnum.INTERNAL_SERVER_ERROR;
        }

        throw new HTTPException(exceptionsEnum);
    }
}
