package tttHttp.models;

import java.util.Arrays;
import java.util.List;

public class Game {
    private int gameId;
    private int player1Id;
    private String player1Name;
    private Integer player2Id;
    private String player2Name;
    private boolean gameStarted;
    private int turn;
    private int turnCounter;
    private boolean winner;
    private boolean draw;
    private boolean surrendered;
    private int[][] board;
    private Point lastInserted;
    private List<Point> winningCombination;

    public Game(int gameId, int player1Id, String player1Name, Integer player2Id, String player2Name, boolean gameStarted, int turn,
                int turnCounter, boolean winner, boolean draw, boolean surrendered, int[][] board,
                Point lastInserted, List<Point> winningCombination) {
        this.gameId = gameId;
        this.player1Id = player1Id;
        this.player1Name = player1Name;
        this.player2Id = player2Id;
        this.player2Name = player2Name;
        this.gameStarted = gameStarted;
        this.turn = turn;
        this.turnCounter = turnCounter;
        this.winner = winner;
        this.draw = draw;
        this.surrendered = surrendered;
        this.board = board;
        this.lastInserted = lastInserted;
        this.winningCombination = winningCombination;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public Integer getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(Integer player2Id) {
        this.player2Id = player2Id;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
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

    public Point getLastInserted() {
        return lastInserted;
    }

    public void setLastInserted(Point lastInserted) {
        this.lastInserted = lastInserted;
    }

    public List<Point> getWinningCombination() {
        return winningCombination;
    }

    public void setWinningCombination(List<Point> winningCombination) {
        this.winningCombination = winningCombination;
    }

    public void updateGame(int turn, int turnCounter, boolean winner, boolean draw, int[][] board, Point lastInserted,
                           List<Point> winningCombination){
        this.turn = turn;
        this.turnCounter = turnCounter;
        this.winner = winner;
        this.draw = draw;
        this.board = board;
        this.lastInserted = lastInserted;
        this.winningCombination = winningCombination;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameId=" + gameId +
                ", player1Id=" + player1Id +
                ", player1Name='" + player1Name + '\'' +
                ", player2Id=" + player2Id +
                ", player2Name='" + player2Name + '\'' +
                ", gameStarted=" + gameStarted +
                ", turn=" + turn +
                ", turnCounter=" + turnCounter +
                ", winner=" + winner +
                ", draw=" + draw +
                ", surrendered=" + surrendered +
                ", board=" + Arrays.deepToString(board) +
                ", lastInserted=" + lastInserted +
                ", winningCombination=" + winningCombination +
                '}';
    }
}
