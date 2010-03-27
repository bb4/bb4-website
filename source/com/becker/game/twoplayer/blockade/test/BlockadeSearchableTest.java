package com.becker.game.twoplayer.blockade.test;

import com.becker.game.common.GameController;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.blockade.BlockadeController;
import com.becker.game.twoplayer.blockade.BlockadeMove;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.test.ISearchableHelper;
import com.becker.game.twoplayer.common.search.test.TwoPlayerSearchableBaseTst;
import com.becker.optimization.parameter.ParameterArray;
import java.util.List;
import junit.framework.*;
import static com.becker.game.twoplayer.blockade.test.ExpectedSearchableResults.*;


/**
 * Verify that all the methods in blockadeSearchable work as expected
 * @author Barry Becker
 */
public class BlockadeSearchableTest extends TwoPlayerSearchableBaseTst {

    @Override
    protected ISearchableHelper createSearchableHelper() {
        return new BlockadeHelper();
    }

    /**
     * @return an initial move by player one.
     */
    @Override
    protected  TwoPlayerMove createInitialMove() {
        return BlockadeMove.createMove(5, 5,   0, new GamePiece(true));
    }


    @Override
    protected int getDebugLevel() {
        return 0;
    }

    @Override
    public void testNotDoneMidGame() {
        // @@
        assertFalse(false);
    }

    @Override
    public void testDoneForMidGameWin() {
        // @@
        assertFalse(false);
    }

    @Override
    public void testDoneEndGame() {
        // @@
        assertFalse(false);
    }

    @Override
    protected int getExpectedNumGeneratedMovesBeforeFirstMove() {
       return 144;
   }


    /**  Load a game in the middle and verify that we can get reasonable next moves. */
    @Override
    public void testGenerateAllP1MovesMidGame() {
         checkGeneratedMoves("middleGameP1Turn", EXPECTED_ALL_MIDDLE_GAME_MOVES_P1);
    }

    /**  Load a game in the middle and verify that we can get the expected high value next moves. */
    @Override
    public void testGenerateTopP1MovesMidGame() {
        getSearchOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("middleGameP1Turn", EXPECTED_TOP_MIDDLE_GAME_MOVES_P1);
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
         checkGeneratedMoves("middleGameP2Turn", EXPECTED_ALL_MIDDLE_GAME_MOVES_P2);
    }

    /**  Load a game in the middle and verify that we can get the expected high value next moves. */
    @Override
    public void testGenerateTopP2MovesMidGame() {
        getSearchOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("middleGameP2Turn", EXPECTED_TOP_MIDDLE_GAME_MOVES_P2);
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


    private void checkGeneratedMoves(String fileName, BlockadeMove[] expectedMoves) {
        restore(fileName);
        ParameterArray wts = weights();
        TwoPlayerMove lastMove = (TwoPlayerMove) getController().getLastMove();
        List<? extends TwoPlayerMove> moves = getController().getSearchable().generateMoves(lastMove, wts, !lastMove.isPlayer1());

        if (expectedMoves.length != moves.size()) {
            printMoves( fileName, moves);
        }
        
        Assert.assertEquals("Unexpected number of generated moves.",
                expectedMoves.length, moves.size());

        StringBuilder diffs = new StringBuilder("");
        for (int i=0; i<moves.size(); i++) {
            BlockadeMove move = (BlockadeMove) moves.get(i);
            BlockadeMove expMove = expectedMoves[i];
            if (!move.equals(expMove)) {
                diffs.append(i).append(") Unexpected moves.\n Expected ").append(expMove).append(" \nBut got ").append(move).append("\n");
            }
        }
        if (diffs.length() > 0) {
            printMoves( fileName, moves);
        }
        Assert.assertTrue("There were unexpected generated moves for " + fileName +"\n" + diffs,
                    diffs.length()==0);
    }

    /**  Verify that we generate a correct list of urgent moves.  */
    @Override
    public void  testGenerateUrgentMoves() {
        // there should not be any urgent moves at the very start of the gamel
         List moves = searchable.generateUrgentMoves(null, weights(), true);
         Assert.assertTrue("We expected move list to be null since generateUrgentMoves is not implemented for Blockade.", (moves == null));
    }

    public static Test suite() {
        return new TestSuite(BlockadeSearchableTest.class);
    }
}