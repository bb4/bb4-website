package com.becker.game.twoplayer.go.test;

import junit.framework.Assert;
import junit.framework.TestSuite;
import junit.framework.Test;

import java.util.List;

import com.becker.game.twoplayer.go.GoMove;


public class TestKiseido2002 extends GoTestCase {



/*  the rest of these work.    */

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

        String[] files = this.getFilesMatching("games2002/", problemPattern);

        for (int i=0; i<files.length; i++) {

            String filename =  files[i].substring(0, files[i].length() - 4);
            System.out.print("about to restore :"+filename);
            restore("games2002/" + filename);
            System.out.println("     done restoring :"+filename);
        }

        // must check the worth of the board once to update the scoreContributions fo empty spaces.
        List moves = controller_.getMoveList();
        double w = controller_.worth((GoMove)moves.get(moves.size()-3), controller_.getDefaultWeights(), true);
        controller_.updateLifeAndDeath();   // this updates the groups and territory as well.

        Assert.assertTrue(true);
    }



    public static Test suite() {
        return new TestSuite(TestKiseido2002.class);
    }
}