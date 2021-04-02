package tttHttp.TicTacToeTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tttHttp.DTO.GameDTO;
import tttHttp.tictactoe.TicTacToe;

import java.util.Arrays;

public class TicTacToeTests {

    private TicTacToe ttt;


    @BeforeEach
    void setUp() {
        ttt = new TicTacToe(2, 0, false, false, false, newBoard());
    }

    /* TODO:
    * Tests:
    * - Make Move:
    *   - Normal
    *   - Winning (x4)
    *   - Tables
    * - Boundaries
    * - Invalid turn
    * - Override Tile
    * - On Winner
    * - On Draw
    * - On Surrender
    * */

    @Test
    @DisplayName("Make Move")
    void makeMoveTest() {
        //Expected
        int[][] expectedBoard = newBoard();
        expectedBoard[0][1] = 2;
        GameDTO expectedGameDTO = new GameDTO(1, 1, false, false, false, expectedBoard);

        //Actual
        boolean actualMakeMoveReturn = ttt.makeMove(2, 0, 1);
        GameDTO actualGameDTO = ttt.getGameDTO();

        assertAll(
                () -> assertTrue(actualMakeMoveReturn),
                () -> assertEquals(expectedGameDTO, actualGameDTO)
        );
    }

    @Nested
    class WinningCombinations {

        @Test
        @DisplayName("Horizontal Win")
        void horizontalWinTest() {
            //Expected
            int[][] expectedBoard = newBoard();
            expectedBoard[0][2] = 2;
            expectedBoard[0][0] = 1;
            expectedBoard[1][2] = 2;
            expectedBoard[0][1] = 1;
            expectedBoard[2][2] = 2;
            GameDTO expectedGameDTO = new GameDTO(2, 5, true, false, false, expectedBoard);

            //Actual
            ttt.makeMove(2, 0,2);
            ttt.makeMove(1, 0,0);
            ttt.makeMove(2, 1,2);
            ttt.makeMove(1, 0,1);
            ttt.makeMove(2, 2,2);
            GameDTO actualGameDTO = ttt.getGameDTO();

            assertEquals(expectedGameDTO, actualGameDTO);
        }

        @Test
        @DisplayName("Vertical Win")
        void verticalWinTest() {
            //Expected
            int[][] expectedBoard = newBoard();
            expectedBoard[1][1] = 2;
            expectedBoard[0][0] = 1;
            expectedBoard[1][2] = 2;
            expectedBoard[0][1] = 1;
            expectedBoard[1][0] = 2;
            GameDTO expectedGameDTO = new GameDTO(2, 5, true, false, false, expectedBoard);

            //Actual
            ttt.makeMove(2, 1,1);
            ttt.makeMove(1, 0,0);
            ttt.makeMove(2, 1,2);
            ttt.makeMove(1, 0,1);
            ttt.makeMove(2, 1,0);
            GameDTO actualGameDTO = ttt.getGameDTO();

            assertEquals(expectedGameDTO, actualGameDTO);
        }

        @Test
        @DisplayName("Diagonal Forward Win")
        void diagonalForwardWinTest() {
            //Expected
            int[][] expectedBoard = newBoard();
            expectedBoard[0][0] = 2;
            expectedBoard[0][2] = 1;
            expectedBoard[1][1] = 2;
            expectedBoard[0][1] = 1;
            expectedBoard[2][2] = 2;
            GameDTO expectedGameDTO = new GameDTO(2, 5, true, false, false, expectedBoard);

            //Actual
            ttt.makeMove(2, 0,0);
            ttt.makeMove(1, 0,2);
            ttt.makeMove(2, 1,1);
            ttt.makeMove(1, 0,1);
            ttt.makeMove(2, 2,2);
            GameDTO actualGameDTO = ttt.getGameDTO();

            assertEquals(expectedGameDTO, actualGameDTO);
        }

        @Test
        @DisplayName("Diagonal Backward Win")
        void diagonalBackwardWinTest() {
            //Expected
            int[][] expectedBoard = newBoard();
            expectedBoard[0][0] = 2;
            expectedBoard[0][2] = 1;
            expectedBoard[1][2] = 2;
            expectedBoard[1][1] = 1;
            expectedBoard[2][2] = 2;
            expectedBoard[2][0] = 1;
            GameDTO expectedGameDTO = new GameDTO(1, 6, true, false, false, expectedBoard);

            //Actual
            ttt.makeMove(2, 0,0);
            ttt.makeMove(1, 0,2);
            ttt.makeMove(2, 1,2);
            ttt.makeMove(1, 1,1);
            ttt.makeMove(2, 2,2);
            ttt.makeMove(1, 2,0);
            GameDTO actualGameDTO = ttt.getGameDTO();

            assertEquals(expectedGameDTO, actualGameDTO);
        }
    }

    @Test
    @DisplayName("Is Draw")
    void drawTest(){
        ttt.makeMove(2, 0, 0);
        ttt.makeMove(1, 0, 1);
        ttt.makeMove(2, 0, 2);

        ttt.makeMove(1, 1, 1);
        ttt.makeMove(2, 1, 0);
        ttt.makeMove(1, 1, 2);

        ttt.makeMove(2, 2, 1);
        ttt.makeMove(1, 2, 0);
        ttt.makeMove(2, 2, 2);

        boolean isDraw = ttt.getGameDTO().isDraw();

        assertTrue(isDraw);
    }

    @ParameterizedTest
    @DisplayName("False on Board Boundaries")
    @CsvSource({"-1,-1", "99, 99", "0, -1", "-1, 0", "99, 0", "0, 99"})
    void boardBoundariesTest(int tileCol, int tileRow){
        assertFalse(ttt.makeMove(2, tileCol, tileRow));
    }

    @Test
    @DisplayName("No Move on Invalid Turn")
    void invalidTurnTest(){
        assertFalse(ttt.makeMove(1, 1, 1));
    }

    @Test
    @DisplayName("No Move Overriding Tile")
    void overridingTileTest(){
        ttt.makeMove(2, 1, 1);
        assertFalse(ttt.makeMove(1, 1, 1));
    }

    @Test
    @DisplayName("No Move on Winner condition")
    void noMoveOnWinnerTest(){
        ttt.makeMove(2, 1,1);
        ttt.makeMove(1, 0,0);
        ttt.makeMove(2, 1,2);
        ttt.makeMove(1, 0,1);
        ttt.makeMove(2, 1,0);

        assertFalse(ttt.makeMove(1, 1, 2));
    }

    @Test
    @DisplayName("No Move on Draw")
    void noMoveOnDrawTest(){
        ttt.makeMove(2, 0, 0);
        ttt.makeMove(1, 0, 1);
        ttt.makeMove(2, 0, 2);

        ttt.makeMove(1, 1, 1);
        ttt.makeMove(2, 1, 0);
        ttt.makeMove(1, 1, 2);

        ttt.makeMove(2, 2, 1);
        ttt.makeMove(1, 2, 0);
        ttt.makeMove(2, 2, 2);

        assertFalse(ttt.makeMove(1,1,1));
    }

    @Test
    @DisplayName("No Move on Draw")
    void noMoveOnSurrenderTest(){
        ttt.makeMove(2, 0, 0);
        ttt.setSurrendered(true);

        assertFalse(ttt.makeMove(1,1,1));
    }


    private int[][] newBoard(){
        int[][] boardTemplate = new int[3][3];
        for(int i = 0; i < boardTemplate.length; i++){
            Arrays.fill(boardTemplate[i],0);
        }
        return boardTemplate;
    }
}
