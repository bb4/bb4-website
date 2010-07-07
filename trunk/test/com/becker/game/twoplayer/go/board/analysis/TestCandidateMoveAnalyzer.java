package com.becker.game.twoplayer.go.board.analysis;

import com.becker.common.Location;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.GoTestCase;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;

/**
 *Test that candidate moves can be generated appropriately.
 *
 * @author Barry Becker
 */
public class TestCandidateMoveAnalyzer extends GoTestCase {

    /** we can just reuse one of the other file sets */
    private static final String PREFIX = "scoring/";


    public void testCandidateMoves1() {
        verifyCandidateMoves("problem_score1", 114, null);
    }

    /** XXXX sometimes passes sometimes fails. odd. */
    public void testCandidateMoves2() {
        verifyCandidateMoves("problem_score2", 103, null);   // or 79?
    }
    
    public void testCandidateMoves3() {
        List<Location> expCandidates = new ArrayList<Location>(10);
        expCandidates.add(new Location(2, 5));
        expCandidates.add(new Location(1, 4));
        expCandidates.add(new Location(4, 3));
        expCandidates.add(new Location(5, 2));

        verifyCandidateMoves("problem_score55a", 4, expCandidates);
    }

    /** XXXX sometimes passes sometimes fails. odd  */
    public void testCandidateMoves4() {

        System.out.println("------------cm4 --------------------------");
        List<Location> expCandidates = new ArrayList<Location>(20);
        expCandidates.add(new Location(1, 1));
        expCandidates.add(new Location(1, 5));
        expCandidates.add(new Location(2, 1));
        expCandidates.add(new Location(2, 3));
        expCandidates.add(new Location(2, 4));
        expCandidates.add(new Location(3, 2));
        expCandidates.add(new Location(4, 1));
        expCandidates.add(new Location(5, 1));
        expCandidates.add(new Location(5, 4));
        expCandidates.add(new Location(5, 5));

        verifyCandidateMoves("problem_score55b", 10, expCandidates);
    }  


    /**
     * Verify candidate move generation.
     */
    private void verifyCandidateMoves(String file, int expNumCandidates, List<Location> expCandidates) {
        restore(PREFIX + file);

        GoBoard board = (GoBoard) controller_.getBoard();
        CandidateMoveAnalyzer cma = new CandidateMoveAnalyzer(board);

        int actNumCandidates = cma.getNumCandidates();
        Assert.assertEquals("Unexpected number of candidate moves for case  "+ file, expNumCandidates, actNumCandidates);

        if (expCandidates != null) { 
            System.out.println("actual candidates="+ actNumCandidates);
            for (Location loc : expCandidates) {
                Assert.assertTrue("Invalid candidate position:" + loc, cma.isCandidateMove(loc.getRow(), loc.getCol()));
            }
        }
    }

}
