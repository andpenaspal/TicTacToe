package tttHttp.DAO.MySQL;

import tttHttp.DAO.GameDAO;
import tttHttp.DAO.exceptions.*;
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
    public Game getGame(int gameId) throws DAOException, DAODataNotFoundException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Game game;

        try {
            statement = connection.prepareStatement(GET_GAME);
            statement.setInt(1, gameId);

            resultSet = statement.executeQuery();

            if(resultSet.next()){
                game = mapResultSetToGameMainInfo(resultSet);
            }else{
                //TODO: Log
                throw new DAODataNotFoundException("Game with Id " + gameId + " not found");
            }
        } catch (SQLException throwables) {
            //TODO: Log
            throw new DAOException("Problem trying to get a Game: " + gameId, throwables);
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

    private Game mapResultSetToGameMainInfo(ResultSet resultSet) throws DAOException {
        Game game;
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

            //If there are moves, will be gameMoveId=1 and go to get the game moves
            resultSet.getInt("moveColumn");
            if(!resultSet.wasNull()){
                getGameMoves(game, resultSet);
            }
        } catch (SQLException throwables) {
            //TODO: Log
            throw new DAOException("Problem trying to access the ResultSet", throwables);
        }
        return game;
    }

    private void getGameMoves(Game game, ResultSet resultSet) throws SQLException, DAOException {
        do {
            try {
                int moveCol = resultSet.getInt("moveColumn");
                int moveRow = resultSet.getInt("moveRow");
                int playerIdMove = resultSet.getInt("playerId");
                game.getBoard()[moveCol][moveRow] = playerIdMove;

                if(resultSet.getBoolean("winningMove")){
                    game.getWinningCombination().add(new Point(moveCol, moveRow));
                }
            } catch (SQLException throwables) {
                //TODO: Log
                throw new DAOException("Problem trying to access the ResultSet", throwables);
            }
        }while (resultSet.next());
    }

    private String mapResultSetToPlayerName(int playerId) throws DAODataNotFoundException, DAOException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String playerName;

        try {
            statement = connection.prepareStatement(GET_PLAYER_NAME);
            statement.setInt(1, playerId);

            resultSet = statement.executeQuery();
            if(resultSet.next()){
                playerName = resultSet.getString("playerName");
            }else {
                //TODO: Log
                throw new DAODataNotFoundException("Player with Id " + playerId + " not found");
            }
        } catch (SQLException throwables) {
            //TODO: Log
            throw new DAOException("Problem trying to get a Player", throwables);
        }finally {
            closeResultSet(resultSet);
            closeStatement(statement);
        }
        return playerName;
    }

    @Override
    public Game addPlayerToGame(int playerId) throws DAODMLException, DAODataNotFoundException, DAOException {
        CallableStatement statement = null;
        Game game = null;

        try {
            statement = connection.prepareCall(ADD_PLAYER_TO_GAME);

            statement.setInt(1, playerId);
            statement.registerOutParameter(2, Types.INTEGER);

            if(statement.executeUpdate() == 0){
                //TODO: Log
                throw new DAODMLException("Problem trying to add Player with Id " + playerId + " to a Game");
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

    @Override
    public Game makeMove(int playerId, int gameId, Point move) throws DAODMLException, DAODataNotFoundException, DAOException, DAOInvalidTurnException, DAOInvalidGameConditionsException, DAOInvalidMoveException {
        CallableStatement statement = null;
        Game game = null;

        try {
            statement = connection.prepareCall(MAKE_MOVE);
            statement.setInt(1, gameId);
            statement.setInt(2, playerId);
            statement.setInt(3, move.getMoveCol());
            statement.setInt(4, move.getMoveRow());

            if(statement.executeUpdate() == 0){
                //TODO: Log
                throw new DAODMLException("Problem making a move [" + move.getMoveCol() + "," + move.getMoveRow() + "] in game " + gameId +
                        " " + "by " + playerId);
            }else{
                game = getGame(gameId);
            }
        } catch (SQLException throwables) {
            catchMySQLMoveExceptions(playerId, gameId, throwables);
        }finally {
            closeStatement(statement);
        }
        return game;
    }

    @Override
    public Game winnerMove(int playerId, int gameId, Point move, List<Point> winningCombination) throws DAODataNotFoundException, DAOException, DAOInvalidTurnException, DAOInvalidGameConditionsException, DAOInvalidMoveException, DAODMLException {
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
                //TODO: Log
                throw new DAODMLException("Problem making a winning move [" + move.getMoveCol() + "," + move.getMoveRow() + "] in game " + gameId +
                        " " + "by " + playerId);
            }else{
                game = getGame(gameId);
            }
        } catch (SQLException throwables) {
            catchMySQLMoveExceptions(playerId, gameId, throwables);
        }finally {
            closeStatement(statement);
        }
        return game;
    }

    @Override
    public Game drawMove(int playerId, int gameId, Point move) throws DAODataNotFoundException, DAOException, DAOInvalidTurnException, DAOInvalidGameConditionsException, DAOInvalidMoveException, DAODMLException {
        CallableStatement statement = null;
        Game game = null;

        try {
            statement = connection.prepareCall(MAKE_DRAW_MOVE);
            statement.setInt(1, gameId);
            statement.setInt(2, playerId);
            statement.setInt(3, move.getMoveCol());
            statement.setInt(4, move.getMoveRow());

            if(statement.executeUpdate() == 0){
                //TODO: Log
                throw new DAODMLException("Problem making a draw move [" + move.getMoveCol() + "," + move.getMoveRow() + "] in game " + gameId +
                        " " + "by " + playerId);
            }else{
                game = getGame(gameId);
            }
        } catch (SQLException throwables) {
            catchMySQLMoveExceptions(playerId, gameId, throwables);
        }finally {
            closeStatement(statement);
        }
        return game;
    }

    @Override
    public Game setSurrendered(int playerId, int gameId) throws DAODataNotFoundException, DAOException, DAOInvalidTurnException, DAOInvalidGameConditionsException, DAOInvalidMoveException, DAODMLException {
        CallableStatement statement = null;
        Game game = null;

        try {
            statement = connection.prepareCall(SET_SURRENDER);
            statement.setInt(1, gameId);
            statement.setInt(2, playerId);

            if(statement.executeUpdate() == 0){
                //TODO: Log
                throw new DAODMLException("Problem setting surrendered on Game " + gameId + "by " + playerId);
            }else{
                game = getGame(gameId);
            }
        } catch (SQLException throwables) {
            catchMySQLMoveExceptions(playerId, gameId, throwables);
        }finally {
            closeStatement(statement);
        }
        return game;
    }

    private void closeStatement(PreparedStatement statement) throws DAOException {
        if(statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                //TODO: Log
                throw new DAOException("Problem trying to close the Statement", throwables);
            }
        }
    }

    private void closeResultSet(ResultSet resultSet) throws DAOException {
        if(resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                //TODO: Log
                throw new DAOException("Problem trying to close the ResultSet", throwables);
            }
        }
    }

    private void catchMySQLMoveExceptions(int playerId, int gameId, SQLException throwables) throws DAOInvalidMoveException, DAOInvalidGameConditionsException, DAOInvalidTurnException, DAOException {
        switch (throwables.getSQLState()) {
            case "23000":
                throw new DAOInvalidMoveException(throwables.getMessage(), throwables);
            case "45000":
                throw new DAOInvalidGameConditionsException(throwables.getMessage(), throwables);
            case "45001":
                throw new DAOInvalidTurnException(throwables.getMessage(), throwables);
            default:
                throw new DAOException("Problem trying to make a Move in Game " + gameId + " by " + playerId, throwables);
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
            Game game = null;
            try {
                //game = gameDAO.winnerMove(10, 10, new Point(0, 0), winnerMoves);
                game = gameDAO.makeMove(11, 11, new Point(2,0));
            } catch (DAODataNotFoundException e) {
                e.printStackTrace();
            } catch (DAOException e) {
                e.printStackTrace();
            } catch (DAODMLException e) {
                e.printStackTrace();
            } catch (DAOInvalidGameConditionsException e) {
                e.printStackTrace();
            } catch (DAOInvalidMoveException e) {
                e.printStackTrace();
            } catch (DAOInvalidTurnException e) {
                e.printStackTrace();
            }
            System.out.println(game.toString());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
