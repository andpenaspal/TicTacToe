package tttHttp.DAO.exceptions;

public class DAOInvalidMoveException extends Exception{
    public DAOInvalidMoveException(String message, Throwable cause) {
        super(message, cause);
    }
    public DAOInvalidMoveException(){
        super();
    }
}
