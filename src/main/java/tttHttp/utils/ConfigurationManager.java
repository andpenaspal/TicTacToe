package tttHttp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tttHttp.httpExceptions.HTTPException;

import java.io.*;
import java.util.Properties;

public class ConfigurationManager {

    private static Properties properties = null;
    private final Logger LOG = LoggerFactory.getLogger(ConfigurationManager.class);

    private ConfigurationManager() {
        properties = new Properties();
        try {
            InputStreamReader is = new InputStreamReader(getClass().getResourceAsStream("/config.properties"));

            properties.load(is);
        } catch (FileNotFoundException e) {
            LOG.error("Configuration File Properties Not Found", e);
            throw new HTTPException(ExceptionsEnum.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            LOG.error("I/O Exception trying to load Configuration File", e);
            throw new HTTPException(ExceptionsEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public static Properties getProperties() {
        if(properties == null) {
            new ConfigurationManager();
        }
        return properties;
    }

    //Helper to create the Config File
    /*
	public static void main(String[] args) {
		Properties p = new Properties();

		try {
			OutputStream os = new FileOutputStream("./src/main/resources/config.properties");

			p.setProperty("host", "localhost");
			p.setProperty("database", "tictactoe");
			p.setProperty("username", "root");
			p.setProperty("password", "");

			p.store(os, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
     */

}
