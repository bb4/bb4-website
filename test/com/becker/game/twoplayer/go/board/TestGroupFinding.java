package com.becker.game.twoplayer.go.board;

import com.becker.common.Location;
import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPositionSet;

import java.util.List;

/**
 * Check that we can identifiy groups on the board.
 * @author Barry Becker
 */
public class TestGroupFinding extends GoTestCase {

    /** where to go for the test files. */
    private static final String PREFIX = "board/analysis/eye/information/FalseEye/";


    // ----------- check that we can find group neighbors -------

    public void testFindKoGroupNeighbors() {
        GoBoard b = initializeBoard("false_ko_eye1");

        // white group neighbors
        verifyGroupNeighbors(b, new Location(4, 5), 3);
        verifyGroupNeighbors(b, new Location(5, 6), 6);
        verifyGroupNeighbors(b, new Location(6, 7), 5);
        verifyGroupNeighbors(b, new Location(7, 5), 7);   // 8?

        // black group neighbors
        verifyGroupNeighbors(b, new Location(4, 6), 3);
        verifyGroupNeighbors(b, new Location(8, 8), 6);  
        verifyGroupNeighbors(b, new Location(6, 7), 5);
        verifyGroupNeighbors(b, new Location(9, 7), 5);
        verifyGroupNeighbors(b, new Location(8, 6), 5);
    }

    public void testFindKoGroupNeighbors2() {
        GoBoard b = initializeBoard("false_ko_eye2");

        // white group neighbors
        verifyGroupNeighbors(b, new Location(13, 7), 6);  
        verifyGroupNeighbors(b, new Location(12, 8), 3);   
        
        // black group neighbors
        verifyGroupNeighbors(b, new Location(10, 8), 9);
        verifyGroupNeighbors(b, new Location(12, 7), 1);   
        
    }

    // ----------- check that we can find groups ----------------

    /**
     * Negative test.
     * The position we give to look from does not contain a stone.
     */
    public void testFindNoGroup() {
        GoBoard b = initializeBoard("false_ko_eye1");
        try {
            verifyGroup(b, new Location(2, 3), 6);
            fail();
        } catch (NullPointerException e) {
            // expected, but maybe we should throw illegal argument exception instead.
        }
    }

    public void testFindFalseEyeGroup1() {
        GoBoard b = initializeBoard("false_ko_eye1");
        verifyGroup(b, new Location(5, 5), 10);
        verifyGroup(b, new Location(6, 8), 11);
    }

    public void testFindFalseEyeGroup2() {
        GoBoard b = initializeBoard("false_ko_eye2");
        verifyGroup(b, new Location(8, 8), 18);
        verifyGroup(b, new Location(13, 10), 18);
    }
    

    private GoBoard initializeBoard(String file) {
        restore(PREFIX  + file);
        return (GoBoard)controller_.getBoard();
    }

    private void verifyGroupNeighbors(GoBoard board, Location loc, int expectedNumNeighbors)  {
        NeighborAnalyzer na = new NeighborAnalyzer(board);
        GoBoardPosition position = (GoBoardPosition)board.getPosition(loc);
        GoBoardPositionSet group = na.findGroupNeighbors(position, true);
        assertEquals("Unexpected number of group neighbors for : "+ position +" \n" + group + "\n",
                expectedNumNeighbors, group.size());
    }

    private void verifyGroup(GoBoard board, Location loc, int expectedNumStonesInGroup)  {
        NeighborAnalyzer na = new NeighborAnalyzer(board);
        GoBoardPosition position = (GoBoardPosition)board.getPosition(loc);
        List<GoBoardPosition> group = na.findGroupFromInitialPosition(position);
        assertEquals("Unexpected number of stones in group: \n" + group + "\n",
                expectedNumStonesInGroup, group.size());
    }
}