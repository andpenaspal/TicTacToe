package tttHttp.RestAssuredTests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import tttHttp.utilsTests.DDBBTestingDataLoader;

public class GameResourceRestAssuredTests {

    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "http://localhost:8086/TicTacToe_HTTP_war_exploded/webapi/players/{playerId}/games";
        try {
            DDBBTestingDataLoader.loadDDBBTestingData(PlayerResourceRestAssuredTests.class.getResource("/DDBBTestingSQL" +
                    "/DDBBTestingDataGames.sql").getFile());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
/*
    @AfterAll
    static void tearDown(){
        DDBBTestingDataLoader.loadDDBBTestingData(PlayerResourceRestAssuredTests.class.getResource("/DDBBTestingSQL" +
                "/DDBBTestingDataTearDown.sql").getFile());
    }

 */

    /*
    Tests for Games:
    - Get:
        - Happy Path with no started game
        - Happy Path with some moves
        - Happy Path with winning combination
        - No authenticated (All different scenarios covered in PlayersTests)
        - No authorized
        - Deleted Player (get from other player) ???
    - Post:
        - Happy Path new Game
        - Happy Path adding Second Player
        - No authenticated
        - Deleted Player
    - Patch:
        - Happy Path Make Move
        - Happy Path Make Draw Move
        - Happy Path Make Winning Move
        - No authenticated
        - No authorized
        - No started Game
        - On surrendered
        - On draw
        - On winner
        - On wrong turn
    - Delete:
        - Happy Path
        - Happy Path on no Turn
        - Happy Path No started Game
        - No authenticated
        - No authorized
        - On surrendered
        - On draw
        - On winner
     */

}
