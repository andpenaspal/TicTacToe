package tttHttp.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import tttHttp.httpExceptions.HTTPException;
import tttHttp.httpExceptions.HttpExceptionManager;

import java.io.IOException;

public class JsonUtils {
    private static ObjectMapper myObjectMapper = defaultObjectMapper();

    private static ObjectMapper defaultObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return om;
    }

    public static String getJsonValue(String jsonSrc, String propertyName){
        String value = null;
        try {
            value = myObjectMapper.readTree(jsonSrc).get(propertyName).asText();
        } catch (IOException e) {
            //TODO: Log
            throw new HTTPException(ExceptionsEnum.INVALID_INPUT);
        }
        return value;
    }
}
