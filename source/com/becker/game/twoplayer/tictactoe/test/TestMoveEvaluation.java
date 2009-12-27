package com.becker.game.twoplayer.tictactoe.test;

import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.pente.analysis.Direction;
import com.becker.game.twoplayer.pente.analysis.Line;
import com.becker.game.twoplayer.pente.analysis.MoveEvaluator;
import com.becker.game.twoplayer.tictactoe.TicTacToeBoard;
import com.becker.game.twoplayer.tictactoe.TicTacToePatterns;
import com.becker.game.twoplayer.tictactoe.TicTacToeWeights;
import junit.framework.TestSuite;
import junit.framework.Test;
import junit.framework.TestCase;

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
public class TestMoveEvaluation extends TestCase  {

    MoveEvaluator evaluator;

    TicTacToeWeights weights;

    TicTacToeBoard board;

    /**
     * common initialization for all go test cases.
     * Override setOptionOverides if you want different search parameters.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        board = new TicTacToeBoard();
        weights = new TicTacToeWeights();
        evaluator = new MoveEvaluator(board, new TicTacToePatterns());
    }

    public void test__OEvaluation() {

        verifyAllDirectionsResult(2, 1, false, -4, 0);
        verifyAllDirectionsResult(2, 3, false, -4, 0);
    }

    public void test_O_Evaluation() {

        verifyAllDirectionsResult(2, 2, false, -16, -16);
    }

    public void test_X_Evaluation() {

        verifyAllDirectionsResult(2, 2, true, 16, 16);
    }

    public void test_XOEvaluation() {

        TwoPlayerMove move = TwoPlayerMove.createMove(2, 2,  0, new GamePiece(true));
        board.makeMove(move);

        verifyAllDirectionsResult(2, 1, false, -4, 0);
        verifyAllDirectionsResult(2, 3, false, -4, 0);
    }

    public void test_OOEvaluation() {

        TwoPlayerMove move = TwoPlayerMove.createMove(2, 2,  0, new GamePiece(false));
        board.makeMove(move);

        verifyAllDirectionsResult(2, 1, false, -44, -40);
        verifyAllDirectionsResult(2, 3, false, -44, -40);
    }

    public void test_XXEvaluation() {

        TwoPlayerMove move = TwoPlayerMove.createMove(2, 2,  0, new GamePiece(true));
        board.makeMove(move);

        verifyAllDirectionsResult(2, 1, true, 44, 40);
        verifyAllDirectionsResult(2, 3, true, 44, 40);
    }

    public void testOOOEvaluation() {

        TwoPlayerMove move1 = TwoPlayerMove.createMove(2, 2,  0, new GamePiece(false));
        board.makeMove(move1);
        
        TwoPlayerMove move2 = TwoPlayerMove.createMove(2, 1,  0, new GamePiece(false));
        board.makeMove(move2);
        verifyResult(Direction.HORIZONTAL, 2, 3, false, -7960);
        board.undoMove();

        move2 = TwoPlayerMove.createMove(1, 2,  0, new GamePiece(false));
        board.makeMove(move2);
        verifyResult(Direction.VERTICAL, 3, 2, false, -7960);
        board.undoMove();

        move2 = TwoPlayerMove.createMove(3, 1,  0, new GamePiece(false));
        board.makeMove(move2);
        verifyResult(Direction.UP_DIAGONAL, 1, 3, false, -7956);
        board.undoMove();

         move2 = TwoPlayerMove.createMove(1, 1,  0, new GamePiece(false));
         board.makeMove(move2);
         verifyResult(Direction.DOWN_DIAGONAL, 3, 3, false, -7956);
         board.undoMove();
    }


    private void verifyAllDirectionsResult(int row, int col, boolean player1, int expectedStraightWorth, int expectedDiagonalWorth) {
        verifyStraightDirectionsResult( row, col, player1, expectedStraightWorth);
        verifyDiagonalDirectionsResult( row, col, player1, expectedDiagonalWorth);
    }
    private void verifyStraightDirectionsResult(int row, int col, boolean player1, int expectedWorth) {
        verifyResult(Direction.HORIZONTAL, row, col, player1, expectedWorth);
        verifyResult(Direction.VERTICAL, col, row, player1, expectedWorth);
    }

    private void verifyDiagonalDirectionsResult(int row, int col, boolean player1, int expectedWorth) {

        verifyResult(Direction.UP_DIAGONAL, col, TicTacToePatterns.WIN_RUN_LENGTH + 1 - col, player1, expectedWorth);
        verifyResult(Direction.DOWN_DIAGONAL, col, col, player1, expectedWorth);
    }

    /**
     * Verify that we get the expected worth.
     * @param row row position of move to verify.
     * @param col column position of move to verify.
     * @param expectedWorth how much the move contributes to the overall worth.
     */
    private void verifyResult(Direction dir, int row, int col, boolean player1, int expectedWorth) {
        TwoPlayerMove lastMove = TwoPlayerMove.createMove(row, col,  0, new GamePiece(player1));
        board.makeMove(lastMove);
        int worth = evaluator.worth(lastMove, weights.getDefaultWeights());
        assertEquals("[" + dir + "] Unexpected worth for move " + lastMove, expectedWorth, worth);
        board.undoMove();
    }

    public static Test suite() {
        return new TestSuite(TestMoveEvaluation.class);
    }
}
