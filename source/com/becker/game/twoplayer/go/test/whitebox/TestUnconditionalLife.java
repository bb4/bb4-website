package com.becker.game.twoplayer.go.test.whitebox;

import com.becker.common.util.Util;
import com.becker.game.twoplayer.go.*;
import com.becker.game.twoplayer.go.test.GoTestCase;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;

/**
 * Mostly test that the scoring of groups works correctly.
 * @author Barry Becker
 */
public class TestUnconditionalLife extends GoTestCase {    
    

    // test for unconditional life
    public void testUnconditionalLife1() {
        controller_.reset();
        verifyUnconditionalLife("whitebox/unconditionalLife1", true, 12, true);
    }

    public void testUnconditionalLife2() {
        verifyUnconditionalLife("whitebox/unconditionalLife2", true, 13, false);
    }

    public void testUnconditionalLife3() {
        verifyUnconditionalLife("whitebox/unconditionalLife3", true, 11, false);
    }


    public void testUnconditionalLife4() {
        verifyUnconditionalLife("whitebox/unconditionalLife4", true, 8, false);
    }

    public void testUnconditionalLife5() {
        verifyUnconditionalLife("whitebox/unconditionalLife5", true, 7, false);
    }

    public void testUnconditionalLife6() {
        verifyUnconditionalLife("whitebox/unconditionalLife6", true, 7, true);
    }
    /*

    public void testUnconditionalLife7() {
        verifyUnconditionalLife("whitebox/unconditionalLife7", false, 8, true);
    }


    public void testUnconditionalLife8() {
        verifyUnconditionalLife("whitebox/unconditionalLife8", true, 13, true);
    }

    public void testUnconditionalLife9() {
        verifyUnconditionalLife("whitebox/unconditionalLife9", false, 9, false);
    }


    public void testUnconditionalLife11() {

        verifyUnconditionalLife("whitebox/unconditionalLife11", true, 14, true);
    }

    public void testUnconditionalLife12() {
        verifyUnconditionalLife("whitebox/unconditionalLife12", true, 21, true);
    }

     public void testUnconditionalLife13() {
        verifyUnconditionalLife("whitebox/unconditionalLife13", true, 15, false);
    }

     public void testUnconditionalLife14() {
        verifyUnconditionalLife("whitebox/unconditionalLife14", true, 13, false);
    }
   
     public void testUnconditionalLife15() {
        verifyUnconditionalLife("whitebox/unconditionalLife15", true, 12, true);
    }
 
*/

    /**
     * Use Benson's algorithm for detecting unconditionally alive groups.
     */
    private void verifyUnconditionalLife(String file,
                                         boolean forBlackGroup, int expectedSizeOfGroup, boolean expectedUnconditionalyAlive) {
        restore(file);

        // find the biggest black and white groups
        GoGroup group = getBiggestGroup(forBlackGroup);

        int size = group.getNumStones();
        Assert.assertTrue("We expected the size of the test group to be "+ expectedSizeOfGroup
                +" but instead it was "+ size,
                size == expectedSizeOfGroup);
        System.out.println("now testing unconditional life.");
        boolean unconditionallyAlive = GoGroupUtil.isUnconditionallyAlive(group, (GoBoard) controller_.getBoard());

        if (expectedUnconditionalyAlive) {
            Assert.assertTrue("Expected this group be unconditionally alive, but its not. group=" + group,
                               unconditionallyAlive);
        } else {
            Assert.assertTrue("Did not expected this group be unconditionally alive, but it is. group=" + group,
                               !unconditionallyAlive);
        }
    }

}
