package tttHttp.DAO;

import tttHttp.DAO.exceptions.*;

public interface DAO<T, K> {

    T get(K k) throws DAOException, DAODataNotFoundException;

    void update(T t) throws DAODMLException, DAOException, DAOInvalidTurnException, DAOInvalidGameConditionsException, DAOInvalidMoveException;
}
