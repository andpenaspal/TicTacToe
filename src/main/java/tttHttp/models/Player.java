package tttHttp.models;

import java.util.ArrayList;
import tttHttp.DTO.NewPlayerDTO;
import tttHttp.DTO.PlayerDTO;

import java.util.List;

public class Player {
    private int playerId;
    private String playerName;
    private String playerToken;
    private List<Integer> gamesIds;

    public Player(int playerId, String playerName, String playerToken, List<Integer> gamesIds) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.playerToken = playerToken;
        this.gamesIds = gamesIds;
    }

    public Player(String playerName, String playerToken){
        this(0, playerName, playerToken, new ArrayList<>());
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

    public String getPlayerToken() {
        return playerToken;
    }

    public void setPlayerToken(String playerToken) {
        this.playerToken = playerToken;
    }

    public List<Integer> getGamesIds() {
        return gamesIds;
    }

    public void setGamesIds(List<Integer> gamesIds) {
        this.gamesIds = gamesIds;
    }
}
