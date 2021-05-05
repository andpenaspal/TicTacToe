package tttHttp.DAO.MySQL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger LOG = LoggerFactory.getLogger(MySQLPlayerDAO.class);

    public MySQLPlayerDAO(Connection connection) {
        this.CONNECTION = connection;
    }

    @Override
    public Player get(Integer playerId) throws DAODataNotFoundException, DAOException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Player player;

        try {
            //"Group By" to avoid 1 row with nulls on not found instead of 0 rows as required
            String GET_PLAYER = "SELECT players.playerId, playerName, playerToken, group_concat(gameId) AS 'playergames' FROM players " +
                    "LEFT JOIN games on players.playerId = games.player1Id OR players.playerId = games.player2Id WHERE players.playerId =" +
                    " ? AND players.active = true GROUP BY players.playerId";
            statement = CONNECTION.prepareStatement(GET_PLAYER);
            statement.setInt(1, playerId);

            resultSet = statement.executeQuery();

            if(resultSet.next()){
                player = mapResultSetToPlayer(resultSet);
            }else{
                LOG.warn("Player with ID '{}' not found in the DataBase", playerId);
                throw new DAODataNotFoundException("Player with ID " + playerId + " not found");
            }
        } catch (SQLException throwables) {
            LOG.error("Error trying to get the Player '{}' from the DataBase", playerId, throwables);
            throw new DAOException("Problem trying to get a Player from the DataBase: " + playerId, throwables);
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
                LOG.warn("Problem Inserting the New Player '{}' in the DataBase", player.getPlayerName());
                throw new DAODMLException("Problem Inserting a New Player: " + player.getPlayerName());
            }else{
                resultSet = statement.getGeneratedKeys();
                resultSet.next();
                newPlayerId = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            LOG.error("Error trying to Insert the Player '{}' in the DataBase", player.getPlayerName(), throwables);
            throw new DAOException("Problem trying to Insert a Player in the DataBase: " + player.getPlayerName(), throwables);
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
                LOG.warn("Problem trying to Update the Player '{}' with the Name '{}' in the DataBase", player.getPlayerId(),
                        player.getPlayerName());
                throw new DAODMLException("Problem trying to update the Player with Id " + player.getPlayerId());
            }
        } catch (SQLException throwables) {
            LOG.error("Error trying to Update the Player '{}' in the DataBase", player.getPlayerId(), throwables);
            throw new DAOException("Error trying to Update the Player " + player.getPlayerId() + " in the DataBase", throwables);
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
                LOG.warn("Problem trying to Delete the Player '{}'", player.getPlayerId());
                throw new DAODMLException("Problem trying to Delete Player with Id " + player.getPlayerId());
            }
        } catch (SQLException throwables) {
            LOG.error("Error trying to set as Inactive the Player '{}' in the DataBase", throwables);
            throw new DAOException("Error trying to set as Inactive the Player " + player.getPlayerId() + " in the Database", throwables);
        }finally {
            closeStatement(statement);
        }
    }

    private void closeStatement(PreparedStatement statement) throws DAOException {
        if(statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                LOG.error("Error trying to Close the Statement", throwables);
                throw new DAOException("Error trying to close the Statement", throwables);
            }
        }
    }

    private void closeResultSet(ResultSet resultSet) throws DAOException {
        if(resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                LOG.error("Error trying to Close the ResultSet", throwables);
                throw new DAOException("Error trying to close a ResultSet", throwables);
            }
        }
    }
}
