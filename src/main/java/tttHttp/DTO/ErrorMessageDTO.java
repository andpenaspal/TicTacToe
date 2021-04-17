package tttHttp.DTO;

public class ErrorMessageDTO {
    private int errorCode;
    private String errorMessage;

    public ErrorMessageDTO(){}

    public ErrorMessageDTO(String errorMessage, int errorCode){
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
