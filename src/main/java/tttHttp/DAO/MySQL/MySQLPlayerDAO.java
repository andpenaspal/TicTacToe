package tttHttp.DAO.MySQL;

import tttHttp.DAO.PlayerDAO;
import tttHttp.models.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLPlayerDAO implements PlayerDAO {

    private final String GET_PLAYER = "SELECT players.playerId, playerName, playerToken, GROUP_CONCAT(gameId) as 'playerGames' " +
            "FROM players, playergames WHERE players.playerId = playergames.playerId AND players.playerId = ?";

    private final String NEW_PLAYER = "INSERT INTO players (playerName, playerToken) VALUES (?, ?)";

    private Connection connection;

    public MySQLPlayerDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Player getPlayer(int playerId) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Player player = null;

        try {
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

        Player player = new Player(playerId, playerName, playerToken, playerGames);
        return player;
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
    public Player newPlayer(String playerName, String playerToken) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Player player = null;

        try {
            statement = connection.prepareStatement(NEW_PLAYER, statement.RETURN_GENERATED_KEYS);

            statement.setString(1, playerName);
            statement.setString(2, playerToken);

            int updateResult = statement.executeUpdate();
            if(updateResult == 0){
                //TODO: No se pudo meter, suelta excepcion
            }else{
                resultSet = statement.getGeneratedKeys();
                resultSet.next();
                int newPlayerId = resultSet.getInt(1);
                player = getPlayer(newPlayerId);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeResultSet(resultSet);
            closeStatement(statement);
        }
        return player;
    }

    private void closeStatement(PreparedStatement statement){
        try {
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void closeResultSet(ResultSet resultSet){
        try {
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            MySQLPlayerDAO playerDAO = new MySQLPlayerDAO(
                    DriverManager.getConnection("jdbc:mysql://localhost/tictactoe", "root", ""));
            playerDAO.newPlayer("TestingJava", "testingJavaToken");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
