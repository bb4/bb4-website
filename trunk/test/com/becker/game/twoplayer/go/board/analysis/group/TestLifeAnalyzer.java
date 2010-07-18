package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.twoplayer.go.board.elements.GoGroup;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.GoTestCase;
import junit.framework.Assert;

/**
 * Mostly test that the scoring of groups works correctly.
 * @author Barry Becker
 */
public class TestLifeAnalyzer extends GoTestCase {

    private static final String PREFIX = "board/analysis/group/life/";

    // test for unconditional life
    public void testUnconditionalLife1() {
        verifyUnconditionalLife("unconditionalLife1", true, 12, true);
    }

    public void testUnconditionalLife2() {
        verifyUnconditionalLife("unconditionalLife2", true, 13, false);
    }

    public void testUnconditionalLife3() {
        verifyUnconditionalLife("unconditionalLife3", true, 11, false);
    }


    public void testUnconditionalLife4() {
        verifyUnconditionalLife("unconditionalLife4", true, 8, false);
    }

    public void testUnconditionalLife5() {
        verifyUnconditionalLife("unconditionalLife5", true, 7, false);
    }

    public void testUnconditionalLife6() {
        verifyUnconditionalLife("unconditionalLife6", true, 7, true);
    }

    public void testUnconditionalLife7() {
        verifyUnconditionalLife("unconditionalLife7", false, 8, true);
    }


    public void testUnconditionalLife8() {
        verifyUnconditionalLife("unconditionalLife8", true, 13, true);
    }

    public void testUnconditionalLife9() {
        verifyUnconditionalLife("unconditionalLife9", false, 9, false);
    }

    public void testUnconditionalLife11() {

        verifyUnconditionalLife("unconditionalLife11", true, 14, true);
    }

    public void testUnconditionalLife12() {
        verifyUnconditionalLife("unconditionalLife12", true, 21, true);
    }

     public void testUnconditionalLife13() {
        verifyUnconditionalLife("unconditionalLife13", true, 15, false);
    }

     public void testUnconditionalLife14() {
        verifyUnconditionalLife("unconditionalLife14", true, 13, false);
    }

     public void testUnconditionalLife15() {
        verifyUnconditionalLife("unconditionalLife15", true, 12, true);
    }

    /**
     * Use Benson's algorithm for detecting unconditionally alive groups.
     */
    private void verifyUnconditionalLife(String file,
                                         boolean forBlackGroup, int expectedSizeOfGroup,
                                         boolean expectedUnconditionalyAlive) {
        restore(PREFIX + file);

        // find the biggest black and white groups
        GoGroup group = getBiggestGroup(forBlackGroup);

        int size = group.getNumStones();
        Assert.assertTrue("We expected the size of the test group to be "+ expectedSizeOfGroup
                +" but instead it was "+ size,
                size == expectedSizeOfGroup);

        LifeAnalyzer analyzer = new LifeAnalyzer(group, (GoBoard) controller_.getBoard());
        boolean unconditionallyAlive = analyzer.isUnconditionallyAlive();

        if (expectedUnconditionalyAlive) {
            Assert.assertTrue("Expected this group be unconditionally alive, but its not. group=" + group,
                               unconditionallyAlive);
        } else {
            Assert.assertTrue("Did not expected this group be unconditionally alive, but it is. group=" + group,
                               !unconditionallyAlive);
        }
    }

}
