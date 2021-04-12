package tttHttp.DAO.MySQL;

import tttHttp.DAO.DAOManager;
import tttHttp.DAO.GameDAO;
import tttHttp.DAO.PlayerDAO;
import tttHttp.DAO.exceptions.DAOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDAOManager implements DAOManager {

    private PlayerDAO players = null;
    private GameDAO games = null;

    private Connection connection;

    public MySQLDAOManager(String host, int port, String database, String username, String password) throws SQLException,
            ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
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

    public void closeConnection() throws DAOException {
        try {
            connection.close();
        } catch (SQLException throwables) {
            //TODO: Log
            throw new DAOException("Problem trying to close the connection", throwables);
        }
    }
}
