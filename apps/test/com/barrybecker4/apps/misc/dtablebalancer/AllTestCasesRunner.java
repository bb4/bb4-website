// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer;

import com.barrybecker4.apps.misc.dtablebalancer.balancers.Balancer;
import com.barrybecker4.common.util.FileUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * @author Barry Becker
 */
@RunWith(Parameterized.class)
public class AllTestCasesRunner {

    private static final double TOL = 0.00001;

    private String testName;
    private Table table;
    private Balancer balancer;
    private double expNormScale;
    private double expInitialOverallRatio;
    private double expBalancedRatio;

    @Before
    public void initialize() {
    }

    public AllTestCasesRunner(String testName, Table table, Balancer balancer,
                              double expNormScale, double expInitialOverallRatio, double expBalancedRatio) {
        this.testName = testName;
        this.table = table;
        this.balancer = balancer;
        this.expNormScale = expNormScale;
        this.expInitialOverallRatio = expInitialOverallRatio;
        this.expBalancedRatio = expBalancedRatio;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testCases() {
        return new TestCases();
    }

    /** This test will be run once for each xml test case file in TEST_CASE_DIR */
    @Test
    public void testCaseRunner() throws Exception {
        //System.out.println("Table : " + table.toString());
        assertEquals(testName + "\nWrong normalization scale\n",
                expNormScale, table.getNormalizationScale(), TOL);
        assertEquals(testName + "\nWrong overall coverage for " + table + "\n",
                expInitialOverallRatio, table.getOverallCoverage(), TOL);

        balancer.doBalancing(table);
        assertEquals(testName + "\nWrong balanced coverage for " + table + "\n",
                expBalancedRatio, table.getOverallCoverage(), TOL);
    }

}

