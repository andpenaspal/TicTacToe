package tttHttp.DAO;

import tttHttp.models.Player;

public interface PlayerDAO {

    Player getPlayer(int playerId);

    Player newPlayer(String playerName, String playerToken);
}
