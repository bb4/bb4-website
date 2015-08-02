// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer;

import com.barrybecker4.common.util.FileUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Barry Becker
 */
@RunWith(Parameterized.class)
public class AllTestCasesRunner {

    private static final double TOL = 0.00001;
    private Table table;
    private double expInitialOverallRatio;

    @Before
    public void initialize() {
    }

    public AllTestCasesRunner(Table table, double expInitialOverallRatio) {
        this.table = table;
        this.expInitialOverallRatio = expInitialOverallRatio;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testCases() {
        return new TestCases();
    }

    /** This test will be run once for each xml test case file in TEST_CASE_DIR */
    @Test
    public void testCaseRunner() throws Exception {

        //System.out.println("Table : " + table.toString());

        assertEquals("Wrong overall coverate for " + table + "\n ",
                expInitialOverallRatio, table.getOverallCoverage(), TOL);


    }

}

