package com.becker.game.twoplayer.go.test.board.analysis;

import com.becker.game.twoplayer.go.board.GoGroup;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.LifeAnalyzer;
import com.becker.common.util.Util;
import com.becker.game.twoplayer.go.*;
import com.becker.game.twoplayer.go.test.GoTestCase;
import junit.framework.Assert;

/**
 *Test that candidate moves can be generated appropriately.
 * 
 * @author Barry Becker
 */
public class TestCandidateMoveAnalyzer extends GoTestCase {    
    
    private static final String PREFIX = "board/candidates/";

    public void testCandidateMoves1() {
        controller_.reset();
        verifyCandidateMoves("findCandidates1", true, 12, true);
    }


    /**
     * Veridy candidate move generation.
     */
    private void verifyCandidateMoves(String file,
                                         boolean forBlackGroup, int expectedSizeOfGroup, boolean expectedUnconditionalyAlive) {
        restore(PREFIX + file);
/** to do 
 * 
 *
        // find the biggest black and white groups
        GoGroup group = getBiggestGroup(forBlackGroup);

        int size = group.getNumStones();
        Assert.assertTrue("We expected the size of the test group to be "+ expectedSizeOfGroup
                +" but instead it was "+ size,
                size == expectedSizeOfGroup);
        System.out.println("now testing unconditional life.");
        LifeAnalyzer analyzer = new LifeAnalyzer(group, (GoBoard) controller_.getBoard());
        boolean unconditionallyAlive = analyzer.isUnconditionallyAlive();

        if (expectedUnconditionalyAlive) {
            Assert.assertTrue("Expected this group be unconditionally alive, but its not. group=" + group,
                               unconditionallyAlive);
        } else {
            Assert.assertTrue("Did not expected this group be unconditionally alive, but it is. group=" + group,
                               !unconditionallyAlive);
        }
 * */
    }

}
