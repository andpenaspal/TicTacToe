package tttHttp.DAO.MySQL;

import tttHttp.DAO.PlayerDAO;
import tttHttp.models.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLPlayerDAO implements PlayerDAO {

    private final Connection connection;

    public MySQLPlayerDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Player get(Integer playerId) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Player player = null;

        try {
            String GET_PLAYER = "SELECT players.playerId, playerName, playerToken, GROUP_CONCAT(gameId) as 'playerGames' " +
                    "FROM players, playergames WHERE players.playerId = playergames.playerId AND players.playerId = ? AND players.active = true";
            statement = connection.prepareStatement(GET_PLAYER);
            statement.setInt(1,playerId);

            resultSet = statement.executeQuery();

            if(resultSet.next()){
                player = mapResultSetToPlayer(resultSet);
            }else{
                //TODO: No player found with this ID. Throw Exception, catched by the Controller
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
    public Integer insert(Player player) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int newPlayerId = 0;

        try {
            String NEW_PLAYER = "INSERT INTO players (playerName, playerToken) VALUES (?, ?)";
            statement = connection.prepareStatement(NEW_PLAYER, statement.RETURN_GENERATED_KEYS);

            statement.setString(1, player.getPlayerName());
            statement.setString(2, player.getPlayerToken());

            int updateResult = statement.executeUpdate();
            if(updateResult == 0){
                //TODO: No se pudo meter, suelta excepcion
            }else{
                resultSet = statement.getGeneratedKeys();
                resultSet.next();
                newPlayerId = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeResultSet(resultSet);
            closeStatement(statement);
        }
        return newPlayerId;
    }

    @Override
    public void update(Player player) {
        PreparedStatement statement = null;

        try {
            String NEW_NAME = "UPDATE players SET playerName = ? WHERE playerId = ?";
            statement = connection.prepareStatement(NEW_NAME);

            statement.setString(1, player.getPlayerName());
            statement.setInt(2, player.getPlayerId());

            if(statement.executeUpdate() == 0){
                //TODO: Problem updating
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeStatement(statement);
        }
    }

    @Override
    public void delete(Player player) {
        PreparedStatement statement = null;

        try {
            String DELETE_PLAYER = "UPDATE players SET active = false WHERE playerId = ?";
            statement = connection.prepareStatement(DELETE_PLAYER);

            statement.setInt(1, player.getPlayerId());

            if(statement.executeUpdate() == 0){
                //TODO: Problem deleting
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeStatement(statement);
        }
    }

    private void closeStatement(PreparedStatement statement){
        if(statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private void closeResultSet(ResultSet resultSet){
        if(resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
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
