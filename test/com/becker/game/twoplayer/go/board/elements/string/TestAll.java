/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.elements.string;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAll {

    private TestAll() {}

    /**
     * @return all the junit test cases to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Go string Tests");

        suite.addTestSuite(TestGoString.class);

        return suite;
    }

}

