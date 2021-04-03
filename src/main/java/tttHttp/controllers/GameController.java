package tttHttp.controllers;

import tttHttp.DTO.GameDTO;
import tttHttp.models.Point;

public class GameController {

    //TODO: Connection in IV

    //TODO: Connect on Constructor

    public GameDTO getGame(int playerId, int gameId, String playerToken){
        //TODO: get player from DDBB, will check Authentication
        //PlayerController playerController = new PlayerController();
        //TODO: check gameId is in the list of Games of the Player (Above) to check authorization
        //If so, get the game
        int[][] board = new int[3][3];
        GameDTO game = new GameDTO(true, 2, 0, false, false, false, board);
        game.setGameId(9);
        game.setPlayerNumber(7);
        game.setRemotePlayerName("tu puta madre");
        return game;
    }

    public GameDTO newGame(int playerId, String playerToken){
        //TODO: get player from DDBB, will check Authentication
        //PlayerController playerController = new PlayerController();
        //TODO: new game DDBB
        int[][] board = new int[3][3];
        GameDTO game = new GameDTO(true, 2, 0, false, false, false, board);
        game.setGameId(9);
        game.setPlayerNumber(7);
        game.setRemotePlayerName("tu puta madre");
        return game;
    }

    public GameDTO makeMove(int playerId, String playerToken, int gameId, Point gameMove){
        //TODO: get player from DDBB, will check Authentication
        //PlayerController playerController = new PlayerController();
        //TODO: get game from DDBB
        //TODO: new TicTacToe, fill with the game, check (started, turn, win, draw, surr) and error if so, if not, make the move, set in
        // DDBB, get the GameDTO and fill the DTO with the data from the Game (playerNumber and RemoteName)
        return null;
    }


    public GameDTO surrender(int playerId, String playerToken, int gameId) {
        //TODO: get player from DDBB, will check Authentication
        //PlayerController playerController = new PlayerController();
        //TODO: get game from DDBB, check (started, turn, win, draw) and error if so, if not, set surrender, set in DDBB, return GameDTO
        return null;
    }
}
