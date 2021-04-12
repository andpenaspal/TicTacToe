package tttHttp.controllers;

import tttHttp.DAO.DAOManager;
import tttHttp.DAO.MySQL.MySQLDAOManager;
import tttHttp.DAO.exceptions.DAODMLException;
import tttHttp.DAO.exceptions.DAODataNotFoundException;
import tttHttp.DAO.exceptions.DAOException;
import tttHttp.DTO.NewPlayerDTO;
import tttHttp.DTO.PlayerDTO;
import tttHttp.models.Player;
import tttHttp.utils.ConfigurationManager;
import tttHttp.utils.JsonUtils;
import tttHttp.utils.TokenManager;

import java.sql.SQLException;
import java.util.Properties;

public class PlayerController {

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
        } catch (SQLException | ClassNotFoundException throwables) {
            //TODO
            throwables.printStackTrace();
        }
    }

    public PlayerDTO getPlayerDTO(int playerId, String playerToken){
        if(playerToken == null); //TODO: exception? Mirar donde salta si tiene header null value, o si no tiene header. Creo que lo mejor
        // seria soltar una exception propia en el Json, cogerla aqui y soltar una mapeada

        Player playerInfo = null;
        try {
            playerInfo = daoManager.getPlayerDAO().getPlayer(playerId);
        } catch (DAODataNotFoundException e) {
            e.printStackTrace();
        } catch (DAOException e) {
            e.printStackTrace();
        }

        PlayerDTO playerDTO = null;

        if(isAuthenticated(playerToken, playerInfo.getPlayerToken())){
            playerDTO = playerInfo.getPlayerDTO();
        }else{
            //TODO: No Authenticated Exception, put return on if and exception on else
            System.out.println("Not authenticated");
        }

        closeConnection();
        return playerDTO;
    }

    //Called by GameController.class
    public Player getPlayer(int playerId, String playerToken){
        if(playerToken == null); //TODO: exception? Mirar donde salta si tiene header null value, o si no tiene header. Creo que lo mejor
        // seria soltar una exception propia en el Json, cogerla aqui y soltar una mapeada
        Player playerInfo = null;
        try {
            playerInfo = daoManager.getPlayerDAO().getPlayer(playerId);
        } catch (DAODataNotFoundException e) {
            e.printStackTrace();
        } catch (DAOException e) {
            e.printStackTrace();
        }


        closeConnection();

        if(isAuthenticated(playerToken, playerInfo.getPlayerToken())){
            return playerInfo;
        }else{
            //TODO: No Authenticated Exception, put return on if and exception on else
            throw new RuntimeException();
        }

        //return playerDTO;
    }

    public NewPlayerDTO addNewPlayer(String jsonSrc){
        String newToken = TokenManager.tokenGenerator(50);
        String playerName = JsonUtils.getJsonValue(jsonSrc, "playerName");
        Player newPlayerInfo = null;
        try {
            newPlayerInfo = daoManager.getPlayerDAO().newPlayer(playerName, newToken);
        } catch (DAOException e) {
            e.printStackTrace();
        } catch (DAODataNotFoundException e) {
            e.printStackTrace();
        } catch (DAODMLException e) {
            e.printStackTrace();
        }


        closeConnection();
        return newPlayerInfo.getNewPlayerDTO();
    }

    private boolean isAuthenticated(String playerToken, String storedPlayerToken){
        if(playerToken == null) return false;
        return TokenManager.validateToken(playerToken, storedPlayerToken);
    }

    private void closeConnection(){
        try {
            daoManager.closeConnection();
        } catch (DAOException e) {
            e.printStackTrace();
        }

    }
}
