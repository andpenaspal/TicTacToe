package tttHttp.DAO;

import tttHttp.models.Game;
import tttHttp.models.Point;

import java.util.List;

public interface GameDAO extends DAO<Game, Integer>{
    Integer save(int playerId);
}
