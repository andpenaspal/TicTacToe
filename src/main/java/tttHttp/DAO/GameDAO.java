package tttHttp.DAO;

import tttHttp.DAO.exceptions.*;
import tttHttp.models.Game;
import tttHttp.models.Point;

import java.util.List;

public interface GameDAO {

    Game getGame(int gameId) throws DAOException, DAODataNotFoundException;

    Game addPlayerToGame(int playerId) throws DAOException, DAODMLException, DAODataNotFoundException;

    Game makeMove(int playerId, int gameId, Point move) throws DAOException, DAODMLException, DAODataNotFoundException, DAOInvalidTurnException, DAOInvalidGameConditionsException, DAOInvalidMoveException;

    Game winnerMove(int playerId, int gameId, Point move, List<Point> winningCombination) throws DAOException, DAODataNotFoundException, DAOInvalidTurnException, DAOInvalidGameConditionsException, DAOInvalidMoveException, DAODMLException;

    Game drawMove(int playerId, int gameId, Point move) throws DAOException, DAODataNotFoundException, DAOInvalidTurnException, DAOInvalidGameConditionsException, DAOInvalidMoveException, DAODMLException;

    Game setSurrendered(int playerId, int gameId) throws DAOException, DAODataNotFoundException, DAOInvalidTurnException, DAOInvalidGameConditionsException, DAOInvalidMoveException, DAODMLException;
}
