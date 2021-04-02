package tttHttp.DTO;

import java.util.Arrays;
import java.util.Objects;

public class GameDTO {
    private int gameId;
    private int playerNumber;
    private String remotePlayerName;
    private int turn;
    private int turnCounter;
    private boolean winner;
    private boolean draw;
    private boolean surrendered;
    private int[][] board;

    public GameDTO(int turn, int turnCounter, boolean winner, boolean draw, boolean surrendered, int[][] board) {
        this.turn = turn;
        this.turnCounter = turnCounter;
        this.winner = winner;
        this.draw = draw;
        this.surrendered = surrendered;
        this.board = board;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String getRemotePlayerName() {
        return remotePlayerName;
    }

    public void setRemotePlayerName(String remotePlayerName) {
        this.remotePlayerName = remotePlayerName;
    }

    public int getTurn() {
        return turn;
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public boolean isWinner() {
        return winner;
    }

    public boolean isDraw() {
        return draw;
    }

    public boolean isSurrendered() {
        return surrendered;
    }

    public int[][] getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameDTO gameDTO = (GameDTO) o;
        return gameId == gameDTO.gameId && playerNumber == gameDTO.playerNumber && turn == gameDTO.turn && turnCounter == gameDTO.turnCounter
                && winner == gameDTO.winner && draw == gameDTO.draw && surrendered == gameDTO.surrendered
                && Objects.equals(remotePlayerName, gameDTO.remotePlayerName) && Arrays.deepEquals(board, gameDTO.board);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(gameId, playerNumber, remotePlayerName, turn, turnCounter, winner, draw, surrendered);
        result = 31 * result + Arrays.hashCode(board);
        return result;
    }
}
