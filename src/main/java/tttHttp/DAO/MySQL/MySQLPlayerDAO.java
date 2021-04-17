package tttHttp.DAO.MySQL;

import tttHttp.DAO.PlayerDAO;
import tttHttp.DAO.exceptions.DAODMLException;
import tttHttp.DAO.exceptions.DAODataNotFoundException;
import tttHttp.DAO.exceptions.DAOException;
import tttHttp.models.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLPlayerDAO implements PlayerDAO {

    private final Connection CONNECTION;

    public MySQLPlayerDAO(Connection connection) {
        this.CONNECTION = connection;
    }

    @Override
    public Player get(Integer playerId) throws DAODataNotFoundException, DAOException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Player player;

        try {
            String GET_PLAYER = "SELECT players.playerId, playerName, playerToken, GROUP_CONCAT(gameId) as 'playerGames' " +
                    "FROM players, playergames WHERE players.playerId = playergames.playerId AND players.playerId = ? AND players.active = true";
            statement = CONNECTION.prepareStatement(GET_PLAYER);
            statement.setInt(1,playerId);

            resultSet = statement.executeQuery();

            if(resultSet.next()){
                player = mapResultSetToPlayer(resultSet);
            }else{
                throw new DAODataNotFoundException("Player with ID " + playerId + " not found");
            }
        } catch (SQLException throwables) {
            //TODO: Log
            throw new DAOException("Problem trying to get a Player in SQL: " + playerId, throwables);
        }finally {
            closeResultSet(resultSet);
            closeStatement(statement);
        }
        return player;
    }

    private Player mapResultSetToPlayer(ResultSet resultSet) throws SQLException {
        int playerId = resultSet.getInt("playerId");
        String playerName = resultSet.getString("playerName");
        String playerToken = resultSet.getString("playerToken");

        String playerGamesConcatenatedGames = resultSet.getString("playerGames");
        List<Integer> playerGames;
        playerGames = getPlayerGames(playerGamesConcatenatedGames);

        return new Player(playerId, playerName, playerToken, playerGames);
    }

    private List<Integer> getPlayerGames(String playerGamesConcatenatedGames) {
        List<Integer> playerGames;
        if(playerGamesConcatenatedGames != null){
            String [] playerGamesWithSplitGames = playerGamesConcatenatedGames.split(",");

            playerGames = new ArrayList<Integer>(playerGamesWithSplitGames.length);
            for(String game : playerGamesWithSplitGames){
                playerGames.add(Integer.parseInt(game));
            }
        }else{
            playerGames = new ArrayList<Integer>();
        }
        return playerGames;
    }

    @Override
    public Integer insert(Player player) throws DAOException, DAODMLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int newPlayerId = 0;

        try {
            String NEW_PLAYER = "INSERT INTO players (playerName, playerToken) VALUES (?, ?)";
            statement = CONNECTION.prepareStatement(NEW_PLAYER, statement.RETURN_GENERATED_KEYS);

            statement.setString(1, player.getPlayerName());
            statement.setString(2, player.getPlayerToken());

            int updateResult = statement.executeUpdate();
            if(updateResult == 0){
                //TODO: Log
                throw new DAODMLException("Problem Inserting a New Player: " + player.getPlayerName());
            }else{
                resultSet = statement.getGeneratedKeys();
                resultSet.next();
                newPlayerId = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            //TODO: Log
            throw new DAOException("Problem trying to Insert a Player in SQL: " + player.getPlayerName(), throwables);
        }finally {
            closeResultSet(resultSet);
            closeStatement(statement);
        }
        return newPlayerId;
    }

    @Override
    public void update(Player player) throws DAOException, DAODMLException {
        PreparedStatement statement = null;

        try {
            String NEW_NAME = "UPDATE players SET playerName = ? WHERE playerId = ?";
            statement = CONNECTION.prepareStatement(NEW_NAME);

            statement.setString(1, player.getPlayerName());
            statement.setInt(2, player.getPlayerId());

            if(statement.executeUpdate() == 0){
                //TODO: Log
                throw new DAODMLException("Problem trying to update Player with Id " + player.getPlayerId() + " to a Game");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeStatement(statement);
        }
    }

    @Override
    public void delete(Player player) throws DAOException, DAODMLException {
        PreparedStatement statement = null;

        try {
            String DELETE_PLAYER = "UPDATE players SET active = false WHERE playerId = ?";
            statement = CONNECTION.prepareStatement(DELETE_PLAYER);

            statement.setInt(1, player.getPlayerId());

            if(statement.executeUpdate() == 0){
                //TODO: Log
                throw new DAODMLException("Problem trying to delete Player with Id " + player.getPlayerId() + " to a Game");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeStatement(statement);
        }
    }

    private void closeStatement(PreparedStatement statement) throws DAOException {
        if(statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                //TODO: Log
                throw new DAOException("Problem trying to close a Statement in SQL", throwables);
            }
        }
    }

    private void closeResultSet(ResultSet resultSet) throws DAOException {
        if(resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                //TODO: Log
                throw new DAOException("Problem trying to close a ResultSet in SQL", throwables);
            }
        }
    }

    /*
    public static void main(String[] args) {
        try {
            MySQLPlayerDAO playerDAO = new MySQLPlayerDAO(
                    DriverManager.getConnection("jdbc:mysql://localhost/tictactoe", "root", ""));
            playerDAO.newPlayer("TestingJava", "testingJavaToken");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
     */
}
