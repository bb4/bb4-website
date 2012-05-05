// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model.fitting;

import com.becker.puzzle.tantrix.model.BorderFinderTest;
import com.becker.puzzle.tantrix.model.MoveGeneratorTest;
import com.becker.puzzle.tantrix.model.TantrixBoardTest;
import com.becker.puzzle.tantrix.model.TantrixTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Tantrix model Tests");

        suite.addTestSuite(PrimaryPathFitterTest.class);

        return suite;
    }
}