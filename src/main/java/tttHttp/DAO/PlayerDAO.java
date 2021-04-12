package tttHttp.DAO;

import tttHttp.DAO.exceptions.DAODMLException;
import tttHttp.DAO.exceptions.DAODataNotFoundException;
import tttHttp.DAO.exceptions.DAOException;
import tttHttp.models.Player;

public interface PlayerDAO {

    Player getPlayer(int playerId) throws DAODataNotFoundException, DAOException;

    Player newPlayer(String playerName, String playerToken) throws DAOException, DAODataNotFoundException, DAODMLException;
}
