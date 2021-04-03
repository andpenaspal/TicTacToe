package tttHttp.controllers;

import tttHttp.DTO.NewPlayerDTO;
import tttHttp.DTO.PlayerDTO;
import tttHttp.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

public class PlayerController {

    //MySql IV

    //MySql Connection DAO on Constructor

    public PlayerDTO getPlayer(int playerId){
        //TODO: Check if not null, Check authorization, Connect to the database. Get the Player and convert to PlaterDTO
        List<Integer> testList = new ArrayList<Integer>();
        testList.add(7);
        return new PlayerDTO(1, "TestName", testList);
    }

    public NewPlayerDTO addNewPlayer(String PlayerName){
        //TODO: get new Token
        //TODO: Add new player to the database with name and token, get the ID, return newPlayerDTO with all
        String playerToken = TokenManager.tokenGenerator(50);
        return new NewPlayerDTO(1, "Testing New Player", playerToken);
    }

    private void playerAuthentication(String playerToken, String storedPlayerToken){
        //TODO: check Id/Token with Database, return Exception if not match
        boolean isAuthenticated = TokenManager.validateToken(playerToken, storedPlayerToken);
    }
}
