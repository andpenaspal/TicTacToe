package tttHttp.tictactoe;

import tttHttp.DTO.GameDTO;
import tttHttp.models.Game;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TicTacToe {
    private boolean gameStarted;
    private int turn;
    private int turnCounter;
    private boolean winner;
    private boolean draw;
    private boolean surrendered;
    private int[][] board;

    public TicTacToe(boolean gameStarted, int turn, int turnCounter, boolean winner, boolean draw, boolean surrendered, int[][] board) {
        this.gameStarted = gameStarted;
        this.turn = turn;
        this.turnCounter = turnCounter;
        this.winner = winner;
        this.draw = draw;
        this.surrendered = surrendered;
        this.board = board;
    }

    public boolean makeMove(int playerNumber, int tileCol, int tileRow){
        if(!gameStarted || (playerNumber != turn) || !isValidMove(tileCol, tileRow) || isWinner() || isDraw() || isSurrendered()) return false;
        board[tileCol][tileRow] = turn;
        winner = checkWinner(tileCol, tileRow);
        if(!winner) turn = (turn == 1? 2 : 1);
        turnCounter++;
        if(!winner) draw = checkDraw();
        return true;
    }

    public boolean isValidMove(int tileCol, int tileRow){
        if(tileCol >= board.length || tileCol < 0) return false;
        if(tileRow >= board[0].length || tileRow < 0) return false;
        if(board[tileCol][tileRow] != 0) return false;
        return true;
    }

    private boolean checkDraw() {
        return turnCounter >= (board.length * board[0].length);
    }

    private boolean checkWinner(int tileCol, int tileRow) {
        return checkVerticalWinner(tileCol)
                || checkHorizontalWinner(tileRow)
                || checkForwardDiagonalWinner()
                || checkBackwardDiagonalWinner();
    }

    private boolean checkVerticalWinner(int tileCol){
        return board[tileCol][0] == board[tileCol][1] && board[tileCol][0] == board[tileCol][2];
    }

    private boolean checkHorizontalWinner(int tileRow){
        return board[0][tileRow] == board[1][tileRow] && board[0][tileRow] == board[2][tileRow];
    }

    private boolean checkForwardDiagonalWinner(){
        return board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[1][1] != 0;
    }

    private boolean checkBackwardDiagonalWinner() {
        return board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[1][1] != 0;
    }

    public int getTurn() {
        return turn;
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

    public void setSurrendered(boolean surrendered) {
        this.surrendered = surrendered;
    }

    public GameDTO getGameDTO(){
        return new GameDTO(gameStarted, turn, turnCounter, winner, draw, surrendered, board);
    }

}
