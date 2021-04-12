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

    private final String GET_PLAYER = "SELECT players.playerId, playerName, playerToken, GROUP_CONCAT(gameId) as 'playerGames' " +
            "FROM players, playergames WHERE players.playerId = playergames.playerId AND players.playerId = ?";
    private final String NEW_PLAYER = "INSERT INTO players (playerName, playerToken) VALUES (?, ?)";

    private final Connection CONNECTION;

    public MySQLPlayerDAO(Connection connection) {
        this.CONNECTION = connection;
    }

    @Override
    public Player getPlayer(int playerId) throws DAODataNotFoundException, DAOException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Player player = null;

        try {
            statement = CONNECTION.prepareStatement(GET_PLAYER);
            statement.setInt(1,playerId);

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
    public Player newPlayer(String playerName, String playerToken) throws DAOException, DAODataNotFoundException, DAODMLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Player player = null;

        try {
            statement = CONNECTION.prepareStatement(NEW_PLAYER, statement.RETURN_GENERATED_KEYS);

            statement.setString(1, playerName);
            statement.setString(2, playerToken);

            int updateResult = statement.executeUpdate();
            if(updateResult == 0){
                //TODO: Log
                throw new DAODMLException("Problem Inserting a New Player: " + playerName);
            }else{
                resultSet = statement.getGeneratedKeys();
                resultSet.next();
                int newPlayerId = resultSet.getInt(1);
                player = getPlayer(newPlayerId);
            }
        } catch (SQLException throwables) {
            //TODO: Log
            throw new DAOException("Problem trying to Insert a Player in SQL: " + playerName, throwables);
        }finally {
            closeResultSet(resultSet);
            closeStatement(statement);
        }
        return player;
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
