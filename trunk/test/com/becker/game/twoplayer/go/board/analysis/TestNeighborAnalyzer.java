package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.GoTestCase;
import junit.framework.*;

import java.util.*;

/**
 * Verify that all our neighbor analysis methods work.
 * @author Barry Becker
 */
public class TestNeighborAnalyzer extends GoTestCase {

    private static final String PREFIX = "board/neighbor/";

    // test group neighbor detection
    public void testGetGroupNbrs1() {
        verifyGroupNbrs("groupNbr1", 3, 3, 1, 1);
    }

    public void testGetGroupNbrs2() {
        verifyGroupNbrs("groupNbr2", 4, 4, 6, 8);
    }

    public void testGetGroupNbrs3() {
        verifyGroupNbrs("groupNbr3", 5, 4, 1, 1);
     }

    public void testGetGroupNbrs4() {
        verifyGroupNbrs("groupNbr4", 4, 4, 5, 5);
    }

    /**
     * Note that only nobi and diagonal enemy nbrs are considered group neighbors
     * while all 20 possible group nbrs are considered for friendly stones.
     */
    public void testGetGroupNbrs5() {
        verifyGroupNbrs("groupNbr5", 4, 4, 0, 1);
    }

    public void testGetGroupNbrs6() {
        verifyGroupNbrs("groupNbr6", 4, 4, 0, 3);
    }

    public void testGetGroupNbrs7() {
        verifyGroupNbrs("groupNbr7", 4, 4, 0, 4);
    }

     public void testGetGroupNbrs8() {
        verifyGroupNbrs("groupNbr8", 4, 4, 4, 6);
    }

    public void testGetGroupNbrs9() {
        verifyGroupNbrs("groupNbr9", 4, 4, 2, 6);
    }

    public void testGetGroupNbrs10() {
        verifyGroupNbrs("groupNbr10", 4, 4, 0, 1);
    }

    public void testGetGroupNbrs11() {
        verifyGroupNbrs("groupNbr11", 4, 4, 2, 3);
    }

    public void testGetGroupNbrs12() {
        verifyGroupNbrs("groupNbr12", 4, 4, 5, 5);
    }

    private void verifyGroupNbrs(String file, int row, int col, int expectedNumSameNbrs, int expectedNumNbrs) {
        restore(PREFIX +file);

        GoBoard board = (GoBoard)controller_.getBoard();
        GoBoardPosition pos = (GoBoardPosition)board.getPosition(row, col);
        int numSameNbrs = board.getGroupNeighbors(pos, true).size();
        int numNbrs = board.getGroupNeighbors(pos, false).size();

        Assert.assertTrue("numSameNbrs="+numSameNbrs+" expected "+ expectedNumSameNbrs, numSameNbrs == expectedNumSameNbrs);
        Assert.assertTrue("numNbrs="+numNbrs+" expected "+ expectedNumNbrs, numNbrs == expectedNumNbrs);
    }

    public void testFindOccupiedNbrs() {
        
        String file = "occupiedNbrs1";
        restore(PREFIX + file);
        GoBoard board = (GoBoard)controller_.getBoard();

        List empties = new ArrayList(4);
        empties.add(board.getPosition(3, 3));
        empties.add(board.getPosition(3, 4));
        empties.add(board.getPosition(4, 3));
        empties.add(board.getPosition(4, 4));
       
        verifyOccupiedNbrs(board, empties, 6); // or 9?
    }

    private void verifyOccupiedNbrs(GoBoard board, List empties, int expectedNumNbrs) {
      
        NeighborAnalyzer na = new NeighborAnalyzer(board);
        int numNbrs = na.findOccupiedNeighbors(empties).size();

        Assert.assertTrue("numNbrs="+numNbrs+" expected "+ expectedNumNbrs, numNbrs == expectedNumNbrs);
    }

}
