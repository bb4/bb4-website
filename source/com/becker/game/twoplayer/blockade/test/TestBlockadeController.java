package com.becker.game.twoplayer.blockade.test;

import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.blockade.BlockadeBoardPosition;
import com.becker.game.twoplayer.blockade.BlockadeWall;
import junit.framework.*;
import com.becker.game.twoplayer.blockade.BlockadeBoard;
import com.becker.game.twoplayer.blockade.BlockadeMove;


/**
 * Test methods on the blockade controller
 * Created on June 2, 2007, 7:08 AM
 * @author Barry Becker
 */
public class TestBlockadeController extends BlockadeTestCase {

    /**
     * Creates a new instance of TestBlockadeController
     */
    public TestBlockadeController() {
    }

    /**
     * Verify that the calculated worth for various moves is within reasonable ranges.
     */
    public void testWorthOfWinningMove() {
        restore("whitebox/endGame");
        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();

        BlockadeMove winningMove =
                new BlockadeMove(5,8,  4,8, 0.0, new GamePiece(true),
                                               new BlockadeWall(new BlockadeBoardPosition(12, 5), new BlockadeBoardPosition(12, 4))
                                               );

        controller_.makeMove(winningMove);

        double winFromP1Persp = controller_.worth(winningMove, controller_.getDefaultWeights(), true);
        double winFromP2Persp = controller_.worth(winningMove, controller_.getDefaultWeights(), false);

        Assert.assertEquals("Unexpected value of winning move from P1 perspective", 1000.0, winFromP1Persp);
        Assert.assertEquals("Unexpected value of winning move from P2 perspective", -1000.0, winFromP2Persp);
    }
}
