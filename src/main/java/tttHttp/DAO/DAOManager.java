package tttHttp.DAO;

import tttHttp.DAO.exceptions.DAOException;

import java.sql.SQLException;

public interface DAOManager {

    PlayerDAO getPlayerDAO();

    GameDAO getGameDAO();

    void closeConnection() throws DAOException;
}
