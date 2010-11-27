package com.becker.game.twoplayer.go.board.elements;

import com.becker.game.twoplayer.go.board.analysis.*;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test out all of my testable methods in the board analysis classes.
 *
 * @author Barry Becker
 */
public class TestAll {

    private TestAll() {}

    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Go elements Tests");

        suite.addTestSuite(TestGoString.class);

        return suite;
    }

}

