/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis.eye;

import com.becker.game.twoplayer.go.board.analysis.eye.information.TestFalseEyeInformation;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAll {

    private TestAll() {}
    
    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Eye Tests");

        suite.addTest(com.becker.game.twoplayer.go.board.analysis.eye.information.TestAll.suite());
        suite.addTestSuite(TestEyeNeighborMap.class);
        suite.addTestSuite(TestFalseEyeInformation.class);

        return suite;
    }

}