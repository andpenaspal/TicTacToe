package tttHttp.controllers;

import tttHttp.DAO.DAOManager;
import tttHttp.DAO.MySQL.MySQLDAOManager;
import tttHttp.DAO.exceptions.*;
import tttHttp.DTO.GameDTO;
import tttHttp.httpExceptions.HTTPException;
import tttHttp.httpExceptions.HttpExceptionManager;
import tttHttp.models.Game;
import tttHttp.models.Player;
import tttHttp.models.Point;
import tttHttp.tictactoe.TicTacToe;
import tttHttp.utils.ConfigurationManager;
import tttHttp.utils.ExceptionsEnum;

import java.util.Properties;

public class GameController {

    //TODO: export and upload DDBB, do the ERD. Take away the gameMoveId (not used) and put as PK col-row

    private DAOManager daoManager;

    public GameController(){
        Properties mySQLConfig = ConfigurationManager.getProperties();

        String host = mySQLConfig.getProperty("host");
        int port = Integer.parseInt(mySQLConfig.getProperty("port"));
        String database = mySQLConfig.getProperty("database");
        String username = mySQLConfig.getProperty("username");
        String password = mySQLConfig.getProperty("password");

        try {
            daoManager = new MySQLDAOManager(host, port, database, username, password);
        } catch (ClassNotFoundException | DAOException throwables) {
            //TODO: Log
            throw new HTTPException(ExceptionsEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public GameDTO getGame(int playerId, int gameId, String playerToken){
        Game game;
        GameDTO gameDTO;

        isAuthenticatedAndAuthorized(playerId, playerToken, gameId);

        game = getGameFromDAO(gameId);

        gameDTO = getFullGameDTO(playerId, game);

        closeConnection();

        return gameDTO;
    }

    public GameDTO newGame(int playerId, String playerToken){
        Game game;
        GameDTO gameDTO;

        isAuthenticated(playerId, playerToken);

        int newGameId = 0;

        try {
            newGameId = daoManager.getGameDAO().insertPlayerIntoGame(playerId);
        } catch (DAOException | DAODMLException e) {
            //TODO: Log
            HttpExceptionManager.handleExceptions(e);
        }

        game = getGameFromDAO(newGameId);

        gameDTO = getFullGameDTO(playerId, game);

        closeConnection();

        return gameDTO;
    }

    public GameDTO makeMove(int playerId, String playerToken, int gameId, Point gameMove){
        Game game;
        TicTacToe ticTacToe;
        GameDTO gameDTO;

        isAuthenticatedAndAuthorized(playerId, playerToken, gameId);

        game = getGameFromDAO(gameId);

        ticTacToe = ticTacToeMakeMove(playerId, gameMove, game);

        game.updateGame(ticTacToe.getTurn(), ticTacToe.getTurnCounter(), ticTacToe.isWinner(), ticTacToe.isDraw(), ticTacToe.getBoard(),
                gameMove, ticTacToe.getWinningCombination());

        try {
            daoManager.getGameDAO().update(game);
        } catch (DAODMLException | DAOException | DAOInvalidTurnException | DAOInvalidGameConditionsException | DAOInvalidMoveException e) {
            //TODO: Log
            HttpExceptionManager.handleExceptions(e);
        }

        gameDTO = getFullGameDTO(playerId, game);

        closeConnection();

        return gameDTO;
    }

    private TicTacToe ticTacToeMakeMove(int playerId, Point gameMove, Game game) {
        TicTacToe ticTacToe;
        ticTacToe = new TicTacToe(game.getPlayer1Id(), game.getPlayer2Id(), game.isGameStarted(), game.getTurn(),
                game.getTurnCounter(), game.isWinner(), game.isDraw(), game.isSurrendered(), game.getBoard(), game.getWinningCombination());

        checkGameConditions(playerId, ticTacToe);
        isValidMove(gameMove, ticTacToe);

        ticTacToe.makeMove(playerId, gameMove.getMoveCol(), gameMove.getMoveRow());
        return ticTacToe;
    }

    private void isValidMove(Point gameMove, TicTacToe ticTacToe){
        if(!ticTacToe.isValidMove(gameMove.getMoveCol(), gameMove.getMoveRow())){
            throw new HTTPException(ExceptionsEnum.NO_VALID_MOVE);
        }
    }

    public GameDTO surrender(int playerId, String playerToken, int gameId) {
        Player player;
        Game game;
        TicTacToe ticTacToe;
        GameDTO gameDTO;

        player = isAuthenticatedAndAuthorized(playerId, playerToken, gameId);

        game = getGameFromDAO(gameId);

        ticTacToe = new TicTacToe(game.getPlayer1Id(), game.getPlayer2Id(), game.isGameStarted(), game.getTurn(),
                game.getTurnCounter(), game.isWinner(), game.isDraw(), game.isSurrendered(), game.getBoard(), game.getWinningCombination());

        checkGameConditions(playerId, ticTacToe);

        game.setSurrendered(true);
        game.setTurn(playerId);

        try {
            daoManager.getGameDAO().deletePlayerFromGame(game, player);
        } catch (DAOException | DAOInvalidGameConditionsException | DAOInvalidMoveException | DAOInvalidTurnException | DAODMLException e) {
            //TODO: Log
            HttpExceptionManager.handleExceptions(e);
        }

        gameDTO = getFullGameDTO(playerId, game);

        closeConnection();

        return gameDTO;
    }

    private Game getGameFromDAO(int gameId){
        Game game = null;
        try {
            game = daoManager.getGameDAO().get(gameId);
        } catch (DAOException | DAODataNotFoundException e) {
            //TODO: Log
            HttpExceptionManager.handleExceptions(e);
        }
        return game;
    }

    private Player isAuthenticatedAndAuthorized(int playerId, String playerToken, int gameId){
        Player playerInfo;

        playerInfo= isAuthenticated(playerId, playerToken);
        isAuthorized(playerInfo, gameId);
        return playerInfo;
    }

    private Player isAuthenticated(int playerId, String playerToken){
        PlayerController playerController = new PlayerController();
        return playerController.getPlayer(playerId, playerToken);
    }

    private void isAuthorized(Player player, int gameId){
        if(!player.getGamesIds().contains(gameId)) throw new HTTPException(ExceptionsEnum.NO_AUTHORIZED);
    }

    private void checkGameConditions(int playerId, TicTacToe ticTacToe){
        if(!ticTacToe.checkGameConditions(playerId)) throw new HTTPException(ExceptionsEnum.INVALID_GAME_CONDITIONS);
    }

    private GameDTO getFullGameDTO(int playerId, Game game){
        int playerNumber = game.getPlayer1Id() == playerId? 1 : 2;
        String remotePlayerName = playerNumber == 1? game.getPlayer2Name() : game.getPlayer1Name();

        game.setBoard(anonymizeBoard(game.getPlayer1Id(), game.getPlayer2Id(), game.getBoard()));
        game.setTurn(anonymizeTurn(game.getPlayer1Id(), game.getTurn()));

        return new GameDTO(game.getGameId(), playerNumber, remotePlayerName, game.isGameStarted(), game.getTurn(),
                game.getTurnCounter(), game.isWinner(), game.isDraw(), game.isSurrendered(), game.getBoard(), game.getWinningCombination());
    }

    private int[][] anonymizeBoard(int player1Id, int player2Id, int[][] board){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(board[i][j] == player1Id){
                    board[i][j] = 1;
                }else if(board[i][j] == player2Id) {
                    board[i][j] = 2;
                }
            }
        }
        return board;
    }

    private int anonymizeTurn(int player1Id, int turn){
        return turn == player1Id? 1 : 2;
    }

    private void closeConnection(){
        try {
            daoManager.closeConnection();
        } catch (DAOException e) {
            HttpExceptionManager.handleExceptions(e);
        }
    }

    public static void main(String[] args) {
        GameController gameController = new GameController();
        gameController.getGame(1, 1, "Token1");
    }
}
