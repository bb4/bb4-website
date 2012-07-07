/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.elements.group;

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

        TestSuite suite =  new TestSuite("Go group Tests");

        suite.addTestSuite(TestGoGroup.class);

        return suite;
    }

}

