package tttHttp.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tttHttp.httpExceptions.HTTPException;
import tttHttp.httpExceptions.HttpExceptionManager;

import java.io.IOException;

public class JsonUtils {
    private static ObjectMapper myObjectMapper = defaultObjectMapper();
    private final static Logger LOG = LoggerFactory.getLogger(JsonUtils.class);

    private static ObjectMapper defaultObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return om;
    }

    public static String getJsonValue(String jsonSrc, String propertyName){
        String value = null;
        try {
            //value = myObjectMapper.readTree(jsonSrc).get(propertyName).asText();
            JsonNode node = myObjectMapper.readTree(jsonSrc);
            if(node.has(propertyName) && !node.get(propertyName).isNull()){
                value = node.get(propertyName).asText();
            }else{
                LOG.error("JsonSrc {} missing or with NULL {}", jsonSrc, propertyName);
                throw new HTTPException(ExceptionsEnum.INVALID_INPUT);
            }
        } catch (IOException e) {
            LOG.error("I/O Error trying to extract the property '{}' from the JsonSrc '{}'", propertyName, jsonSrc, e);
            throw new HTTPException(ExceptionsEnum.INTERNAL_SERVER_ERROR);
        }
        return value;
    }
}
