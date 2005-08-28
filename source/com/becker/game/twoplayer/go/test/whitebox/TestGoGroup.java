package com.becker.game.twoplayer.go.test.whitebox;

import com.becker.game.twoplayer.go.*;
import com.becker.game.twoplayer.go.test.GoTestCase;
import junit.framework.Assert;

/**
 * Mostly test that the scoring of groups works correctly.
 * @author Barry Becker
 */
public class TestGoGroup extends GoTestCase {


    // test
    public void testAbsHealth1() {
        controller_.reset();
        verifyHealthDifferences("whitebox/groupHealth1", .0, .2, .2);
    }


    public void testAbsHealth2() {
        verifyHealthDifferences("whitebox/groupHealth2", .1, .8, .7);
    }

    public void testAbsHealth3() {
        verifyHealthDifferences("whitebox/groupHealth3", .8, 1.1, 1.1);
    }

    public void testAbsHealth4() {
        verifyHealthDifferences("whitebox/groupHealth4", .2, 1.1, 1.0);
    }

    public void testAbsHealth5() {
        verifyHealthDifferences("whitebox/groupHealth5", -.1, .2, .2);
    }

    public void testAbsHealth6() {
        verifyHealthDifferences("whitebox/groupHealth6", .0, -.6, -.6);
    }



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





    private static final double THRESH = .11;

    /**
     * verify the black - white goup health differences for
     * the abs(absolute health), absolute health, and relative health.
     */
    private void verifyHealthDifferences(String file,
                                         double expectedAbsAbsHealthDifference,
                                         double expectedAbsHealthDifference,
                                         double expectedRelHealthDifference) {
        restore(file);

        // find the biggest black and white groups
        GoGroup bg = getBiggestGroup(true);
        GoGroup wg = getBiggestGroup(false);

        double bah = bg.getAbsoluteHealth();
        double wah = wg.getAbsoluteHealth();

        double abah = Math.abs(bah);
        double awah = Math.abs(wah);

        double brh = bg.getRelativeHealth();
        double wrh = wg.getRelativeHealth();

        double daah = abah - awah;
        double dah = bah - wah;
        double drh = brh - wrh;

        Assert.assertTrue("Expected abs(black AbsHealth) - abs(white AbsHealth) to be about "
                + expectedAbsAbsHealthDifference + "\n but instead got ("+abah+" -"+ awah+") = "+ daah,
                approximatelyEqual(daah, expectedAbsAbsHealthDifference, THRESH));
        Assert.assertTrue("Expected (black AbsHealth) - (white AbsHealth) to be about "
                + expectedAbsHealthDifference + "\n but instead got ("+bah+" -"+ wah+") = "+ dah,
                approximatelyEqual(dah, expectedAbsHealthDifference, THRESH));
        Assert.assertTrue("Expected (black RelativeHealth) - (white RelativeHealth) to be about "
                + expectedRelHealthDifference + "\n but instead got  ("+brh+" -"+ wrh+") = "+ drh,
                approximatelyEqual(drh, expectedRelHealthDifference, THRESH));

    }


    private void verifyUnconditionalLife(String file,
                                         boolean forBlackGroup, int expectedSizeOfGroup, boolean expectedUnconditionalyAlive) {
        restore(file);

        // find the biggest black and white groups
        GoGroup group = getBiggestGroup(forBlackGroup);

        //System.out.println(controller_.getBoard());

        int size = group.getNumStones();
        Assert.assertTrue("We expected the size of the test group to be "+ expectedSizeOfGroup
                +" but instead it was "+ size,
                size == expectedSizeOfGroup);

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
