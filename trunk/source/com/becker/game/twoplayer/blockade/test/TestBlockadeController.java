package com.becker.game.twoplayer.blockade.test;

import com.becker.game.twoplayer.blockade.BlockadeBoard;
import com.becker.game.twoplayer.blockade.BlockadeController;
import com.becker.game.twoplayer.blockade.Path;

/**
 *
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
        restore("whitebox/wallsForMove2");
        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
          
       // List<BlocakdWall> walls = controller_.getWallsForMove(move, paths);
          
        // verify that the list of walls is what we expect.
        //System.out.println("Walls="+walls);
    }
    
}
