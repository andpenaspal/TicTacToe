package tttHttp.utilsTests;

import tttHttp.utils.ConfigurationManager;

import java.io.*;
import java.sql.*;
import java.util.Properties;

public class DDBBTestingDataLoader {
    public static void loadDDBBTestingData(String scriptSrc){
        Properties properties = ConfigurationManager.getProperties();

        Connection connection;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + properties.getProperty("host") + ":" + properties.getProperty("port")
                    + "/" + properties.getProperty("database"), properties.getProperty("username"), properties.getProperty("password"));
            readScript(scriptSrc, connection);
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void readScript(String scriptSrc, Connection connection){
        File script = new File(scriptSrc);
        BufferedReader br;
        StringBuffer sb = new StringBuffer();
        Statement statement = null;
        String line = null;
        try {
            br = new BufferedReader(new FileReader(script));

            connection.setAutoCommit(false);

            while((line = br.readLine()) != null){
                line = line.trim();
                if(line.startsWith("--")) continue;
                sb.append(line);
                if(line.endsWith(";")){
                    statement = connection.createStatement();
                    statement.execute(sb.toString());
                    sb.delete(0, sb.length());
                }
            }
            connection.commit();

            System.out.println("Script Loaded Successfully");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throwables.printStackTrace();
        }finally {
            try {
                if(statement != null){
                    statement.close();
                }
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
