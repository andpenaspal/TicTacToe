package tttHttp.DAO;

import tttHttp.models.Player;

public interface PlayerDAO extends DAO<Player, Integer>{
    Integer insert(Player player);

    void delete(Player player);
}
