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

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;

public class PlayerResourceRestAssuredTests {

    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "http://localhost:8086/TicTacToe_HTTP_war_exploded/webapi/players/";
        DDBBTestingDataLoader.loadDDBBTestingData(PlayerResourceRestAssuredTests.class.getResource("/DDBBTestingSQL" +
                "/DDBBTestingDataPlayers.sql").getFile());
    }

    @AfterAll
    static void tearDown(){
        DDBBTestingDataLoader.loadDDBBTestingData(PlayerResourceRestAssuredTests.class.getResource("/DDBBTestingSQL" +
                "/DDBBTestingDataTearDown.sql").getFile());
    }

    /*
     * Tests for Player:
     * - Get:
     *  * Happy path with Games
     *  * Happy path no Games
     *  * Happy path with no started Game
     *  * Deleted Player
     *  * Incorrect Token
     *  * Null Token Header
     *  * No Token Header
     *  * No Player (Max_integer, Min_integer)
     * - Post:
     *  * Happy Path
     *  * Name Input Boundaries (empty, <3 Or >10)
     *  * Incorrect Json Key
     *  * Null Json
     * - Patch:
     *  * Happy Path
     *  * Incorrect Token
     *  * Null Token Header
     *  * No Token Header
     *  * Name Input Boundaries (empty, <3 Or >10)
     *  * Incorrect Json Key
     *  * Null Json
     * - Delete:
     *  * Happy Path with Game
     *  * Happy Path with no Game
     *  * Happy Path with no started Game
     *  * Incorrect Token
     *  * Null Token Header
     *  * No Token Header
     */

    //TODO: Fix all ErrorMessages: Documentation and not ErrorCode

    @Nested
    class GetPlayer{

        @Test
        @DisplayName("Getting a Player with Games")
        void getPlayerWithGames(){

            Header headerPlayerToken = new Header("playerToken", "Token1");

            RestAssured
                    .given()
                        .header(headerPlayerToken)
                    .when()
                        .get("/{playerId}", 1)
                    .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaPlayerDTO.json"))
                        .body("playerId", equalTo(1),
                                "playerName", equalTo("Andres1"),
                                "gamesIds", hasItems(1, 3))
                        .log().ifValidationFails();

            //TODO: Update "gameIds" once the Game Tests are done
        }

        @Test
        @DisplayName("Getting a Player with no Games")
        void getPlayerNoGames(){

            Header headerPlayerToken = new Header("playerToken", "Token2");

            RestAssured
                    .given()
                    .header(headerPlayerToken)
                    .when()
                    .get("/{playerId}", 2)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaPlayerDTO.json"))
                    .body("playerId", equalTo(2),
                            "playerName", equalTo("Andres2"),
                            "gamesIds", anyOf(nullValue(),empty()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Getting a Player with no started Games")
        void getPlayerNoStartedGames(){

            Header headerPlayerToken = new Header("playerToken", "Token3");

            RestAssured
                    .given()
                    .header(headerPlayerToken)
                    .when()
                    .get("/{playerId}", 3)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaPlayerDTO.json"))
                    .body("playerId", equalTo(3),
                            "playerName", equalTo("Andres3"),
                            "gamesIds", hasItems(2))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Getting a Deleted Player")
        void getDeletedPlayer(){

            Header headerPlayerToken = new Header("playerToken", "Token9");

            RestAssured
                    .given()
                        .header(headerPlayerToken)
                    .when()
                        .get("/{playerId}", 9)
                    .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_NOT_FOUND)
                        .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                        .body("errorMessage", equalTo(ExceptionsEnum.NOT_FOUND.getExceptionMessage()))
                        .log().ifValidationFails();
        }

        @Test
        @DisplayName("Get Player Incorrect Token")
        void getPlayerIncorrectToken(){

            Header headerPlayerToken = new Header("playerToken", "IncorrectToken");

            RestAssured
                    .given()
                    .header(headerPlayerToken)
                    .when()
                    .get("/{playerId}", 1)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHENTICATED.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Get Player Null Token")
        void getPlayerNoToken(){

            Header headerPlayerToken = new Header("playerToken", null);

            RestAssured
                    .given()
                    .header(headerPlayerToken)
                    .when()
                    .get("/{playerId}", 1)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHENTICATED.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Get Player No Token Header")
        void getPlayerNoTokenHeader(){

            RestAssured
                    .given()
                    .when()
                    .get("/{playerId}", 1)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHENTICATED.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @ParameterizedTest
        @DisplayName("Get Non-Existing Player")
        @ValueSource(ints = {Integer.MAX_VALUE, Integer.MIN_VALUE})
        void getPlayerNonExisting(int playerId){

            Header headerPlayerToken = new Header("playerToken", "Token");

            RestAssured
                    .given()
                    .header(headerPlayerToken)
                    .when()
                    .get("/{playerId}", playerId)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NOT_FOUND.getExceptionMessage()))
                    .log().ifValidationFails();
        }
    }

    @Nested
    class PostPlayer{

        @Test
        @DisplayName("New Player")
        void newPlayer(){

            String jsonBody = "{\"playerName\": \"Andres\"}";

            RestAssured
                    .given()
                        .contentType(ContentType.JSON)
                        .body(jsonBody)
                    .when()
                        .post()
                    .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_CREATED)
                        .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaNewPlayerDTO.json"))
                        .body("playerName", equalTo("Andres"),
                                "playerId", equalTo(10))
                        .log().ifValidationFails();
        }

        //Boundaries: 3 => NewName <= 10 or empty
        @ParameterizedTest
        @DisplayName("New Player Name Boundaries")
        @ValueSource(strings = {"", "aa", "aaaaaaaaaaa"})
        void newPlayerBoundariesName(String newPlayerName){

            String jsonBody = "{\"playerName\": \"" + newPlayerName + "\"}";

            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(jsonBody)
                    .when()
                    .post()
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.INVALID_INPUT.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("New Player Incorrect Json Key")
        void newPlayerIncorrectJsonKey(){

            String jsonBody = "{\"playerNameXXXX\": \"Andres\"}";

            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(jsonBody)
                    .when()
                    .post()
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.INVALID_INPUT.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("New Player Null Json Value")
        void newPlayerNullJson(){

            String jsonBody = "{\"playerName\": null}";

            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(jsonBody)
                    .when()
                    .post()
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.INVALID_INPUT.getExceptionMessage()))
                    .log().ifValidationFails();
        }
    }

    @Nested
    class PatchPlayer{

        @Test
        @DisplayName("Patch Player")
        void patchPlayer(){

            Header header = new Header("playerToken", "Token4");
            String newPlayerName = "{\"playerName\": \"AndresNew\"}";

            RestAssured
                    .given()
                        .header(header)
                        .contentType(ContentType.JSON)
                        .body(newPlayerName)
                    .when()
                        .patch("/{playerId}", 4)
                    .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_ACCEPTED)
                        .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaPlayerDTO.json"))
                        .body("playerId", equalTo(4),
                                "playerName", equalTo("AndresNew"))
                        .log().ifValidationFails();
        }

        @Test
        @DisplayName("Patch Player Incorrect Token")
        void patchPlayerIncorrectToken(){

            Header header = new Header("playerToken", "Incorrect");
            String newPlayerName = "{\"playerName\": \"AndresNew\"}";

            RestAssured
                    .given()
                    .header(header)
                    .contentType(ContentType.JSON)
                    .body(newPlayerName)
                    .when()
                    .patch("/{playerId}", 4)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHENTICATED.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Patch Player Null Token")
        void patchPlayerNullToken(){

            Header header = new Header("playerToken", null);
            String newPlayerName = "{\"playerName\": \"AndresNew\"}";

            RestAssured
                    .given()
                    .header(header)
                    .contentType(ContentType.JSON)
                    .body(newPlayerName)
                    .when()
                    .patch("/{playerId}", 4)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHENTICATED.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Patch Player No Token Header")
        void patchPlayerNoTokenHeader(){

            String newPlayerName = "{\"playerName\": \"AndresNew\"}";

            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(newPlayerName)
                    .when()
                    .patch("/{playerId}", 4)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHENTICATED.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        //Boundaries: 3 => NewName <= 10 or empty
        @ParameterizedTest
        @DisplayName("Patch Player Name Boundaries")
        @ValueSource(strings = {"", "aa", "aaaaaaaaaaa"})
        void patchPlayerBoundariesName(String newPlayerName){

            Header header = new Header("playerToken", "Token4");
            String jsonBody = "{\"playerName\": \"" + newPlayerName + "\"}";

            RestAssured
                    .given()
                    .header(header)
                    .contentType(ContentType.JSON)
                    .body(jsonBody)
                    .when()
                    .patch("/{playerId}", 4)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.INVALID_INPUT.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Patch Player Incorrect Json Key")
        void patchPlayerIncorrectJsonKey(){

            Header header = new Header("playerToken", "Token4");
            String jsonBody = "{\"playerNameXXXX\": \"AndresNew\"}";

            RestAssured
                    .given()
                    .header(header)
                    .contentType(ContentType.JSON)
                    .body(jsonBody)
                    .when()
                    .patch("/{playerId}", 4)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.INVALID_INPUT.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Patch Player Null Json Value")
        void patchPlayerNullJson(){

            Header header = new Header("playerToken", "Token4");
            String jsonBody = "{\"playerName\": null}";

            RestAssured
                    .given()
                    .header(header)
                    .contentType(ContentType.JSON)
                    .body(jsonBody)
                    .when()
                    .patch("/{playerId}", 4)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.INVALID_INPUT.getExceptionMessage()))
                    .log().ifValidationFails();
        }
    }

    @Nested
    class DeletePlayer{

        @Test
        @DisplayName("Delete a Player with Games")
        void deletePlayerWithGames(){

            Header header = new Header("playerToken", "Token5");

            //Delete Player

            RestAssured
                    .given()
                        .header(header)
                    .when()
                        .delete("/{playerId}", 5)
                    .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_ACCEPTED)
                        .log().ifValidationFails();

            //Ensure is deleted trying to get it

            RestAssured
                    .given()
                        .header(header)
                    .when()
                        .get("{playerId}", 5)
                    .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_NOT_FOUND)
                        .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                        .body("errorMessage", equalTo(ExceptionsEnum.NOT_FOUND.getExceptionMessage()))
                        .log().ifValidationFails();

            //Ensure Games were set to surrendered (Get from the another Player, as it has been deleted)

            Header header2 = new Header("playerToken", "Token1");

            RestAssured
                    .given()
                        .header(header2)
                    .when()
                        .get("{playerId}/games/{gameId}", 1, 3)
                    .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaGameDTO.json"))
                        .body("turn", equalTo(5),
                                "surrendered", equalTo(true))
                        .log().ifValidationFails();
        }

        @Test
        @DisplayName("Delete a Player with no Games")
        void deletePlayerWithNoGames(){

            Header header = new Header("playerToken", "Token6");

            //Delete a Player

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .delete("/{playerId}", 6)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_ACCEPTED)
                    .log().ifValidationFails();

            //Ensure is deleted trying to access it

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .get("{playerId}", 6)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NOT_FOUND.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Delete Player No Started Game")
        void deletePlayerNoStartedGame(){

            Header header = new Header("playerToken", "Token7");

            //Delete Player

            RestAssured
                    .given()
                        .header(header)
                    .when()
                        .delete("{playerId}", 7)
                    .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_ACCEPTED)
                        .log().ifValidationFails();

            //Ensure is deleted trying to access it

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .get("{playerId}", 7)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NOT_FOUND.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Delete Player Incorrect Token")
        void deletePlayerIncorrectToken(){

            Header header = new Header("playerToken", "Incorrect");

            //Delete Player

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .delete("{playerId}", 8)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHENTICATED.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Delete Player Null Token Header")
        void deletePlayerNullToken(){

            Header header = new Header("playerToken", null);

            //Delete Player

            RestAssured
                    .given()
                    .header(header)
                    .when()
                    .delete("{playerId}", 8)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHENTICATED.getExceptionMessage()))
                    .log().ifValidationFails();
        }

        @Test
        @DisplayName("Delete Player No Token Header")
        void deletePlayerNoToken(){

            //Delete Player

            RestAssured
                    .given()
                    .when()
                    .delete("{playerId}", 8)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body(matchesJsonSchemaInClasspath("JsonSchemas/JsonSchemaErrorMessageDTO.json"))
                    .body("errorMessage", equalTo(ExceptionsEnum.NO_AUTHENTICATED.getExceptionMessage()))
                    .log().ifValidationFails();
        }
    }
}
