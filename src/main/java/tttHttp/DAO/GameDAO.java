package tttHttp.DAO;

import tttHttp.models.Game;
import tttHttp.models.Player;
import tttHttp.models.Point;

import java.util.List;

public interface GameDAO extends DAO<Game, Integer>{
    Integer insertPlayerIntoGame(int playerId);

    void deletePlayerFromGame(Game game, Player player);
}
