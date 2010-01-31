package com.becker.game.twoplayer.tictactoe.test;

import com.becker.game.common.GameController;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.test.ISearchableHelper;
import com.becker.game.twoplayer.common.search.test.TwoPlayerSearchableBaseTst;
import com.becker.game.twoplayer.tictactoe.TicTacToeController;
import com.becker.game.twoplayer.tictactoe.TicTacToeOptions;
import com.becker.optimization.parameter.ParameterArray;
import junit.framework.Assert;
import junit.framework.TestSuite;
import junit.framework.Test;
import static com.becker.game.twoplayer.tictactoe.test.ExpectedSearchableResults.*;

import java.util.List;


/**
 * Verify that all the methods in PenteSearchable work as expected
 * @author Barry Becker
 */
public class TicTacToeSearchableTest extends TwoPlayerSearchableBaseTst {

    @Override
    protected ISearchableHelper createSearchableHelper() {
        return new TicTacToeHelper();
    }

    /**
     * @return an initial move by player one.
     */
    @Override
    protected  TwoPlayerMove createInitialMove() {
        return  TwoPlayerMove.createMove(2, 2,  0, new GamePiece(true));
    }

    @Override
    protected int getDebugLevel() {
        return 0;
    }

    /** at the very start only the center move is a candidate move */
    @Override
    protected int getExpectedNumGeneratedMovesBeforeFirstMove() {
       return 9;
   }

    @Override
    public void testNotDoneMidGame() {
        restore("midGameCenterO");
        Assert.assertFalse("Did not expect done in the middle of the game. ",
                searchable.done((TwoPlayerMove)getController().getLastMove(), false));
    }

    @Override
    public void testDoneForMidGameWin() {
        restore("wonGameO");
        Assert.assertTrue("Expected done state for this game. ",
                searchable.done((TwoPlayerMove)getController().getLastMove(), false));
    }

    /** Load a game at the last move and verify that the next move results in done == true  */
    @Override
    public void testDoneEndGame() {
         restore("endGameNoMoreMoves");
        Assert.assertTrue("Expected done state for this game because there are no more moves. ",
                searchable.done((TwoPlayerMove)getController().getLastMove(), false));
    }

    /**  Load a game in the middle and verify that we can get reasonable next moves. */
    @Override
    public void testGenerateAllP1MovesMidGame() {
        checkGeneratedMoves("midGameCenterX", EXPECTED_ALL_MIDDLE_GAME_MOVES_CENTER_P1);
        checkGeneratedMoves("midGameCornerX", EXPECTED_ALL_MIDDLE_GAME_MOVES_CORNER_P1);
        checkGeneratedMoves("midGameEdgeX", EXPECTED_ALL_MIDDLE_GAME_MOVES_EDGE_P1);
    }

    /** Load a game in the middle and verify that we can get the expected high value next moves. */
    @Override
    public void testGenerateTopP1MovesMidGame() {
        getSearchOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("midGameCenterX", EXPECTED_TOP_MIDDLE_GAME_MOVES_CENTER_P1);
        checkGeneratedMoves("midGameCornerX", EXPECTED_TOP_MIDDLE_GAME_MOVES_CORNER_P1);
        checkGeneratedMoves("midGameEdgeX", EXPECTED_TOP_MIDDLE_GAME_MOVES_EDGE_P1);
    }

    /**
      * Load a game at the end and verify that there are no valid next moves.
      * Of particular interest here is that we can generate moves that lead to a win.
      */
    @Override
    public void testGenerateAllP1MovesEndGame() {
         checkGeneratedMoves("endGameP1Turn", EXPECTED_ALL_END_GAME_MOVES_P1);
    }

    /** Load a game at the end and verify that we can get all the high value next moves. */
    @Override
    public void testGenerateTopP1MovesEndGame() {
        getSearchOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("endGameP1Turn", EXPECTED_TOP_END_GAME_MOVES_P1);
    }

    /**  Load a game in the middle and verify that we can get reasonable next moves. */
    @Override
    public void testGenerateAllP2MovesMidGame() {
        checkGeneratedMoves("midGameCenterO", EXPECTED_ALL_MIDDLE_GAME_MOVES_CENTER_P2);
        checkGeneratedMoves("midGameCornerO", EXPECTED_ALL_MIDDLE_GAME_MOVES_CORNER_P2);
        checkGeneratedMoves("midGameEdgeO", EXPECTED_ALL_MIDDLE_GAME_MOVES_EDGE_P2);
    }

    /**  Load a game in the middle and verify that we can get the expected high value next moves. */
    @Override
    public void testGenerateTopP2MovesMidGame() {
        getSearchOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("midGameCenterO", EXPECTED_TOP_MIDDLE_GAME_MOVES_CENTER_P2);
        checkGeneratedMoves("midGameCornerO", EXPECTED_TOP_MIDDLE_GAME_MOVES_CORNER_P2);
        checkGeneratedMoves("midGameEdgeO", EXPECTED_TOP_MIDDLE_GAME_MOVES_EDGE_P2);
    }

    /**
     * Load a game at the end and verify that there are no valid next moves.
     * Of particular interest here is that we can generate moves that lead to a win.
     */
    @Override
    public void testGenerateAllP2MovesEndGame() {
        checkGeneratedMoves("endGameP2Turn", EXPECTED_ALL_END_GAME_MOVES_P2);
    }

     /** Load a game at the end and verify that we can get all the high value next moves. */
     @Override
     public void testGenerateTopP2MovesEndGame() {
        getSearchOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("endGameP2Turn", EXPECTED_TOP_END_GAME_MOVES_P2);
    }

/**  Verify that we generate a correct list of urgent moves.  */
    @Override
    public void  testGenerateUrgentMoves() {

        restore("urgentMoves");
        // there should not be any urgent moves at the very start of the game.
        List<? extends TwoPlayerMove> moves =
            searchable.generateUrgentMoves((TwoPlayerMove)getController().getLastMove(),
                                           getController().getComputerWeights().getPlayer1Weights(), true);

        checkMoveListAgainstExpected("urgentMoves", EXPECTED_URGENT_MOVES, moves);
    }


    private void checkGeneratedMoves(String fileName, TwoPlayerMove[] expectedMoves) {
        restore(fileName);
        ParameterArray wts = getController().getComputerWeights().getPlayer1Weights();
        TwoPlayerMove lastMove = (TwoPlayerMove) getController().getLastMove();
        List<? extends TwoPlayerMove> moves =
                getController().getSearchable().generateMoves(lastMove, wts, !lastMove.isPlayer1());

        checkMoveListAgainstExpected(fileName, expectedMoves, moves);
    }

    private void checkMoveListAgainstExpected(String title, TwoPlayerMove[] expectedMoves,
                                              List<? extends TwoPlayerMove> moves) {
        if (expectedMoves.length != moves.size()) {
            printMoves( title, moves);
        }

        Assert.assertEquals("Unexpected number of generated moves.",
                expectedMoves.length, moves.size());

        StringBuilder diffs = new StringBuilder("");
        for (int i=0; i<moves.size(); i++) {
            TwoPlayerMove move = moves.get(i);
            TwoPlayerMove expMove = expectedMoves[i];
            if (!move.equals(expMove)) {
                diffs.append(i);
                diffs.append(") Unexpected moves.\n Expected ");
                diffs.append(expMove);
                diffs.append(" \nBut got ");
                diffs.append(move);
                diffs.append("\n");
            }
        }
        if (diffs.length() > 0) {
            printMoves( title, moves);
        }
        Assert.assertTrue("There were unexpected generated moves for " + title +"\n" + diffs,
                    diffs.length() == 0);
    }

    public static Test suite() {
        return new TestSuite(TicTacToeSearchableTest.class);
    }
}
