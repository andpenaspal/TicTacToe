package tttHttp.DTO;

import java.util.List;

//TODO: Gives a sorted Json, check if it affects JsonSchema
public class PlayerDTO {
    private int playerId;
    private String playerName;
    private List<Integer> gamesIds;

    public PlayerDTO(){}

    public PlayerDTO(int playerId, String playerName, List<Integer> gamesIds) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.gamesIds = gamesIds;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public List<Integer> getGamesIds() {
        return gamesIds;
    }

    public void setGamesIds(List<Integer> gamesIds) {
        this.gamesIds = gamesIds;
    }
}
