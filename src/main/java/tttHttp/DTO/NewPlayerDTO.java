package tttHttp.DTO;

public class NewPlayerDTO {
    private int playerId;
    private String playerName;
    private String playerToken;

    public NewPlayerDTO(){}

    public NewPlayerDTO(int playerId, String playerName, String playerToken) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.playerToken = playerToken;
    }

    //TODO: Check maybe only Getters needed. Same for all DTOs
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
}
