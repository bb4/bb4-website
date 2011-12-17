/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis.neighbor;

import com.becker.game.twoplayer.go.GoTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAll extends GoTestCase {

    private TestAll() {}
    
    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Neighbor Tests");

        suite.addTestSuite(TestGameStateBoostCalculator.class);
        suite.addTestSuite(TestNeighborAnalyzer.class);
        suite.addTestSuite(TestNobiNeighborAnalyzer.class);
        suite.addTestSuite(TestStringNeighborAnalyzer.class);
        suite.addTestSuite(TestGroupNeighborAnalyzer.class);

        return suite;
    }

}