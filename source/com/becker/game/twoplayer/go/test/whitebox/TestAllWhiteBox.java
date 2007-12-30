package com.becker.game.twoplayer.go.test.whitebox;

import com.becker.game.twoplayer.go.test.GoTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test out all of my testable methods in my go related classes.
 *
 * @author Barry Becker
 */
public class TestAllWhiteBox extends GoTestCase {


    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Whitebox Tests");

        ////suite.addTestSuite(TestGoBoard.class);
        suite.addTestSuite(TestGoGroup.class);

        return suite;
    }


}
