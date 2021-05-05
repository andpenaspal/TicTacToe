package tttHttp.DAO.MySQL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tttHttp.DAO.DAOManager;
import tttHttp.DAO.GameDAO;
import tttHttp.DAO.PlayerDAO;
import tttHttp.DAO.exceptions.DAOException;
import tttHttp.controllers.PlayerController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDAOManager implements DAOManager {

    private PlayerDAO players = null;
    private GameDAO games = null;

    private Connection connection;
    private final Logger LOG = LoggerFactory.getLogger(MySQLDAOManager.class);

    public MySQLDAOManager(String host, int port, String database, String username, String password) throws DAOException,
            ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        } catch (SQLException throwables) {
            LOG.error("Error trying to connect to the DDBB", throwables);
            throw new DAOException("Problem trying to connect to the DDBB", throwables);
        }
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
            LOG.error("Error trying to close the Connection to the DDBB", throwables);
            throw new DAOException("Problem trying to close the connection", throwables);
        }
    }
}
