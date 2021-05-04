package tttHttp.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tttHttp.DAO.DAOManager;
import tttHttp.DAO.MySQL.MySQLDAOManager;
import tttHttp.DAO.exceptions.*;
import tttHttp.DTO.NewPlayerDTO;
import tttHttp.DTO.PlayerDTO;
import tttHttp.httpExceptions.HTTPException;
import tttHttp.httpExceptions.HttpExceptionManager;
import tttHttp.models.Player;
import tttHttp.utils.ConfigurationManager;
import tttHttp.utils.ExceptionsEnum;
import tttHttp.utils.JsonUtils;
import tttHttp.utils.TokenManager;

import java.util.Properties;

public class PlayerController {

    private final Logger LOG = LoggerFactory.getLogger(PlayerController.class);
    private DAOManager daoManager;

    public PlayerController(){
        Properties mySQLConfig = ConfigurationManager.getProperties();

        String host = mySQLConfig.getProperty("host");
        int port = Integer.parseInt(mySQLConfig.getProperty("port"));
        String database = mySQLConfig.getProperty("database");
        String username = mySQLConfig.getProperty("username");
        String password = mySQLConfig.getProperty("password");

        try {
            daoManager = new MySQLDAOManager(host, port, database, username, password);
        } catch (ClassNotFoundException | DAOException throwables) {
            LOG.error("Error connecting to the DDBB", throwables);
            HttpExceptionManager.handleExceptions(throwables);
        }
    }

    public PlayerDTO getPlayerDTO(int playerId, String playerToken){
        Player playerInfo;
        PlayerDTO playerDTO;

        playerInfo = getPlayer(playerId, playerToken);

        playerDTO = new PlayerDTO(playerInfo.getPlayerId(), playerInfo.getPlayerName(), playerInfo.getGamesIds());

        return playerDTO;
    }

    //[public] Called by GameController.class. Return Player, not PlayerDTO => Internal Info. Closes Player Connection
    public Player getPlayer(int playerId, String playerToken){
        Player player = getPlayerFromDAO(playerId);

        closeConnection();

        isAuthenticated(playerToken, player.getPlayerToken());

        return player;
    }

    public NewPlayerDTO addNewPlayer(String jsonSrc){
        String newToken = TokenManager.tokenGenerator(50);
        String playerName = JsonUtils.getJsonValue(jsonSrc, "playerName");
        Player player = new Player(playerName, newToken);
        int newPlayerId = 0;
        NewPlayerDTO newPlayerDTO;

        isCorrectName(playerName);

        try {
            newPlayerId = daoManager.getPlayerDAO().insert(player);
        } catch (DAOException | DAODMLException e) {
            LOG.error("Error trying to insert the New Player '{}' in the DDBB", playerName, e);
            HttpExceptionManager.handleExceptions(e);
        }

        player = getPlayerFromDAO(newPlayerId);

        closeConnection();

        newPlayerDTO = new NewPlayerDTO(player.getPlayerId(), player.getPlayerName(), player.getPlayerToken());

        return newPlayerDTO;
    }

    public PlayerDTO updatePlayer(int playerId, String playerToken, String jsonSrc){
        Player player;
        PlayerDTO playerDTO;
        String playerName = JsonUtils.getJsonValue(jsonSrc, "playerName");

        isCorrectName(playerName);

        player = getAuthenticatedPlayerFromDAO(playerId, playerToken);
        player.setPlayerName(playerName);

        try {
            daoManager.getPlayerDAO().update(player);
        } catch (DAODMLException | DAOException | DAOInvalidGameConditionsException | DAOInvalidMoveException | DAOInvalidTurnException e) {
            LOG.error("Error trying to Update the Player '{}' in the DDBB", playerId, e);
            HttpExceptionManager.handleExceptions(e);
        }

        player = getPlayerFromDAO(playerId);

        closeConnection();

        playerDTO = new PlayerDTO(player.getPlayerId(), player.getPlayerName(), player.getGamesIds());

        return playerDTO;
    }

    public void deletePlayer(int playerId, String playerToken){
        Player player;

        player = getAuthenticatedPlayerFromDAO(playerId, playerToken);

        try {
            daoManager.getPlayerDAO().delete(player);
        } catch (DAOException | DAODMLException e) {
            LOG.error("Error trying to Delete the Player '{}' in the DDBB", playerId, e);
            HttpExceptionManager.handleExceptions(e);
        }

        closeConnection();
    }

    private Player getPlayerFromDAO(int playerId){
        if(playerId <= 0){
            LOG.warn("Error Requesting a Player with ID '{}'");
            throw new HTTPException(ExceptionsEnum.NOT_FOUND);
        }
        Player player = null;
        try {
            player = daoManager.getPlayerDAO().get(playerId);
        } catch (DAODataNotFoundException | DAOException e) {
            LOG.error("Error trying to Get the Player '{}' from the DDBB", playerId, e);
            HttpExceptionManager.handleExceptions(e);
        }
        return player;
    }

    private Player getAuthenticatedPlayerFromDAO(int playerId, String playerToken){
        Player player = getPlayerFromDAO(playerId);

        isAuthenticated(playerToken, player.getPlayerToken());

        return player;
    }

    private void isAuthenticated(String playerToken, String storedPlayerToken){
        if(playerToken == null || !TokenManager.validateToken(playerToken, storedPlayerToken)){
            LOG.warn("Player not Authenticated. Token provided '{}' vs Token stored '{}'", playerToken, storedPlayerToken);
            throw new HTTPException(ExceptionsEnum.NO_AUTHENTICATED);
        }
    }

    private void isCorrectName(String playerName){
        if(playerName.length() < 3 || playerName.length() > 10 || playerName == null){
            LOG.warn("Name provided '{}' not valid", playerName);
            throw new HTTPException(ExceptionsEnum.INVALID_INPUT);
        }
    }

    private void closeConnection(){
        try {
            daoManager.closeConnection();
        } catch (DAOException e) {
            LOG.error("Error trying to Close the Connection to the DDBB", e);
            HttpExceptionManager.handleExceptions(e);
        }
    }

}
