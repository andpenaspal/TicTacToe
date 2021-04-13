package tttHttp.DAO;

import tttHttp.models.Player;

public interface PlayerDAO extends DAO<Player, Integer>{
    Integer save(Player player);
}
