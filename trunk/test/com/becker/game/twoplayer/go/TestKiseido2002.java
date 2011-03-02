package com.becker.game.twoplayer.go;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.board.move.GoMove;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.List;

/**
 * @author Barry Becker
 */
public class TestKiseido2002 extends GoTestCase {


    public void testJanuary() {
        check("2002-01");
    }

    public void testFebruary() {
        check("2002-02");
    }

    public void testMarch() {
        check("2002-03");
    }

    public void testApril() {
        check("2002-04");
    }

    public void testMay() {
        check("2002-05");
    }

    public void testJune() {
        check("2002-06");
    }

    public void testJuly() {
        check("2002-07");
    }

    public void testAugust() {
        check("2002-08");
    }

    public void testSeptember() {
        check("2002-09");
    }

    public void testOctober() {
        check("2002-10");
    }

    public void testNovember() {
        check("2002-11");
    }

    public void testDecember() {
        check("2002-12");
    }


    /**
     * Verify that we can load all the files with the specified pattern
     * @param problemPattern
     */
    private void check(String problemPattern) {

        GameContext.log(0, "Now checking "+ problemPattern);
        String[] files = getFilesMatching("games2002/", problemPattern);

        for (String file : files) {

            String filename = file.substring(0, file.length() - 4);
            GameContext.log(0, " about to restore :" + filename);
            try {
                restore("games2002/" + filename);
            } catch (AssertionError e) {
                System.out.println("error on " + filename);
                e.printStackTrace();
            }
        }

        // must check the worth of the board once to update the scoreContributions fo empty spaces.
        List moves = controller_.getMoveList();
        //double w = controller_.worth((GoMove)moves.get(moves.size()-3), controller_.getDefaultWeights(), true);
        controller_.getSearchable().done(GoMove.createResignationMove(true), true);
        //controller_.updateLifeAndDeath();   // this updates the groups and territory as well.

        assertTrue(true);
    }


    public static Test suite() {
        return new TestSuite(TestKiseido2002.class);
    }
}