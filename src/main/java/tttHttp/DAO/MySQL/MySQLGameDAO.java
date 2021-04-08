package tttHttp.DAO.MySQL;

import jdk.nashorn.internal.codegen.CompilerConstants;
import tttHttp.DAO.GameDAO;
import tttHttp.models.Game;
import tttHttp.models.Point;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLGameDAO implements GameDAO {

    private final String GET_GAME = "SELECT * FROM games LEFT JOIN gamemoves on games.gameId = gamemoves.gameId WHERE games.gameId = ?";
    private final String GET_PLAYER_NAME = "SELECT playerName FROM players WHERE playerId = ?";
    //Stored Procedure (playerId, gameId)
    private final String ADD_PLAYER_TO_GAME = "{call add_second_player_or_insert_new_game_and_insert_into_playergames(?, ?)}";
    //Stored Procedure (gameId, playerId, moveCol, moveRow)
    private final String MAKE_MOVE = "{call make_move_in_game(?, ?, ?, ?)}";
    //Stored Procedure (gameId, playerId, moveCol, moveRow, moveColWinnerMove2, moveRowWinnerMove2, moveColWinnerMove3, moveRowWinnerMove3)
    private final String MAKE_WINNER_MOVE = "{call make_winner_move_in_game(?, ?, ?, ?, ?, ?, ?, ?)}";
    //Stored Procedure (gameId, playerId, moveCol, moveRow)
    private final String MAKE_DRAW_MOVE = "{call make_draw_move_in_game(?, ?, ?, ?)}";
    //Stored Procedure (gameId, playerId)
    private final String SET_SURRENDER = "{call set_surrendered_in_game(?, ?)}";

    private Connection connection;

    public MySQLGameDAO(Connection connection){
        this.connection = connection;
    }

    @Override
    public Game getGame(int gameId) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Game game = null;

        try {
            statement = connection.prepareStatement(GET_GAME);
            statement.setInt(1, gameId);

            resultSet = statement.executeQuery();

            if(resultSet.next()){
                game = mapResultSetToGameMainInfo(resultSet);
            }else{
                //TODO: no game with this ID
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeResultSet(resultSet);
            closeStatement(statement);
        }

        game.setPlayer1Name(mapResultSetToPlayerName(game.getPlayer1Id()));
        if(game.getPlayer2Id() != null){
            game.setPlayer2Name(mapResultSetToPlayerName(game.getPlayer2Id()));
        }

        return game;
    }

    private Game mapResultSetToGameMainInfo(ResultSet resultSet) {
        Game game = null;
        try {
            int gameId = resultSet.getInt("games.gameId");
            int player1Id = resultSet.getInt("player1Id");
            Integer player2Id = resultSet.getInt("player2Id");
            //Avoid storing player2Id=0 on NULL on DDBB
            if(resultSet.wasNull()) player2Id = null;
            boolean gameStarted = resultSet.getBoolean("gameStarted");
            int turn = resultSet.getInt("turn");
            //Set the turn based on the order of the players. Keep the ID in DDBB to preserve the relational structure
            if(turn != 0) turn = turn == player1Id? 1 : 2;
            int turnCounter = resultSet.getInt("turnCounter");
            boolean winner = resultSet.getBoolean("winner");
            boolean draw = resultSet.getBoolean("draw");
            boolean surrendered = resultSet.getBoolean("surrendered");

            game = new Game(gameId, player1Id, player2Id, gameStarted, turn, turnCounter, winner, draw, surrendered, new int[3][3],
                    new ArrayList<Point>());

            //If there are moves, will be gameMoveId=1 and go to get the game moves, if not, null=0 on ints
            resultSet.getInt("gameMoveId");
            if(!resultSet.wasNull()){
                getGameMoves(game, resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return game;
    }

    private void getGameMoves(Game game, ResultSet resultSet) throws SQLException {
        do {
            try {
                int moveCol = resultSet.getInt("moveColumn");
                int moveRow = resultSet.getInt("moveRow");
                int playerIdMove = resultSet.getInt("playerId");
                //Set the Tile based on the order of the players. Keep the ID in DDBB to preserve the relational structure
                game.getBoard()[moveCol][moveRow] = playerIdMove;
                if(resultSet.getBoolean("winningMove")){
                    game.getWinningCombination().add(new Point(moveCol, moveRow));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }while (resultSet.next());
    }

    private String mapResultSetToPlayerName(int playerId){
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String playerName = null;

        try {
            statement = connection.prepareStatement(GET_PLAYER_NAME);
            statement.setInt(1, playerId);

            resultSet = statement.executeQuery();
            if(resultSet.next()){
                playerName = resultSet.getString("playerName");
            }else {
                //TODO: No player
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeResultSet(resultSet);
            closeStatement(statement);
        }
        return playerName;
    }

    @Override
    public Game addPlayerToGame(int playerId) {
        CallableStatement statement = null;
        Game game = null;

        try {
            statement = connection.prepareCall(ADD_PLAYER_TO_GAME);

            statement.setInt(1, playerId);
            statement.registerOutParameter(2, Types.INTEGER);

            if(statement.executeUpdate() == 0){
                //TODO: Problem
            }else{
                int newGameId = statement.getInt(2);
                game = getGame(newGameId);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeStatement(statement);
        }

        return game;
    }

    //TODO: Cambiar el Trigger por una
    // Stored Procedure (habia que hacerlo igualmente para el select for update)

    @Override
    public Game makeMove(int playerId, int gameId, Point move) {
        CallableStatement statement = null;
        Game game = null;

        try {
            statement = connection.prepareCall(MAKE_MOVE);
            statement.setInt(1, gameId);
            statement.setInt(2, playerId);
            statement.setInt(3, move.getMoveCol());
            statement.setInt(4, move.getMoveRow());

            if(statement.executeUpdate() == 0){
                //TODO: problem. NEVER HERE, MYSQLEXCEPTION BELLOW (CAUSE OF SIGNAL) en TODOS
            }else{
                game = getGame(gameId);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            //TODO: mirar el numero de la signal del custom, y del primary key (repeatedtile) para mandar custom exceptions
            System.out.println("Error Executing makeMove");
        }finally {
            closeStatement(statement);
        }
        return game;
    }

    @Override
    public Game winnerMove(int playerId, int gameId, Point move, List<Point> winningCombination) {
        CallableStatement statement = null;
        Game game = null;

        List<Point> oldMovesWinningPoints = new ArrayList<Point>(2);
        for(Point p : winningCombination){
            if(!p.equals(move)){
                oldMovesWinningPoints.add(p);
            }
        }

        try {
            statement = connection.prepareCall(MAKE_WINNER_MOVE);
            statement.setInt(1, gameId);
            statement.setInt(2, playerId);
            statement.setInt(3, move.getMoveCol());
            statement.setInt(4, move.getMoveRow());
            statement.setInt(5, oldMovesWinningPoints.get(0).getMoveCol());
            statement.setInt(6, oldMovesWinningPoints.get(0).getMoveRow());
            statement.setInt(7, oldMovesWinningPoints.get(1).getMoveCol());
            statement.setInt(8, oldMovesWinningPoints.get(1).getMoveRow());

            if(statement.executeUpdate() == 0){
                //TODO: problem. NEVER HERE, MYSQLEXCEPTION BELLOW (CAUSE OF SIGNAL) en TODOS
            }else{
                game = getGame(gameId);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeStatement(statement);
        }
        return game;
    }

    @Override
    public Game drawMove(int playerId, int gameId, Point move) {
        CallableStatement statement = null;
        Game game = null;

        try {
            statement = connection.prepareCall(MAKE_DRAW_MOVE);
            statement.setInt(1, gameId);
            statement.setInt(2, playerId);
            statement.setInt(3, move.getMoveCol());
            statement.setInt(4, move.getMoveRow());

            if(statement.executeUpdate() == 0){
                //TODO: problem. NEVER HERE, MYSQLEXCEPTION BELLOW (CAUSE OF SIGNAL) en TODOS
            }else{
                game = getGame(gameId);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeStatement(statement);
        }
        return game;
    }

    @Override
    public Game setSurrendered(int playerId, int gameId) {
        CallableStatement statement = null;
        Game game = null;

        try {
            statement = connection.prepareCall(SET_SURRENDER);
            statement.setInt(1, gameId);
            statement.setInt(2, playerId);

            if(statement.executeUpdate() == 0){
                //TODO: problem. NEVER HERE, MYSQLEXCEPTION BELLOW (CAUSE OF SIGNAL) en TODOS
            }else{
                game = getGame(gameId);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeStatement(statement);
        }
        return game;
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
            MySQLGameDAO gameDAO = new MySQLGameDAO(
                    DriverManager.getConnection("jdbc:mysql://localhost/tictactoe", "root", ""));
            //Game game = gameDAO.getGame(10);
            List<Point> winnerMoves = new ArrayList<>();
            winnerMoves.add(new Point(0,0));
            winnerMoves.add(new Point(1,1));
            winnerMoves.add(new Point(2,2));
            Game game = gameDAO.winnerMove(10, 10, new Point(0, 0), winnerMoves);
            System.out.println(game.toString());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
