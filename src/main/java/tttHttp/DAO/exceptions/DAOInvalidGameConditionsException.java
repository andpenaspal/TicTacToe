package tttHttp.DAO.exceptions;

public class DAOInvalidGameConditionsException extends Exception{
    public DAOInvalidGameConditionsException(String message, Throwable cause) {
        super(message, cause);
    }
    public DAOInvalidGameConditionsException(){
        super();
    }
}
