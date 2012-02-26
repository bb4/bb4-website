// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.pente.pattern;

import com.becker.game.twoplayer.pente.PenteSearchableTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Master test suite to test all aspects of my pente program.
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {


    public static Test suite() {

        TestSuite suite =  new TestSuite("All Pente pattern Tests");

        suite.addTestSuite(PatternIntConverterTest.class);
        suite.addTestSuite(PatternsTest.class);
        suite.addTestSuite(PatternTableTest.class);

        //suite.addTest(com.becker.game.twoplayer.pente.analysis.TestAll.suite());

        return suite;
    }
}