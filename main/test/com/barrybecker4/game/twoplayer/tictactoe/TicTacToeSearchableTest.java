/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.ISearchableHelper;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerSearchableBaseTst;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import static com.barrybecker4.game.twoplayer.tictactoe.ExpectedSearchableResults.*;


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
        getBestMovesOptions().setPercentageBestMoves(20);
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
         checkGeneratedMoves("endGameX", EXPECTED_ALL_END_GAME_MOVES_P1);
    }

    /** Load a game at the end and verify that we can get all the high value next moves. */
    @Override
    public void testGenerateTopP1MovesEndGame() {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("endGameX", EXPECTED_TOP_END_GAME_MOVES_P1);
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
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("midGameCenterO", EXPECTED_TOP_MIDDLE_GAME_MOVES_CENTER_P2);
        checkGeneratedMoves("midGameCornerO", EXPECTED_TOP_MIDDLE_GAME_MOVES_CORNER_P2);
        checkGeneratedMoves("midGameEdgeO", EXPECTED_TOP_MIDDLE_GAME_MOVES_EDGE_P2);
    }

    /**
     * Load a game at the end and verify generated endgame moves.
     * Of particular interest here is that we can generate moves that lead to a win.
     */
    @Override
    public void testGenerateAllP2MovesEndGame() {
        checkGeneratedMoves("endGameO", EXPECTED_ALL_END_GAME_MOVES_P2);
    }

     /** Load a game at the end and verify that we can get all the high value next moves. */
     @Override
     public void testGenerateTopP2MovesEndGame() {
        checkGeneratedMoves("endGameO", EXPECTED_TOP_END_GAME_MOVES_P2);
    }

    /**  Verify that we generate a correct list of urgent moves.  */
    @Override
    public void  testGenerateUrgentMoves() {

        System.out.println("GEN URGENT 1......");
        restore("urgentMoves");
        // there should not be any urgent moves at the very start of the game.
        System.out.println("lastMove="+getController().getLastMove() );   // 1,1
        MoveList moves =
            searchable.generateUrgentMoves((TwoPlayerMove)getController().getLastMove(), weights());

        checkMoveListAgainstExpected("urgentMoves", EXPECTED_URGENT_MOVES, moves);
        System.out.println("DONE GEN URGENT 1");
    }

    /**  Verify that we generate a correct list of urgent moves for the other player.  */
    public void  testGenerateUrgentMovesP2() {

        System.out.println("GEN URGENT 2......");
        restore("urgentMoves");
        // there should not be any urgent moves at the very start of the game.
        System.out.println("lastMove=" + getController().getLastMove() );   // 1,1
        MoveList moves =
            searchable.generateUrgentMoves((TwoPlayerMove)getController().getLastMove(), weights());

        checkMoveListAgainstExpected("urgentMoves", EXPECTED_URGENT_MOVES, moves);
        System.out.println("DONE GEN URGENT 2");
    }


    private void checkGeneratedMoves(String fileName, TwoPlayerMove[] expectedMoves) {
        restore(fileName);
        TwoPlayerMove lastMove = (TwoPlayerMove) getController().getLastMove();
        MoveList moves =
                getController().getSearchable().generateMoves(lastMove, weights());

        checkMoveListAgainstExpected(fileName, expectedMoves, moves);
    }
}
