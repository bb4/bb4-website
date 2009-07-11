package com.becker.game.twoplayer.blockade.test;

import com.becker.game.common.GamePiece;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.blockade.BlockadeBoardPosition;
import com.becker.game.twoplayer.blockade.BlockadeWall;
import junit.framework.*;
import com.becker.game.twoplayer.blockade.BlockadeBoard;
import com.becker.game.twoplayer.blockade.BlockadeMove;
import java.util.List;

/**
 * Test methods on the blockade controller
 * Created on June 2, 2007, 7:08 AM
 * @author becker
 */
public class TestBlockadeController extends BlockadeTestCase {

    /**
     * Creates a new instance of TestBlockadeController
     */
    public TestBlockadeController() {
    }


    public void  testGetWallsForMove() {
        restore("whitebox/moveList1");

        //BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
        // List<BlockadeWall> walls = controller_.getWallsForMove(move, paths);
        // verify that the list of walls is what we expect.
        //GameContext.log(2, "Walls=" + walls);
    }

    public void testGenerateMoves() {

        restore("whitebox/noMoves2");

        BlockadeMove lastMove = (BlockadeMove) controller_.getMoveList().getLast();
        GameContext.log(2, "lastMove=" + lastMove);
        List moves = controller_.getSearchable().generateMoves(lastMove, controller_.getDefaultWeights(), false);
        int expectedNumMoves = 64;
        Assert.assertTrue("Expected there to be "+expectedNumMoves+" moves but got " +moves.size() +" moves="+ moves, moves.size() == expectedNumMoves);
    }

    public void testGenerateMoves2() {

        restore("whitebox/noMoves2");
        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();

        GamePiece piece1 = new GamePiece(true); // player 1
        GamePiece piece2 = new GamePiece(false);  // player 2
        BlockadeWall wall1 = new BlockadeWall((BlockadeBoardPosition) board.getPosition(8, 10), (BlockadeBoardPosition) board.getPosition(9, 10));
        BlockadeWall wall2 = new BlockadeWall((BlockadeBoardPosition) board.getPosition(12, 6), (BlockadeBoardPosition) board.getPosition(12, 7));

        BlockadeMove move1 = BlockadeMove.createMove(8, 11, 6, 11, 0.1, piece2, wall2);
        BlockadeMove move2 = BlockadeMove.createMove(12,6, 10, 6, 0.1, piece1, wall1);

        controller_.makeMove(move1);
        controller_.makeMove(move2);

        BlockadeMove lastMove = (BlockadeMove) controller_.getMoveList().getLast();
        GameContext.log(2, "lastMove="+lastMove);
        List moves = controller_.getSearchable().generateMoves(lastMove, controller_.getDefaultWeights(), false);

        int expectedNumMoves = 66;
        Assert.assertTrue("Expected there to be "+expectedNumMoves+" moves but got " +moves.size() +" moves="+ moves, moves.size() == expectedNumMoves);
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

        Assert.assertEquals("Unexpected value of winning move from P1 perspective", 2000.0, winFromP1Persp);
        Assert.assertEquals("Unexpected value of winning move from P2 perspective", -2000.0, winFromP2Persp);

    }

}
