package com.becker.game.twoplayer.blockade.test;

import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.blockade.BlockadeBoardPosition;
import com.becker.game.twoplayer.blockade.BlockadeWall;
import junit.framework.*;
import com.becker.game.twoplayer.blockade.BlockadeBoard;
import com.becker.game.twoplayer.blockade.BlockadeController;
import com.becker.game.twoplayer.blockade.BlockadeMove;
import com.becker.game.twoplayer.blockade.Path;
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
        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
          
       // List<BlockadeWall> walls = controller_.getWallsForMove(move, paths);
          
        // verify that the list of walls is what we expect.
        //System.out.println("Walls="+walls);
    }
    
    public void testGenerateMoves() {
        
        restore("whitebox/noMoves2");
        
        BlockadeMove lastMove = (BlockadeMove) controller_.getMoveList().getLast(); 
        //System.out.println("lastMove=" + lastMove);
        List moves = controller_.getSearchable().generateMoves(lastMove, controller_.getDefaultWeights(), false);
        int expectedNumMoves = 188;
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
        //System.out.println("lastMove="+lastMove);
        List moves = controller_.getSearchable().generateMoves(lastMove, controller_.getDefaultWeights(), false);
        
        int expectedNumMoves = 328;
        Assert.assertTrue("Expected there to be "+expectedNumMoves+" moves but got " +moves.size() +" moves="+ moves, moves.size() == expectedNumMoves);
    }
    
}