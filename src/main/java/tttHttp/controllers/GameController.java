package tttHttp.controllers;

import tttHttp.DAO.DAOManager;
import tttHttp.DAO.MySQL.MySQLDAOManager;
import tttHttp.DAO.exceptions.DAODMLException;
import tttHttp.DAO.exceptions.DAODataNotFoundException;
import tttHttp.DAO.exceptions.DAOException;
import tttHttp.DTO.GameDTO;
import tttHttp.models.Game;
import tttHttp.models.Player;
import tttHttp.models.Point;
import tttHttp.tictactoe.TicTacToe;
import tttHttp.utils.ConfigurationManager;

import java.sql.SQLException;
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
        } catch (SQLException | ClassNotFoundException throwables) {
            //TODO
            throwables.printStackTrace();
        }
    }

    public GameDTO getGame(int playerId, int gameId, String playerToken){
        isAuthenticatedAndAuthorized(playerId, playerToken, gameId);

        Game game = null;
        //TODO mirar excepciones del mysql
        try {
            game = daoManager.getGameDAO().getGame(gameId);
        } catch (DAOException e) {
            e.printStackTrace();
        } catch (DAODataNotFoundException e) {
            e.printStackTrace();
        }

        GameDTO gameDTO = getFullGameDTO(playerId, game);

        return gameDTO;
    }

    public GameDTO newGame(int playerId, String playerToken){
        isAuthenticated(playerId, playerToken);

        Game game = null;
        //TODO mirar excepciones del mysql
        try {
            game = daoManager.getGameDAO().addPlayerToGame(playerId);
        } catch (DAOException e) {
            e.printStackTrace();
        } catch (DAODMLException e) {
            e.printStackTrace();
        } catch (DAODataNotFoundException e) {
            e.printStackTrace();
        }

        GameDTO gameDTO = getFullGameDTO(playerId, game);

        return gameDTO;
    }

    public GameDTO makeMove(int playerId, String playerToken, int gameId, Point gameMove){
        isAuthenticatedAndAuthorized(playerId, playerToken, gameId);

        Game game = null;
        //TODO: mirar excepciones del Mysql
        try {
            game = daoManager.getGameDAO().getGame(gameId);
        } catch (DAOException e) {
            e.printStackTrace();
        } catch (DAODataNotFoundException e) {
            e.printStackTrace();
        }

        TicTacToe ticTacToe = new TicTacToe(game.getPlayer1Id(), game.getPlayer2Id(), game.isGameStarted(), game.getTurn(),
                game.getTurnCounter(), game.isWinner(), game.isDraw(), game.isSurrendered(), game.getBoard(), game.getWinningCombination());

        checkGameConditions(playerId, ticTacToe);
        isValidMove(gameMove, ticTacToe);

        ticTacToe.makeMove(playerId, gameMove.getMoveCol(), gameMove.getMoveRow());

        game = storeMove(playerId, gameId, gameMove, ticTacToe);

        GameDTO gameDTO = getFullGameDTO(playerId, game);

        return gameDTO;
    }

    private void isValidMove(Point gameMove, TicTacToe ticTacToe){
        if(!ticTacToe.isValidMove(gameMove.getMoveCol(), gameMove.getMoveRow())){

        }
        //TODO check and exception
    }

    //TODO: Exceptions?
    private Game storeMove(int playerId, int gameId, Point gameMove, TicTacToe ticTacToe) {
        try {
            if(ticTacToe.isWinner()){
                return daoManager.getGameDAO().winnerMove(playerId, gameId, gameMove, ticTacToe.getWinningCombination());
            }else if(ticTacToe.isDraw()){
                return daoManager.getGameDAO().drawMove(playerId, gameId, gameMove);
            }else{
                return daoManager.getGameDAO().makeMove(playerId, gameId, gameMove);
            }
        } catch (DAOException e) {
            e.printStackTrace();
        } catch (DAODataNotFoundException e) {
            e.printStackTrace();
        } catch (DAODMLException e) {
            e.printStackTrace();
        }
        //TODO: Sacar, es solo para que compile. Al soltar nueva excepcion en los catch no hara falta
        return null;
    }

    public GameDTO surrender(int playerId, String playerToken, int gameId) {
        isAuthenticatedAndAuthorized(playerId, playerToken, gameId);

        Game game = null;
        try {
            game = daoManager.getGameDAO().getGame(gameId);
        } catch (DAOException e) {
            e.printStackTrace();
        } catch (DAODataNotFoundException e) {
            e.printStackTrace();
        }

        TicTacToe ticTacToe = new TicTacToe(game.getPlayer1Id(), game.getPlayer2Id(), game.isGameStarted(), game.getTurn(),
                game.getTurnCounter(), game.isWinner(), game.isDraw(), game.isSurrendered(), game.getBoard(), game.getWinningCombination());

        checkGameConditions(playerId, ticTacToe);

        try {
            game = daoManager.getGameDAO().setSurrendered(playerId, gameId);
        } catch (DAOException e) {
            e.printStackTrace();
        } catch (DAODataNotFoundException e) {
            e.printStackTrace();
        }

        GameDTO gameDTO = getFullGameDTO(playerId, game);

        return gameDTO;
    }

    private void isAuthenticatedAndAuthorized(int playerId, String playerToken, int gameId){
        Player playerInfo = isAuthenticated(playerId, playerToken);
        isAuthorized(playerInfo, gameId);
    }

    private Player isAuthenticated(int playerId, String playerToken){
        PlayerController playerController = new PlayerController();
        Player playerInfo = playerController.getPlayer(playerId, playerToken);
        return playerInfo;
    }

    private void isAuthorized(Player player, int gameId){
        if(!player.getGamesIds().contains(gameId)){
            //TODO: not authorized
        }
    }

    private void checkGameConditions(int playerId, TicTacToe ticTacToe){
        if(!ticTacToe.checkGameConditions(playerId)){

        }
        //TODO check and exception
    }

    private GameDTO getFullGameDTO(int playerId, Game game){
        int playerNumber = game.getPlayer1Id() == playerId? 1 : 2;
        String remotePlayerName = playerNumber == 1? game.getPlayer1Name() : game.getPlayer2Name();

        GameDTO gameDTO = new GameDTO(game.getGameId(), playerNumber, remotePlayerName, game.isGameStarted(), game.getTurn(),
                game.getTurnCounter(), game.isWinner(), game.isDraw(), game.isSurrendered(), game.getBoard(), game.getWinningCombination());
        return gameDTO;
    }
}
