package tttHttp.DTO;

import tttHttp.models.Point;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GameDTO {
    private int gameId;
    private int playerNumber;
    private String remotePlayerName;
    private boolean gameStarted;
    private int turn;
    private int turnCounter;
    private boolean winner;
    private boolean draw;
    private boolean surrendered;
    private int[][] board;
    private List<Point> winningCombination;

    public GameDTO(int gameId, int playerNumber, String remotePlayerName, boolean gameStarted, int turn, int turnCounter, boolean winner,
                   boolean draw, boolean surrendered, int[][] board, List<Point> winningCombination) {
        this.gameId = gameId;
        this.playerNumber = playerNumber;
        this.remotePlayerName = remotePlayerName;
        this.gameStarted = gameStarted;
        this.turn = turn;
        this.turnCounter = turnCounter;
        this.winner = winner;
        this.draw = draw;
        this.surrendered = surrendered;
        this.board = board;
        this.winningCombination = winningCombination;
    }

    //Used in TicTacToe.class for JUnit Tests
    public GameDTO(boolean gameStarted, int turn, int turnCounter, boolean winner, boolean draw, boolean surrendered, int[][] board,
                   List<Point> winningCombination) {
        this(0, 0, null, gameStarted, turn, turnCounter, winner, draw, surrendered, board, winningCombination);
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

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public void setTurnCounter(int turnCounter) {
        this.turnCounter = turnCounter;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public boolean isSurrendered() {
        return surrendered;
    }

    public void setSurrendered(boolean surrendered) {
        this.surrendered = surrendered;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public List<Point> getWinningCombination() {
        return winningCombination;
    }

    public void setWinningCombination(List<Point> winningCombination) {
        this.winningCombination = winningCombination;
    }

    //For JUnit Tests
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameDTO gameDTO = (GameDTO) o;
        return gameId == gameDTO.gameId && playerNumber == gameDTO.playerNumber && gameStarted == gameDTO.gameStarted && turn == gameDTO.turn && turnCounter == gameDTO.turnCounter && winner == gameDTO.winner && draw == gameDTO.draw && surrendered == gameDTO.surrendered && Objects.equals(remotePlayerName, gameDTO.remotePlayerName) && Arrays.deepEquals(board, gameDTO.board);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(gameId, playerNumber, remotePlayerName, gameStarted, turn, turnCounter, winner, draw, surrendered);
        result = 31 * result + Arrays.deepHashCode(board);
        return result;
    }
}
