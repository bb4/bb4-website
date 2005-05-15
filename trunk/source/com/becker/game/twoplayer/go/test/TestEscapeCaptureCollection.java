package com.becker.game.twoplayer.go.test;

import com.becker.game.twoplayer.go.GoMove;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test a collection of problems from
 * Martin Mueller (mmueller)
 * Markus Enzenberger (emarkus)
 *email domain: cs.ualberta.ca
 *
 * @author Barry Becker
 */
public class TestEscapeCaptureCollection extends GoTestCase {


    public void test3() {
        GoMove m = getNextMove("problems/sgf/escape_capture/escape_capture.3", true);
        checkExpected(m, 6, 11);
    }

    public void test5() {
        GoMove m = getNextMove("problems/sgf/escape_capture/escape_capture.5", true);
        checkExpected(m, 0, 6);
    }

    public void test13() {
        GoMove m = getNextMove("problems/sgf/escape_capture/escape_capture.13", false);
        checkExpected(m, 13, 5);
    }



    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {
        return new TestSuite(TestEscapeCaptureCollection.class);
    }


}
