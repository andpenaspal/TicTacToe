package tttHttp.httpExceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import tttHttp.DTO.ErrorMessageDTO;

@Provider
public class HTTPExceptionMapper implements ExceptionMapper<HTTPException> {
    @Override
    public Response toResponse(HTTPException exception) {
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(exception.getMessage(), exception.getExceptionCode());
        return Response
                .status(exception.getExceptionCode())
                .entity(errorMessageDTO)
                .build();
    }
}
