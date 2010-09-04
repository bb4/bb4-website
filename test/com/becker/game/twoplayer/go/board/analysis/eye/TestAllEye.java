package com.becker.game.twoplayer.go.board.analysis.eye;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.analysis.eye.information.TestAllInformation;
import com.becker.game.twoplayer.go.board.analysis.eye.information.TestFalseEyeInformation;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAllEye extends GoTestCase {

    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Eye Tests");

        suite.addTest(TestAllInformation.suite());
        suite.addTestSuite(TestEyeNeighborMap.class);
        suite.addTestSuite(TestFalseEyeInformation.class);

        return suite;
    }

}