package tttHttp.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class TokenManager {
    private TokenManager() {}

    public static String tokenGenerator(int size) {
        String generatedString = RandomStringUtils.randomAlphanumeric(size);
        return generatedString;
    }

    public static boolean validateToken(String inputToken, String savedToken) {
        if(inputToken.equals(savedToken)) {
            return true;
        }else {
            return false;
        }
    }
}
