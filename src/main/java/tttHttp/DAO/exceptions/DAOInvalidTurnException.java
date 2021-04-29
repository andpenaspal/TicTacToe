package tttHttp.DAO.exceptions;

public class DAOInvalidTurnException extends Exception{
    public DAOInvalidTurnException(String message, Throwable cause) {
        super(message, cause);
    }
    public DAOInvalidTurnException(){
        super();
    }
}
