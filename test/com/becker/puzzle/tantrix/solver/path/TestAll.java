// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver.path;

import com.becker.puzzle.tantrix.solver.path.permuting.TilesOfTypeIndices;
import com.becker.puzzle.tantrix.solver.path.permuting.TilesOfTypeIndicesTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Tantrix path Tests");

        suite.addTestSuite(TantrixPathTest.class);
        suite.addTestSuite(PathEvaluatorTest.class);
        suite.addTestSuite(PathifierTest.class);
        suite.addTestSuite(TilesOfTypeIndicesTest.class);
        suite.addTest(com.becker.puzzle.tantrix.solver.path.permuting.TestAll.suite());

        return suite;
    }
}