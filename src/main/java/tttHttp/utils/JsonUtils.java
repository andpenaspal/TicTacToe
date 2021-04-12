package tttHttp.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
            //TODO: If property bad written (or none), nullpointer exception
            e.printStackTrace();
        }
        return value;
    }
}
