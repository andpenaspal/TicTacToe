package tttHttp.utils;

import java.io.*;
import java.util.Properties;

public class ConfigurationManager {

    private static Properties properties = null;

    private ConfigurationManager() {
        properties = new Properties();
        try {
            InputStreamReader is = new InputStreamReader(getClass().getResourceAsStream("/config.properties"));

            properties.load(is);
        } catch (FileNotFoundException e) {
            System.out.println("Configuration file (config.properties) not found");
        } catch (IOException e) {
            System.out.println("Error loading Configuration file" + e.getStackTrace());
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
