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
        int gameIdWithNewPlayer = 0;
        String lockNoStartedGame = "SELECT * FROM games WHERE gameStarted = false AND NOT player1Id = ? limit 1 FOR UPDATE";
        PreparedStatement lockStatement = null;
        ResultSet lockResultSet = null;

        try {
            CONNECTION.setAutoCommit(false);

            lockStatement = CONNECTION.prepareStatement(lockNoStartedGame, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            lockStatement.setInt(1, playerId);
            lockResultSet = lockStatement.executeQuery();

            if(lockResultSet.next()){
                gameIdWithNewPlayer = lockResultSet.getInt("gameId");
                lockResultSet.updateInt("player2Id", playerId);
                lockResultSet.updateBoolean("gameStarted", true);
                lockResultSet.updateInt("turn", playerId);
                lockResultSet.updateRow();
            }else{
                gameIdWithNewPlayer = insertNewGame(playerId);
            }
            CONNECTION.commit();
        } catch (SQLException throwables) {
            //TODO: ? Log
            try {
                CONNECTION.rollback();
            } catch (SQLException e) {
                throw new DAOException("Problem trying to Rollback after a problem trying to insert in or create a Game for Player Id: " + playerId,
                        e);
            }
            throw new DAOException("Problem trying to insert in or create a Game for Player Id: " + playerId, throwables);
        }finally {
            restoreAutoCommit();
            closeStatement(lockStatement);
            closeResultSet(lockResultSet);
        }
        return gameIdWithNewPlayer;
    }

    private int insertNewGame(int playerId) throws DAODMLException, DAOException {
        PreparedStatement insertStatement = null;
        String insertNewGame = "INSERT INTO games (player1Id) VALUES (?)";
        ResultSet insertResultSet = null;

        try {
            insertStatement = CONNECTION.prepareStatement(insertNewGame, insertStatement.RETURN_GENERATED_KEYS);
            insertStatement.setInt(1, playerId);
            if (insertStatement.executeUpdate() == 0) {
                //TODO: Log
                CONNECTION.rollback();
                throw new DAODMLException("Problem trying to create a new Game for Player with Id " + playerId);
            } else {
                insertResultSet = insertStatement.getGeneratedKeys();
                insertResultSet.next();
                return insertResultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            //TODO: Log
            try {
                CONNECTION.rollback();
            } catch (SQLException e) {
                throw new DAOException("Problem trying to Rollback after a problem creating a new Game for Player: " + playerId, e);
            }
            throw new DAOException("Problem creating a new Game for Player: " + playerId, throwables);
        }finally {
            closeResultSet(insertResultSet);
            closeStatement(insertStatement);
        }
    }

    @Override
    public void update(Game game) throws DAOException, DAOInvalidTurnException, DAOInvalidGameConditionsException, DAOInvalidMoveException, DAODMLException {
        //Switch Turn back for validation (already changed in GameController)
        int turn = game.getTurn() == game.getPlayer1Id()? game.getPlayer2Id() : game.getPlayer1Id();
        if(game.isWinner()){
            winnerMove(turn, game);
        }else if(game.isDraw()) {
            makeMove(turn, game, true);
        } else{
            makeMove(turn, game, false);
        }
    }

    private void makeMove(int playerId, Game game, boolean isDraw) throws DAOException, DAOInvalidTurnException,
            DAOInvalidGameConditionsException, DAOInvalidMoveException, DAODMLException {
        PreparedStatement lockStatement = null;
        String lockMakeMove = "SELECT * FROM games LEFT JOIN gamemoves ON games.gameId = gamemoves.gameId WHERE games.gameId = ? FOR " +
                "UPDATE";
        ResultSet lockResultSet = null;

        try{
            CONNECTION.setAutoCommit(false);

            lockStatement = CONNECTION.prepareStatement(lockMakeMove);
            lockStatement.setInt(1, game.getGameId());
            lockResultSet = lockStatement.executeQuery();

            if(lockResultSet.next()){
                checkCorrectTurn(lockResultSet, playerId);
                checkGameConditions(lockResultSet);

                updateGame(game.getGameId(), game.getPlayer1Id(), game.getPlayer2Id(), playerId, game.getTurnCounter());

                if(isDraw) updateGameSetDrawCondition(game.getGameId());

                insertMove(game.getGameId(), playerId, game.getLastInserted());

                CONNECTION.commit();
            }else{
                //TODO: Log
                CONNECTION.rollback();
                throw new DAODMLException("Problem Trying to access the Game: " + game.getGameId());
            }
        } catch (SQLException throwables) {
            //TODO: Log
            try {
                CONNECTION.rollback();
            } catch (SQLException e) {
                //TODO: Log
                throw new DAOException("Problem trying to Rollback after problems trying to Make a Move in the Game: " + game.getGameId(), e);
            }
            throw new DAOException("Problem trying to Make a Move in the Game: " + game.getGameId(), throwables);
        }finally {
            restoreAutoCommit();
            closeResultSet(lockResultSet);
            closeStatement(lockStatement);
        }
    }

    private void updateGame(int gameId, int player1Id, int player2Id, int currentTurn, int currentTurnCounter) throws DAODMLException, DAOException {
        PreparedStatement updateGameStatement = null;
        String updateGame = "UPDATE games SET turn = ?, turnCounter = ? WHERE gameId = ?";

        int nextTurn = currentTurn == player1Id? player2Id : player1Id;

        try{
            updateGameStatement = CONNECTION.prepareStatement(updateGame);
            updateGameStatement.setInt(1, nextTurn);
            updateGameStatement.setInt(2, (currentTurnCounter+1));
            updateGameStatement.setInt(3, gameId);

            if(updateGameStatement.executeUpdate() == 0){
                //TODO: Log
                CONNECTION.rollback();
                throw new DAODMLException("Problem Trying to update the Game Turn-Variables in the Game :" + gameId);
            }
        } catch (SQLException throwables) {
            try {
                CONNECTION.rollback();
            } catch (SQLException e) {
                //TODO: Log
                throw new DAOException("Problem trying to Rollback after a problem trying to update the Game Turn-Variables in the Game: " +
                        + gameId, e);
            }
            throw new DAOException("Problem trying to update the Game Turn-Variables in the Game :" + gameId, throwables);
        }finally {
            closeStatement(updateGameStatement);
        }
    }

    private void updateGameSetDrawCondition(int gameId) throws DAOException, DAODMLException {
        PreparedStatement updateGameDrawStatement = null;
        String updateGameDraw = "UPDATE games SET draw = true WHERE gameId = ?";

        try {
            updateGameDrawStatement = CONNECTION.prepareStatement(updateGameDraw);
            updateGameDrawStatement.setInt(1, gameId);

            if (updateGameDrawStatement.executeUpdate() == 0) {
                //TODO: Problem
                CONNECTION.rollback();
                throw new DAODMLException("Problem Trying to set Draw in the Game: " + gameId);
            }
        } catch (SQLException throwables) {
            try {
                CONNECTION.rollback();
            } catch (SQLException e) {
                //TODO: Log
                throw new DAOException("Problem trying to Rollback after a problem trying to set Draw in the Game: " +
                        + gameId, e);
            }
            throw new DAOException("Problem trying to set Draw in the Game :" + gameId, throwables);
        }finally {
            closeStatement(updateGameDrawStatement);
        }
    }

    private void winnerMove(int playerId, Game game) throws DAOInvalidTurnException, DAOInvalidGameConditionsException, DAOException,
            DAOInvalidMoveException, DAODMLException {
        PreparedStatement lockStatement = null;
        String lockMakeMove = "SELECT * FROM games LEFT JOIN gamemoves ON games.gameId = gamemoves.gameId WHERE games.gameId = ? FOR " +
                "UPDATE";
        ResultSet lockResultSet = null;

        try {
            CONNECTION.setAutoCommit(false);

            lockStatement = CONNECTION.prepareStatement(lockMakeMove);
            lockStatement.setInt(1, game.getGameId());
            lockResultSet = lockStatement.executeQuery();

            if (lockResultSet.next()) {
                checkCorrectTurn(lockResultSet, playerId);
                checkGameConditions(lockResultSet);

                updateGameWinVariables(game.getGameId(), lockResultSet.getInt("turnCounter"));

                insertMove(game.getGameId(), playerId, game.getLastInserted());

                updateWinMoves(game.getGameId(), game.getWinningCombination());

                CONNECTION.commit();
            }else{
                //TODO: Log
                CONNECTION.rollback();
                throw new DAODMLException("Problem Trying to access the Game: " + game.getGameId());
            }
        } catch (SQLException throwables) {
            //TODO: Log
            try {
                CONNECTION.rollback();
            } catch (SQLException e) {
                //TODO: Log
                throw new DAOException("Problem trying to Rollback after problems trying to Make a Move in the Game: " + game.getGameId(), e);
            }
            throw new DAOException("Problem trying to Make a Move in the Game: " + game.getGameId(), throwables);
        }finally {
            restoreAutoCommit();
            closeResultSet(lockResultSet);
            closeStatement(lockStatement);
        }
    }

    private void updateGameWinVariables(int gameId, int currentTurnCounter) throws DAOException, DAODMLException {
        PreparedStatement updateGameStatement = null;
        String updateGame = "UPDATE games SET turnCounter = ?, winner = true WHERE gameId = ?";

        try{
            updateGameStatement = CONNECTION.prepareStatement(updateGame);
            updateGameStatement.setInt(1, (currentTurnCounter+1));
            updateGameStatement.setInt(2, gameId);

            if(updateGameStatement.executeUpdate() == 0){
                //TODO: Log
                CONNECTION.rollback();
                throw new DAODMLException("Problem Trying to update the Game Win-Variables in the Game :" + gameId);
            }
        } catch (SQLException throwables) {
            try {
                CONNECTION.rollback();
            } catch (SQLException e) {
                //TODO: Log
                throw new DAOException("Problem trying to Rollback after a problem trying to update Winning variable in the Game: " +
                        + gameId, e);
            }
            //TODO: Log
            throw new DAOException("Problem trying to update Winning variable in the Game :" + gameId, throwables);
        }finally {
            closeStatement(updateGameStatement);
        }
    }

    private void updateWinMoves(int gameId, List<Point> winningCombination) throws DAOException, DAODMLException {
        PreparedStatement updateOldMovesStatement = null;
        String updateOldMoves = "UPDATE gamemoves SET winningMove = true WHERE gameId = ? AND moveColumn = ? AND moveRow = ?";

        for(Point p : winningCombination){
            try {
                updateOldMovesStatement = CONNECTION.prepareStatement(updateOldMoves);
                updateOldMovesStatement.setInt(1, gameId);
                updateOldMovesStatement.setInt(2, p.getMoveCol());
                updateOldMovesStatement.setInt(3, p.getMoveRow());

                if (updateOldMovesStatement.executeUpdate() == 0) {
                    //TODO: Log
                    CONNECTION.rollback();
                    throw new DAODMLException("Problem Trying to update old winning Moves in the Game: " + gameId);
                }
            } catch (SQLException throwables) {
                //TODO
                try {
                    CONNECTION.rollback();
                } catch (SQLException e) {
                    //TODO: Log
                    throw new DAOException("Problem trying to Rollback after a problem trying to update the Win Moves in the Game: " +
                            + gameId, e);
                }
                throw new DAOException("Problem trying to update the Win moves in the Game :" + gameId, throwables);
            }finally {
                closeStatement(updateOldMovesStatement);
            }
        }

    }

    private void checkCorrectTurn(ResultSet resultSet, int playerId) throws DAOInvalidTurnException, SQLException {
        if (resultSet.getInt("turn") != playerId) {
            CONNECTION.rollback();
            throw new DAOInvalidTurnException();
        }
    }

    private void checkGameConditions(ResultSet resultSet) throws SQLException, DAOInvalidGameConditionsException {
        if(resultSet.getBoolean("winner")
                || resultSet.getBoolean("draw")
                || resultSet.getBoolean("surrendered")){
            CONNECTION.rollback();
            throw new DAOInvalidGameConditionsException();
        }
    }

    private void insertMove(int gameId, int playerId, Point move) throws DAOException, DAOInvalidMoveException, DAODMLException {
        PreparedStatement insertMoveStatement = null;
        String insertMoveQuery = "INSERT INTO gamemoves (gameId, moveColumn, moveRow, playerId) VALUES (?,?,?,?)";

        try {
            insertMoveStatement = CONNECTION.prepareStatement(insertMoveQuery);

            insertMoveStatement.setInt(1, gameId);
            insertMoveStatement.setInt(2, move.getMoveCol());
            insertMoveStatement.setInt(3, move.getMoveRow());
            insertMoveStatement.setInt(4, playerId);

            if(insertMoveStatement.executeUpdate() == 0){
                //TODO: log
                CONNECTION.rollback();
                throw new DAODMLException("Problem Trying to insert Moves in the Game: " + gameId);
            }
        } catch (SQLException throwables) {
            //TODO
            if(throwables.getSQLState().equalsIgnoreCase("23000")) throw new DAOInvalidMoveException();
            try {
                CONNECTION.rollback();
            } catch (SQLException e) {
                //TODO: Log
                throw new DAOException("Problem trying to Rollback after a problem trying to insert Moves in the Game: " +
                        + gameId, e);
            }
            //TODO: Log
            throw new DAOException("Problem trying to insert Moves in the Game :" + gameId, throwables);
        }finally {
            closeStatement(insertMoveStatement);
        }
    }

    @Override
    public void deletePlayerFromGame(Game game, Player player) throws DAOException, DAOInvalidGameConditionsException, DAODMLException {
        PreparedStatement lockStatement = null;
        String lockMakeMove = "SELECT * FROM games LEFT JOIN gamemoves ON games.gameId = gamemoves.gameId WHERE games.gameId = ? FOR " +
                "UPDATE";
        ResultSet lockResultSet = null;

        try {
            CONNECTION.setAutoCommit(false);

            lockStatement = CONNECTION.prepareStatement(lockMakeMove);
            lockStatement.setInt(1, game.getGameId());
            lockResultSet = lockStatement.executeQuery();

            if (lockResultSet.next()) {
                checkGameConditions(lockResultSet);

                updateSurrenderedGame(game.getGameId(), player.getPlayerId());

                CONNECTION.commit();
            }else{
                //TODO: Log
                CONNECTION.rollback();
                throw new DAODMLException("Problem Trying to access the Game: " + game.getGameId());
            }
        } catch (SQLException throwables) {
            //TODO: Log
            try {
                CONNECTION.rollback();
            } catch (SQLException e) {
                //TODO: Log
                throw new DAOException("Problem trying to Rollback after problems trying to access the Game: " + game.getGameId() + " to " +
                        "set Surrender for player Id: " + player.getPlayerId(), e);
            }
            throw new DAOException("Problem trying to access the Game: " + game.getGameId() + " to set Surrender for player Id: " + player.getPlayerId(), throwables);
        }finally {
            restoreAutoCommit();
            closeResultSet(lockResultSet);
            closeStatement(lockStatement);
        }
    }

    private void updateSurrenderedGame(int gameId, int playerId) throws DAOException, DAODMLException {
        PreparedStatement updateGameSurrenderStatement = null;
        String updateGameSurrender = "UPDATE games SET surrendered = true, turn = ? WHERE gameId = ?";

        try{
            updateGameSurrenderStatement = CONNECTION.prepareStatement(updateGameSurrender);
            updateGameSurrenderStatement.setInt(1, playerId);
            updateGameSurrenderStatement.setInt(2, gameId);

            if(updateGameSurrenderStatement.executeUpdate() == 0){
                //TODO: Log
                CONNECTION.rollback();
                throw new DAODMLException("Problem Trying to set Surrendered for Player Id: " + playerId + " in Game: " + gameId);
            }
        } catch (SQLException throwables) {
            try {
                CONNECTION.rollback();
            } catch (SQLException e) {
                //TODO: Log
                throw new DAOException("Problem trying to Rollback after a problem trying to update Surrender variable in the Game: " +
                        + gameId, e);
            }
            //TODO: Log
            throw new DAOException("Problem trying to update Surrender variable in the Game :" + gameId, throwables);
        }finally {
            closeStatement(updateGameSurrenderStatement);
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

    private void restoreAutoCommit() {
        try {
            CONNECTION.setAutoCommit(true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
