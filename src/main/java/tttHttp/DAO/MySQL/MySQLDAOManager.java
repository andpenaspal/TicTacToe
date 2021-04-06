package tttHttp.DAO.MySQL;

import tttHttp.DAO.DAOManager;
import tttHttp.DAO.GameDAO;
import tttHttp.DAO.PlayerDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDAOManager implements DAOManager {

    private PlayerDAO players = null;
    private GameDAO games = null;

    private Connection connection;

    public MySQLDAOManager(String host, String username, String password, String database) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, username, password);
    }

    @Override
    public PlayerDAO getPlayerDAO() {
        if(players == null){
            players = new MySQLPlayerDAO(connection);
        }
        return players;
    }

    @Override
    public GameDAO getGameDAO() {
        if(games == null){
            games = new MySQLGameDAO(connection);
        }
        return games;
    }
}
