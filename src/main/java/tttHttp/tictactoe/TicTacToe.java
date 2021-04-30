package tttHttp.tictactoe;

import tttHttp.DTO.GameDTO;
import tttHttp.httpExceptions.HTTPException;
import tttHttp.models.Game;
import tttHttp.models.Point;
import tttHttp.utils.ExceptionsEnum;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TicTacToe {
    private int player1Id;
    private Integer player2Id;
    private boolean gameStarted;
    private int turn;
    private int turnCounter;
    private boolean winner;
    private boolean draw;
    private boolean surrendered;
    private int[][] board;
    private List<Point> winningCombination;

    public TicTacToe(int player1Id, Integer player2Id, boolean gameStarted, int turn, int turnCounter, boolean winner, boolean draw,
                     boolean surrendered,
                     int[][] board,
                     List<Point> winningCombination) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.gameStarted = gameStarted;
        this.turn = turn;
        this.turnCounter = turnCounter;
        this.winner = winner;
        this.draw = draw;
        this.surrendered = surrendered;
        this.board = board;
        this.winningCombination = winningCombination;
    }

    //TODO: exceptions for TicTacToe on conditions and invalid move
    public boolean makeMove(int playerId, int tileCol, int tileRow){
        if(!hasStarted() || !checkGameConditions() || !isCorrectTurn(playerId) || !isValidMove(tileCol, tileRow)) return false;
        board[tileCol][tileRow] = turn;
        winner = checkWinner(tileCol, tileRow);
        if(!winner) turn = (turn == player1Id? player2Id : player1Id);
        turnCounter++;
        if(!winner) draw = checkDraw();
        return true;
    }

    public boolean hasStarted(){
        return gameStarted;
    }

    public boolean checkGameConditions(){
        return (!isWinner() && !isDraw() && !surrendered);
    }

    public boolean isCorrectTurn(int playerId){
        return (turn == playerId);
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
        boolean isWin = board[tileCol][0] == board[tileCol][1] && board[tileCol][0] == board[tileCol][2];
        if(isWin){
            for(int i = 0; i < 3; i++){
                winningCombination.add(new Point(tileCol, i));
            }
        }
        return isWin;
    }

    private boolean checkHorizontalWinner(int tileRow){
        boolean isWin = board[0][tileRow] == board[1][tileRow] && board[0][tileRow] == board[2][tileRow];
        if(isWin){
            for(int i = 0; i < 3; i++){
                winningCombination.add(new Point(i, tileRow));
            }
        }
        return isWin;
    }

    private boolean checkForwardDiagonalWinner(){
        boolean isWin = board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[1][1] != 0;
        if(isWin){
            for(int i = 0; i < 3; i++){
                winningCombination.add(new Point(i, i));
            }
        }
        return isWin;
    }

    private boolean checkBackwardDiagonalWinner() {
        boolean isWin = board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[1][1] != 0;
        if(isWin){
            for(int i = 0; i < 3; i++){
                winningCombination.add(new Point(i, 2-i));
            }
        }
        return isWin;
    }

    public void setSurrendered(boolean surrendered) {
        this.surrendered = surrendered;
    }

    public boolean isWinner() {
        return winner;
    }

    public boolean isDraw() {
        return draw;
    }

    public List<Point> getWinningCombination() {
        return winningCombination;
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(int player2Id) {
        this.player2Id = player2Id;
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

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public boolean isSurrendered() {
        return surrendered;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public void setWinningCombination(List<Point> winningCombination) {
        this.winningCombination = winningCombination;
    }

    //Method used only in JUnit Tests
    public GameDTO getGameDTOBasicInfo(){
        int[][] winningCombination2Dint = get2DIntFromListPoints(winningCombination);
        return new GameDTO(gameStarted, turn, turnCounter, winner, draw, surrendered, board, winningCombination2Dint);
    }

    //Helper function for getGameDTOBasicInfo
    private int[][] get2DIntFromListPoints(List<Point> winningCombination) {
        if(winningCombination.isEmpty() || winningCombination == null) return null;
        int[][] int2D = new int[3][2];
        Iterator<Point> iter = winningCombination.iterator();
        for(int i = 0; i < int2D.length; i++){
            if(iter.hasNext()){
                Point p = iter.next();
                int2D[i][0] = p.getMoveCol();
                int2D[i][1] = p.getMoveRow();
            }else{
                //TODO: Log Exception
            }
        }
        return int2D;
    }

}
