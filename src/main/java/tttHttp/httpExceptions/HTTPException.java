package tttHttp.httpExceptions;

import tttHttp.utils.ExceptionsEnum;

public class HTTPException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private int exceptionCode;

    public HTTPException(ExceptionsEnum excp){
        super(excp.getExceptionMessage());
        this.exceptionCode = excp.getExceptionCode();
    }

    public int getExceptionCode() {
        return exceptionCode;
    }
}

//TODO: Probar: Superclass "HTTPException" extends RuntimeException, maybe abstract class? or just normal and all the other extend them
// (not Runtime). One mapper catching the HTTPException. It is the same code, will get the info from the Enum
