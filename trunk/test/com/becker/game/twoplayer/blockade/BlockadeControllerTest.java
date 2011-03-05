package com.becker.game.twoplayer.blockade;

import com.becker.common.Location;
import com.becker.game.common.board.GamePiece;
import com.becker.optimization.parameter.ParameterArray;
import junit.framework.*;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategy;


/**
 * @author Barry Becker
 */
public class BlockadeControllerTest extends BlockadeTestCase {

    /**
     * Creates a new instance of BlockadeControllerTest
     */
    public BlockadeControllerTest() {}

    /**
     * Verify that the calculated worth for various moves is within reasonable ranges.
     */
    public void testWorthOfWinningMove() {
        restore("whitebox/endGame");
        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();

        BlockadeMove winningMove =
                new BlockadeMove(new Location(5, 8), new Location(4, 8), 0, new GamePiece(true),
                                 new BlockadeWall(new BlockadeBoardPosition(12, 5), new BlockadeBoardPosition(12, 4))
                );

        controller_.makeMove(winningMove);

        ParameterArray weights = controller_.getComputerWeights().getDefaultWeights();
        int winFromP1Persp = controller_.getSearchable().worth(winningMove, weights);

        Assert.assertEquals("Unexpected value of winning move from P1 perspective",
                SearchStrategy.WINNING_VALUE, winFromP1Persp);
    }
}