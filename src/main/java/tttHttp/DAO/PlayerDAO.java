package tttHttp.DAO;

import tttHttp.DAO.exceptions.DAODMLException;
import tttHttp.DAO.exceptions.DAODataNotFoundException;
import tttHttp.DAO.exceptions.DAOException;
import tttHttp.models.Player;

public interface PlayerDAO extends DAO<Player, Integer>{
    Integer insert(Player player) throws DAOException, DAODMLException;

    void delete(Player player) throws DAOException, DAODMLException;
}
