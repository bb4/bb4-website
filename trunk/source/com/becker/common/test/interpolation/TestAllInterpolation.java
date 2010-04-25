package com.becker.common.test.interpolation;

import com.becker.common.test.CommandLineOptionsTest;
import com.becker.common.test.LRUCacheTest;
import com.becker.common.test.NiceNumbersTest;
import com.becker.common.test.util.MathUtilTest;
import com.becker.common.test.util.TestAllUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAllInterpolation  {

    private TestAllInterpolation() {}

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Interpolation Tests");

        suite.addTestSuite(LinearInterpolatorTest.class);
        suite.addTestSuite(CubicInterpolatorTest.class);
        suite.addTestSuite(CosineInterpolatorTest.class);
        suite.addTestSuite(HermiteInterpolatorTest.class);
        suite.addTestSuite(StepInterpolatorTest.class);

        return suite;
    }
}