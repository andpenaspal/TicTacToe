package tttHttp.DAO.MySQL;

import tttHttp.DAO.GameDAO;
import tttHttp.DAO.exceptions.*;
import tttHttp.models.Game;
import tttHttp.models.Player;
import tttHttp.models.Point;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLGameDAO implements GameDAO {

    private final Connection CONNECTION;

    public MySQLGameDAO(Connection CONNECTION){
        this.CONNECTION = CONNECTION;
    }

    @Override
    public Game get(Integer gameId) throws DAOException, DAODataNotFoundException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Game game;

        try {
            String GET_GAME = "SELECT * FROM games LEFT JOIN gamemoves on games.gameId = gamemoves.gameId WHERE games.gameId = ?";
            statement = CONNECTION.prepareStatement(GET_GAME);
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
            int turnCounter = resultSet.getInt("turnCounter");
            boolean winner = resultSet.getBoolean("winner");
            boolean draw = resultSet.getBoolean("draw");
            boolean surrendered = resultSet.getBoolean("surrendered");

            game = new Game(gameId, player1Id, null, player2Id, null, gameStarted, turn, turnCounter, winner, draw, surrendered,
                    new int[3][3],
                   null, new ArrayList<Point>());

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
            String GET_PLAYER_NAME = "SELECT playerName FROM players WHERE playerId = ?";
            statement = CONNECTION.prepareStatement(GET_PLAYER_NAME);
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
    public Integer insertPlayerIntoGame(int playerId) throws DAODMLException, DAOException{
        CallableStatement statement = null;
        int gameIdWithNewPlayer = 0;

        try {
            //Stored Procedure (playerId, gameId)
            String ADD_PLAYER_TO_GAME = "{call add_second_player_or_insert_new_game_and_insert_into_playergames(?, ?)}";
            statement = CONNECTION.prepareCall(ADD_PLAYER_TO_GAME);

            statement.setInt(1, playerId);
            statement.registerOutParameter(2, Types.INTEGER);

            if(statement.executeUpdate() == 0){
                //TODO: Log
                throw new DAODMLException("Problem trying to add Player with Id " + playerId + " to a Game");
            }else{
                gameIdWithNewPlayer = statement.getInt(2);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeStatement(statement);
        }

        return gameIdWithNewPlayer;
    }

    @Override
    public void update(Game game) throws DAODMLException, DAOException, DAOInvalidTurnException, DAOInvalidGameConditionsException, DAOInvalidMoveException {
        int turn = game.getTurn() == game.getPlayer1Id()? game.getPlayer2Id() : game.getPlayer1Id();
        if(game.isWinner()){
            winnerMove(turn, game.getGameId(), game.getLastInserted(), game.getWinningCombination());
        }else if(game.isDraw()) {
            makeMove(turn, game.getGameId(), game.getLastInserted(), true);
        } else{
            makeMove(turn, game.getGameId(), game.getLastInserted(), false);
        }
    }

    private void makeMove(int playerId, int gameId, Point move, boolean isDraw) throws DAOException, DAOInvalidTurnException,
            DAOInvalidGameConditionsException, DAOInvalidMoveException, DAODMLException {
        CallableStatement statement = null;

        //Stored Procedure (gameId, playerId, moveCol, moveRow)
        String MAKE_DRAW_MOVE = "{call make_draw_move_in_game(?, ?, ?, ?)}";
        //Stored Procedure (gameId, playerId, moveCol, moveRow)
        String MAKE_MOVE = "{call make_move_in_game(?, ?, ?, ?)}";

        String correctStatement = isDraw? MAKE_DRAW_MOVE : MAKE_MOVE;

        try {
            statement = CONNECTION.prepareCall(correctStatement);
            statement.setInt(1, gameId);
            statement.setInt(2, playerId);
            statement.setInt(3, move.getMoveCol());
            statement.setInt(4, move.getMoveRow());

            if(statement.executeUpdate() == 0) {
                //TODO: Log
                throw new DAODMLException("Problem making a move [" + move.getMoveCol() + "," + move.getMoveRow() + "] in game " + gameId +
                        " " + "by " + playerId);            }
        } catch (SQLException throwables) {
            catchMySQLMoveExceptions(playerId, gameId, throwables);
        }finally {
            closeStatement(statement);
        }
    }

    private void winnerMove(int playerId, int gameId, Point move, List<Point> winningCombination) throws DAOException,
            DAOInvalidTurnException, DAOInvalidGameConditionsException, DAOInvalidMoveException, DAODMLException {
        CallableStatement statement = null;

        List<Point> oldMovesWinningPoints = new ArrayList<Point>(2);
        for(Point p : winningCombination){
            if(!p.equals(move)){
                oldMovesWinningPoints.add(p);
            }
        }

        try {
            //Stored Procedure (gameId, playerId, moveCol, moveRow, moveColWinnerMove2, moveRowWinnerMove2, moveColWinnerMove3,
            // moveRowWinnerMove3)
            String MAKE_WINNER_MOVE = "{call make_winner_move_in_game(?, ?, ?, ?, ?, ?, ?, ?)}";
            statement = CONNECTION.prepareCall(MAKE_WINNER_MOVE);
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
            }
        } catch (SQLException throwables) {
            catchMySQLMoveExceptions(playerId, gameId, throwables);
        }finally {
            closeStatement(statement);
        }
    }

    @Override
    public void deletePlayerFromGame(Game game, Player player) throws DAODMLException, DAOInvalidMoveException, DAOException, DAOInvalidTurnException, DAOInvalidGameConditionsException {
        CallableStatement statement = null;

        try {
            //Stored Procedure (gameId, playerId)
            String SET_SURRENDER = "{call set_surrendered_in_game(?, ?)}";
            statement = CONNECTION.prepareCall(SET_SURRENDER);
            statement.setInt(1, game.getGameId());
            statement.setInt(2, player.getPlayerId());

            if(statement.executeUpdate() == 0){
                //TODO: Log
                throw new DAODMLException("Problem setting surrendered on Game " + game.getGameId() + "by " + player.getPlayerId());
            }
        } catch (SQLException throwables) {
            catchMySQLMoveExceptions(player.getPlayerId(), game.getGameId(), throwables);
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
                throw new DAOException("Problem trying to modify data in the DDBB", throwables);
        }
    }

    /*
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

     */








}
