package tttHttp.DAO;

import tttHttp.models.Game;
import tttHttp.models.Point;

import java.util.List;

public interface GameDAO {

    Game getGame(int gameId);

    Game addPlayerToGame(int playerId);

    Game makeMove(int playerId, int gameId, Point move);

    Game winnerMove(int playerId, int gameId, Point move, List<Point> winningCombination);

    Game drawMove(int playerId, int gameId, Point move);

    Game setSurrendered(int playerId, int gameId);
}
