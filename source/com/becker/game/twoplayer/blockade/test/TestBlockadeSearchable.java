package com.becker.game.twoplayer.blockade.test;

import com.becker.game.common.GameController;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.blockade.BlockadeController;
import com.becker.game.twoplayer.blockade.BlockadeMove;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.test.TwoPlayerSearchableBaseTst;
import java.util.List;
import junit.framework.*;


/**
 * Verify that all the methods in blockadeSearchable work as expected
 * @author Barry Becker
 */
public class TestBlockadeSearchable extends TwoPlayerSearchableBaseTst {


    /**
     * Create the controller containing the searchable to test.
     */
    protected  GameController createController() {
        return new BlockadeController();
    }

    /**
     * Create the game options
     */
    protected TwoPlayerOptions createTwoPlayerGameOptions() {
        return new TwoPlayerOptions();
    }

    /**
     * @return an initial move by player one.
     */
    protected  TwoPlayerMove createInitialMove() {
        return  BlockadeMove.createMove(5, 5,   0, new GamePiece(true));
    }


    @Override
    protected int getDebugLevel() {
        return 0;
    }

    @Override
    protected int getExpectedNumGeneratedMovesBeforeFirstMove() {
       return 288;
   }

    /**  Verify that we generate a correct list of urgent moves.  */
    @Override
    public void  testGenerateUrgentMoves() {
        // there should not be any urgen moves at the very start of the gamel
         List moves = searchable.generateUrgentMoves(null, getTwoPlayerController().getComputerWeights().getPlayer1Weights(), true);
         Assert.assertTrue("We expected move list to be null since generateUrgentMoves is not implemented for Blockade.", (moves == null));
    }

    public static Test suite() {
        return new TestSuite(TestBlockadeSearchable.class);
    }
}
