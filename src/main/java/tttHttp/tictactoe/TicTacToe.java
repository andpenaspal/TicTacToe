package tttHttp.tictactoe;

import tttHttp.models.Game;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TicTacToe {
    private int turn;
    private int turnCounter;
    private boolean winner;
    private boolean draw;
    private boolean surrendered;
    private int[][] board;

    public TicTacToe(Game game){
        turn = game.getTurn();
        turnCounter = game.getTurnCounter();
        winner = game.isWinner();
        draw = game.isDraw();
        surrendered = game.isSurrendered();
        board = game.getBoard();
    }

    public boolean makeMove(int playerNumber, int tileCol, int tileRow){
        board[tileCol][tileRow] = turn;
        winner = checkWinner(tileCol, tileRow);
        turn = (turn == 1? 2 : 1);
        turnCounter++;
        draw = checkDraw();
        return true;
    }

    public boolean isValidMove(int tileCol, int tileRow){
        if(tileCol > 2 || tileCol < 0) return false;
        if(tileRow > 2 || tileRow < 0) return false;
        if(board[tileCol][tileRow] != 0) return false;
        return true;
    }

    private boolean checkDraw() {
        return turnCounter > 9;
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
        return board[0][0] == board[1][1] && board[0][0] == board[2][2];
    }

    private boolean checkBackwardDiagonalWinner() {
        return board[0][2] == board[1][1] && board[0][2] == board[2][0];
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

}
