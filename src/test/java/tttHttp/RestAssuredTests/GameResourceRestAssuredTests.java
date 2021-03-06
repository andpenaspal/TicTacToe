package tttHttp.RestAssuredTests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tttHttp.utils.ExceptionsEnum;
import tttHttp.utilsTests.DDBBTestingDataLoader;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.*;

public class GameResourceRestAssuredTests {

    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "http://localhost:8086/TicTacToe_HTTP_war_exploded/webapi/players/";
        try {
            DDBBTestingDataLoader.loadDDBBTestingData(PlayerResourceRestAssuredTests.class.getResource("/DDBBTestingSQL" +
                    "/DDBBTestingDataGames.sql").getFile());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @AfterAll
    static void tearDown(){
        DDBBTestingDataLoader.loadDDBBTestingData(PlayerResourceRestAssuredTests.class.getResource("/DDBBTestingSQL" +
                "/DDBBTestingDataTearDown.sql").getFile());
    }

    /*
    Tests for Games:
    - Get:
        - Happy Path with some moves
        - Happy Path with some moves Second Player
        - Happy Path with winning combination
        - Happy Path with winning combination Second Player
        - Happy Path with no started game
        - No authenticated (All different scenarios covered in PlayersTests)
        - No authorized
    - Post:
        - Happy Path new Game
        - Happy Path adding Second Player
        - No authenticated
    - Patch:
        - Happy Path Make Move
        - Happy Path Make Draw Move
        - Happy Path Make Winning Move
        - No authenticated
        - No authorized
        - No started Game
        - On DrawS/Surrendered
        - Incorrect Turn
        - On winner
    - Delete:
        - Happy Path on turn
        - Happy Path on no Turn
        - Happy Path No started Game
        - No authenticated
        - No authorized
        - On surrendered
        - On draw
        - On winner
     */

    /*
    - Get:
        - Happy Path with some moves
        - Happy Path with winning combination
        - Happy Path with no started game
        - No authenticated (All different scenarios covered in PlayersTests)
        - No authorized
     */
    @Nested
    class Get{

        @Test
        @DisplayName("Get a Game")
        void getGame(){

            //Expected Board
            List<List<Integer>> expectedBoard = getExpectedBoard(new int[][]{{2,1,0},{0,1,0},{0,0,2}});

            Header header = new Header("playerToken", "Token1");

            RestAssured
                    .given()
                        .header(header)
                    .when()
                        .get("{playerId}/games/{gameId}", 1, 1)
                    .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaGameDTO.json"))
                        .body("gameId", equalTo(1),
                                "playerNumber", equalTo(1),
                                "remotePlayerName", equalTo("Andres2"),
                                "gameStarted", is(true),
                                "turn", equalTo(2),
                                "turnCounter", equalTo(4),
                                "winner", is(false),
                                "draw", is(false),
                                "surrendered", is(false),
                                "board", equalTo(expectedBoard))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Get a Game Second Player")
        void getGameSecondPlayer(){

            //Expected Board
            List<List<Integer>> expectedBoard = getExpectedBoard(new int[][]{{2,1,0},{0,1,0},{0,0,2}});

            Header header = new Header("playerToken", "Token2");

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .get("{playerId}/games/{gameId}", 2, 1)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaGameDTO.json"))
                    .body("gameId", equalTo(1),
                            "playerNumber", equalTo(2),
                            "remotePlayerName", equalTo("Andres1"),
                            "gameStarted", is(true),
                            "turn", equalTo(2),
                            "turnCounter", equalTo(4),
                            "winner", is(false),
                            "draw", is(false),
                            "surrendered", is(false),
                            "board", equalTo(expectedBoard))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Get a game with a Winner")
        void getGameWinner(){

            //Expected
            List<List<Integer>> expectedBoard = getExpectedBoard(new int[][]{{2,1,1},{0,2,0},{0,0,2}});

            List<List<Integer>> expectedWinningCombination = getExpectedWinningCombination(new int[][]{{0,0},{1,1},{2,2}});

            Header header = new Header("playerToken", "Token1");

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .get("{playerId}/games/{gameId}", 1, 2)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaGameDTO.json"))
                    .body("gameId", equalTo(2),
                            "playerNumber", equalTo(1),
                            "remotePlayerName", equalTo("Andres2"),
                            "gameStarted", is(true),
                            "turn", equalTo(2),
                            "turnCounter", equalTo(5),
                            "winner", is(true),
                            "draw", is(false),
                            "surrendered", is(false),
                            "board", equalTo(expectedBoard),
                            "winningCombination", equalTo(expectedWinningCombination))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Get a game with a Winner Second PLayer")
        void getGameWinnerSecondPlayer(){

            //Expected
            List<List<Integer>> expectedBoard = getExpectedBoard(new int[][]{{2,1,1},{0,2,0},{0,0,2}});

            List<List<Integer>> expectedWinningCombination = getExpectedWinningCombination(new int[][]{{0,0},{1,1},{2,2}});

            Header header = new Header("playerToken", "Token2");

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .get("{playerId}/games/{gameId}", 2, 2)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaGameDTO.json"))
                    .body("gameId", equalTo(2),
                            "playerNumber", equalTo(2),
                            "remotePlayerName", equalTo("Andres1"),
                            "gameStarted", is(true),
                            "turn", equalTo(2),
                            "turnCounter", equalTo(5),
                            "winner", is(true),
                            "draw", is(false),
                            "surrendered", is(false),
                            "board", equalTo(expectedBoard),
                            "winningCombination", equalTo(expectedWinningCombination))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Get Not Started Game")
        void getNotStartedGame(){

            List<List<Integer>> expectedBoard = getExpectedBoard(new int[][]{{0,0,0},{0,0,0},{0,0,0}});

            Header header = new Header("playerToken", "Token3");

            RestAssured
                    .given()
                        .header(header)
                    .when()
                        .get("{playerId}/games/{gameId}", 3, 4)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaGameDTO.json"))
                    .body("gameId", equalTo(4),
                            "playerNumber", equalTo(1),
                            "gameStarted", is(false),
                            "turn", equalTo(0),
                            "turnCounter", equalTo(0),
                            "winner", is(false),
                            "draw", is(false),
                            "surrendered", is(false),
                            "board", equalTo(expectedBoard))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Get a Game No Authenticated")
        void getGameNoAuthenticated(){

            Header header = new Header("playerToken", "Incorrect");

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .get("{playerId}/games/{gameId}", 1, 1)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHENTICATED.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Get a Game No Authorized")
        void getGameNoAuthorized(){

            Header header = new Header("playerToken", "Token3");

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .get("{playerId}/games/{gameId}", 3, 1)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHORIZED.getExceptionMessage()))
                    .log().ifValidationFails();
        }
    }

    @Nested
    class Post{

        @Test
        @DisplayName("New Game")
        void newGame(){

            List<List<Integer>> expectedBoard = getExpectedBoard(new int[][]{{0,0,0},{0,0,0},{0,0,0}});

            Header header = new Header("playerToken", "Token3");

            RestAssured
                    .given()
                        .header(header)
                    .when()
                        .post("{playerId}/games", 3)
                    .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_CREATED)
                        .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaGameDTO.json"))
                        .body("gameId", equalTo(14),
                                "playerNumber", equalTo(1),
                                "gameStarted", is(false),
                                "turn", equalTo(0),
                                "turnCounter", equalTo(0),
                                "winner", is(false),
                                "draw", is(false),
                                "surrendered", is(false),
                                "board", equalTo(expectedBoard))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("New Game Second Player")
        void newGameSecondPlayer(){

            List<List<Integer>> expectedBoard = getExpectedBoard(new int[][]{{0,0,0},{0,0,0},{0,0,0}});

            Header header = new Header("playerToken", "Token4");

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .post("{playerId}/games", 4)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_CREATED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaGameDTO.json"))
                    .body("gameId", equalTo(3),
                            "playerNumber", equalTo(2),
                            "remotePlayerName", equalTo("Andres3"),
                            "gameStarted", is(true),
                            "turn", equalTo(2),
                            "turnCounter", equalTo(0),
                            "winner", is(false),
                            "draw", is(false),
                            "surrendered", is(false),
                            "board", equalTo(expectedBoard))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("New Game no authenticated PLayer")
        void newGameNoAuthenticatedPLayer(){
            Header header = new Header("playerToken", "Incorrect");

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .post("{playerId}/games", 4)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHENTICATED.getExceptionMessage()))
                    .log().ifValidationFails();
        }
    }

    @Nested
    class Patch{

        @Test
        @DisplayName("Make Move")
        void makeMove(){
            List<List<Integer>> expectedBoard = getExpectedBoard(new int[][]{{2,1,2},{0,1,0},{0,0,2}});

            Header header = new Header("playerToken", "Token2");
            String move = "{\"moveCol\":\"0\", \"moveRow\": \"2\"}";

            RestAssured
                    .given()
                        .header(header)
                        .contentType(ContentType.JSON)
                        .body(move)
                    .when()
                        .patch("{playerId}/games/{gameId}", 2, 5)
                    .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_ACCEPTED)
                        .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaGameDTO.json"))
                        .body("gameId", equalTo(5),
                                "playerNumber", equalTo(2),
                                "remotePlayerName", equalTo("Andres1"),
                                "gameStarted", is(true),
                                "turn", equalTo(1),
                                "turnCounter", equalTo(5),
                                "winner", is(false),
                                "draw", is(false),
                                "surrendered", is(false),
                                "board", equalTo(expectedBoard))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Make Draw Move")
        void makeDrawMove(){
            List<List<Integer>> expectedBoard = getExpectedBoard(new int[][]{{2,2,1},{1,1,2},{2,1,2}});

            Header header = new Header("playerToken", "Token2");
            String move = "{\"moveCol\":\"2\", \"moveRow\": \"2\"}";

            RestAssured
                    .given()
                    .header(header)
                    .contentType(ContentType.JSON)
                    .body(move)
                    .when()
                    .patch("{playerId}/games/{gameId}", 2, 6)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_ACCEPTED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaGameDTO.json"))
                    .body("gameId", equalTo(6),
                            "playerNumber", equalTo(2),
                            "remotePlayerName", equalTo("Andres1"),
                            "gameStarted", is(true),
                            "turn", equalTo(1),
                            "turnCounter", equalTo(9),
                            "winner", is(false),
                            "draw", is(true),
                            "surrendered", is(false),
                            "board", equalTo(expectedBoard))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Make Winning Move")
        void makeWinningMove(){
            List<List<Integer>> expectedBoard = getExpectedBoard(new int[][]{{2,1,1},{0,2,0},{0,0,2}});
            List<List<Integer>> expectedWinningCombination = getExpectedWinningCombination(new int[][]{{0,0},{1,1},{2,2}});

            Header header = new Header("playerToken", "Token2");
            String move = "{\"moveCol\":\"2\", \"moveRow\": \"2\"}";

            RestAssured
                    .given()
                    .header(header)
                    .contentType(ContentType.JSON)
                    .body(move)
                    .when()
                    .patch("{playerId}/games/{gameId}", 2, 7)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_ACCEPTED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaGameDTO.json"))
                    .body("gameId", equalTo(7),
                            "playerNumber", equalTo(2),
                            "remotePlayerName", equalTo("Andres1"),
                            "gameStarted", is(true),
                            "turn", equalTo(2),
                            "turnCounter", equalTo(5),
                            "winner", is(true),
                            "draw", is(false),
                            "surrendered", is(false),
                            "board", equalTo(expectedBoard),
                            "winningCombination", equalTo(expectedWinningCombination))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Make Move No Authenticated")
        void makeMoveNoAuthenticated(){
            Header header = new Header("playerToken", "incorrect");
            String move = "{\"moveCol\":\"2\", \"moveRow\": \"2\"}";

            RestAssured
                    .given()
                    .header(header)
                    .contentType(ContentType.JSON)
                    .body(move)
                    .when()
                    .patch("{playerId}/games/{gameId}", 1, 1)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHENTICATED.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Make Move No Authorized")
        void makeMoveNoAuthorized(){
            Header header = new Header("playerToken", "Token4");
            String move = "{\"moveCol\":\"2\", \"moveRow\": \"2\"}";

            RestAssured
                    .given()
                    .header(header)
                    .contentType(ContentType.JSON)
                    .body(move)
                    .when()
                    .patch("{playerId}/games/{gameId}", 4, 1)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHORIZED.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @ParameterizedTest
        @DisplayName("Make Move On Draw/Surrendered")
        @ValueSource( ints = {8, 9})
        void makeMoveOnDrawSurrendered(int gameId){
            Header header = new Header("playerToken", "Token1");
            String move = "{\"moveCol\":\"2\", \"moveRow\": \"2\"}";

            RestAssured
                    .given()
                    .header(header)
                    .contentType(ContentType.JSON)
                    .body(move)
                    .when()
                    .patch("{playerId}/games/{gameId}", 1, gameId)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_CONFLICT)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.INVALID_GAME_CONDITIONS.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Make Move On Incorrect Turn")
        void makeMoveOnIncorrectTurn(){
            Header header = new Header("playerToken", "Token1");
            String move = "{\"moveCol\":\"2\", \"moveRow\": \"2\"}";

            RestAssured
                    .given()
                    .header(header)
                    .contentType(ContentType.JSON)
                    .body(move)
                    .when()
                    .patch("{playerId}/games/{gameId}", 1, 2)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_CONFLICT)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_CORRECT_TURN.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Make Move On Won Game")
        void makeMoveOnWon(){
            Header header = new Header("playerToken", "Token2");
            String move = "{\"moveCol\":\"2\", \"moveRow\": \"2\"}";

            RestAssured
                    .given()
                    .header(header)
                    .contentType(ContentType.JSON)
                    .body(move)
                    .when()
                    .patch("{playerId}/games/{gameId}", 2, 2)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_CONFLICT)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.INVALID_GAME_CONDITIONS.getExceptionMessage()))
                    .log().ifValidationFails();
        }
    }

    @Nested
    class Delete{

        @Test
        @DisplayName("Delete On Turn")
        void deleteOnTurn(){
            List<List<Integer>> expectedBoard = getExpectedBoard(new int[][]{{2,1,0},{0,1,0},{0,0,2}});

            Header header = new Header("playerToken", "Token6");

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .delete("{playerId}/games/{gameId}", 6, 11)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_ACCEPTED)
                    .log().ifValidationFails();

            //Verify action done with a Get Request
            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .get("{playerId}/games/{gameId}", 6, 11)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaGameDTO.json"))
                    .body("gameId", equalTo(11),
                            "playerNumber", equalTo(2),
                            "remotePlayerName", equalTo("Andres5"),
                            "gameStarted", is(true),
                            "turn", equalTo(2),
                            "turnCounter", equalTo(4),
                            "winner", is(false),
                            "draw", is(false),
                            "surrendered", is(true),
                            "board", equalTo(expectedBoard))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Delete On Incorrect Turn")
        void deleteOnIncorrectTurn(){
            List<List<Integer>> expectedBoard = getExpectedBoard(new int[][]{{2,1,0},{0,1,0},{0,0,2}});

            Header header = new Header("playerToken", "Token5");

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .delete("{playerId}/games/{gameId}", 5, 12)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_ACCEPTED)
                    .log().ifValidationFails();

            //Verify action done with a Get Request
            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .get("{playerId}/games/{gameId}", 5, 12)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaGameDTO.json"))
                    .body("gameId", equalTo(12),
                            "playerNumber", equalTo(1),
                            "remotePlayerName", equalTo("Andres6"),
                            "gameStarted", is(true),
                            "turn", equalTo(1),
                            "turnCounter", equalTo(4),
                            "winner", is(false),
                            "draw", is(false),
                            "surrendered", is(true),
                            "board", equalTo(expectedBoard))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Delete No Started Game")
        void deleteNoStartedGame(){
            Header header = new Header("playerToken", "Token5");

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .delete("{playerId}/games/{gameId}", 5, 13)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_ACCEPTED)
                    .log().ifValidationFails();

            //Verify action done with a Get Request
            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .get("{playerId}/games/{gameId}", 5, 13)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHORIZED.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Delete No Authenticated")
        void deleteNoAuthenticated(){
            Header header = new Header("playerToken", "incorrect");

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .delete("{playerId}/games/{gameId}", 5, 12)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHENTICATED.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Delete No Authorized")
        void deleteNoAuthorized(){
            Header header = new Header("playerToken", "Token5");

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .delete("{playerId}/games/{gameId}", 5, 1)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHORIZED.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @ParameterizedTest
        @DisplayName("Delete On Draw/Surrendered")
        @ValueSource( ints = {8, 9})
        void deleteOnDrawSurrendered(int gameId){
            Header header = new Header("playerToken", "Token1");

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .delete("{playerId}/games/{gameId}", 1, gameId)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_CONFLICT)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.INVALID_GAME_CONDITIONS.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Delete On Won Game")
        void deleteOnWon(){
            Header header = new Header("playerToken", "Token2");

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .delete("{playerId}/games/{gameId}", 2, 2)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_CONFLICT)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.INVALID_GAME_CONDITIONS.getExceptionMessage()))
                    .log().ifValidationFails();
        }

    }

    //Helper functions
    private List<List<Integer>> getExpectedBoard(int[][] board){
        List<List<Integer>> expectedBoard = new ArrayList<List<Integer>>(){
            {
                add(Arrays.stream(board[0]).boxed().collect(Collectors.toList()));
                add(Arrays.stream(board[1]).boxed().collect(Collectors.toList()));
                add(Arrays.stream(board[2]).boxed().collect(Collectors.toList()));
            }
        };
        return expectedBoard;
    }

    private List<List<Integer>> getExpectedWinningCombination(int[][] winningCombination){
        List<List<Integer>> expectedWinningCombination = new ArrayList<List<Integer>>(){
            {
                add(Arrays.stream(winningCombination[0]).boxed().collect(Collectors.toList()));
                add(Arrays.stream(winningCombination[1]).boxed().collect(Collectors.toList()));
                add(Arrays.stream(winningCombination[2]).boxed().collect(Collectors.toList()));
            }
        };
        return expectedWinningCombination;
    }
}
