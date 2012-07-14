/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.ISearchableHelper;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerSearchableBaseTst;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import static com.barrybecker4.game.twoplayer.pente.ExpectedSearchableResults.*;


/**
 * Verify that all the methods in PenteSearchable work as expected
 * @author Barry Becker
 */
public class PenteSearchableTest extends TwoPlayerSearchableBaseTst {

    @Override
    protected ISearchableHelper createSearchableHelper() {
        return new PenteHelper();
    }

    /**
     * @return an initial move by player one.
     */
    @Override
    protected TwoPlayerMove createInitialMove() {
        return TwoPlayerMove.createMove(5, 5,   0, new GamePiece(true));
    }

    /** at the very start only the center move is a candidate move */
    @Override
    protected int getExpectedNumGeneratedMovesBeforeFirstMove() {
       return 1;
   }

    @Override
    public void testNotDoneMidGame() {
        restore("midGameP1ToPlay");
        Assert.assertFalse("Did not expect done in the middle of the game. ",
                searchable.done((TwoPlayerMove)getController().getLastMove(), false));
    }

    @Override
    public void testDoneForMidGameWin() {
        restore("wonGameP1");
        Assert.assertTrue("Expected done state for this game. ",
                searchable.done((TwoPlayerMove)getController().getLastMove(), false));
        restore("wonGameP2");
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
        checkGeneratedMoves("midGameP1ToPlay8x8", EXPECTED_ALL_MIDDLE_GAME_MOVES_P1);
    }

    /** Load a game in the middle and verify that we can get the expected high value next moves. */
    @Override
    public void testGenerateTopP1MovesMidGame() {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("midGameP1ToPlay8x8", EXPECTED_TOP_MIDDLE_GAME_MOVES_P1);
    }

    /**
      * Load a game at the end and verify the generated endgame moves.
      * Of particular interest here is that we can generate moves that lead to a win.
      */
    @Override
    public void testGenerateAllP1MovesEndGame() {
         checkGeneratedMoves("endGameP1ToPlay8x8", EXPECTED_ALL_END_GAME_MOVES_P1);
    }

    /** Load a game at the end and verify that we can get all the high value next moves. */
    @Override
    public void testGenerateTopP1MovesEndGame() {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("endGameP1ToPlay8x8", EXPECTED_TOP_END_GAME_MOVES_P1);
    }

    /**  Load a game in the middle and verify that we can get reasonable next moves. */
    @Override
    public void testGenerateAllP2MovesMidGame() {
        checkGeneratedMoves("midGameP2ToPlay8x8", EXPECTED_ALL_MIDDLE_GAME_MOVES_P2);
    }

    /**  Load a game in the middle and verify that we can get the expected high value next moves. */
    @Override
    public void testGenerateTopP2MovesMidGame() {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("midGameP2ToPlay8x8", EXPECTED_TOP_MIDDLE_GAME_MOVES_P2);
    }

    /**
     * Load a game at the end and verify that there are no valid next moves.
     * Of particular interest here is that we can generate moves that lead to a win.
     */
    @Override
    public void testGenerateAllP2MovesEndGame() {
        checkGeneratedMoves("endGameP2ToPlay8x8", EXPECTED_ALL_END_GAME_MOVES_P2);
    }

     /** Load a game at the end and verify that we can get all the high value next moves. */
     @Override
     public void testGenerateTopP2MovesEndGame() {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("endGameP2ToPlay8x8", EXPECTED_TOP_END_GAME_MOVES_P2);
    }

    /**  Verify that we generate a correct list of urgent moves.  */
    @Override
    public void testGenerateUrgentMoves() {

        restore("urgentMoveP1ToPlay");
        // there should not be any urgent moves at the very start of the game.
        MoveList moves =
            searchable.generateUrgentMoves((TwoPlayerMove)getController().getLastMove(), weights());

        checkMoveListAgainstExpected("urgentMoveP1ToPlay", EXPECTED_URGENT_MOVES_P1, moves);
    }

    /**  Verify that we generate a correct list of urgent moves.  */
    public void testGenerateUrgentMovesP2() {

        restore("urgentMoveP2ToPlay");
        // there should not be any urgent moves at the very start of the game.
        MoveList moves =
            searchable.generateUrgentMoves((TwoPlayerMove)getController().getLastMove(), weights());

        checkMoveListAgainstExpected("urgentMoveP2ToPlay", EXPECTED_URGENT_MOVES_P2, moves);
    }


    private void checkGeneratedMoves(String fileName, TwoPlayerMove[] expectedMoves) {
        restore(fileName);
        TwoPlayerMove lastMove = (TwoPlayerMove) getController().getLastMove();
        MoveList moves =
                getController().getSearchable().generateMoves(lastMove, weights());

        checkMoveListAgainstExpected(fileName, expectedMoves, moves);
    }

    public static Test suite() {
        return new TestSuite(PenteSearchableTest.class);
    }
}
