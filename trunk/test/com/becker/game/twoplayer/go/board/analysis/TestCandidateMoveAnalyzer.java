package com.becker.game.twoplayer.go.board.analysis;

import com.becker.common.Location;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.test.GoTestCase;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;

/**
 *Test that candidate moves can be generated appropriately.
 *
 * @author Barry Becker
 */
public class TestCandidateMoveAnalyzer extends GoTestCase {

    /** we can jsut reuse one of the other file sets */
    private static final String PREFIX = "scoring/";

    public void testCandidateMoves1() {
        verifyCandidateMoves("problem_score1", 71, null);
    }

    public void testCandidateMoves2() {
        verifyCandidateMoves("problem_score2", 79, null);   // or 74?
    }
    public void testCandidateMoves3() {
        List<Location> expCandidates = new ArrayList<Location>(20);
        expCandidates.add(new Location(1, 1));
        expCandidates.add(new Location(1, 4));
        expCandidates.add(new Location(2, 1));
        expCandidates.add(new Location(2, 5));
        expCandidates.add(new Location(4, 1));
        expCandidates.add(new Location(4, 3));
        expCandidates.add(new Location(4, 5));
        expCandidates.add(new Location(5, 1));
        expCandidates.add(new Location(5, 2));
        expCandidates.add(new Location(5, 5));

        verifyCandidateMoves("problem_score55a", 10, expCandidates);
    }

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
        //expCandidates.add(new Location(5, 4));
        //expCandidates.add(new Location(5, 5));

        verifyCandidateMoves("problem_score55b", 8, expCandidates);   // 10?
    }


    /**
     * Veridy candidate move generation.
     */
    private void verifyCandidateMoves(String file, int expNumCandidates, List<Location> expCandidates) {
        restore(PREFIX + file);

        GoBoard board = (GoBoard) controller_.getBoard();
        CandidateMoveAnalyzer cma = new CandidateMoveAnalyzer(board);

        int actNumCandidates = cma.getNumCandidates();
        Assert.assertEquals("Unexpected number of candidate moves for case  "+ file, expNumCandidates, actNumCandidates);

        if (expCandidates != null) {
            for (Location loc : expCandidates) {
                Assert.assertTrue("Invalid candidate position:" + loc, cma.isCandidateMove(loc.getRow(), loc.getCol()));
            }
        }
    }

}
