// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.dtablebalancer;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.assertTrue;

/**
 * @author Barry Becker
 */
public class AllTestCasesTest {

    @Test
    public void runAllTestCases() {
        Result result = JUnitCore.runClasses(AllTestCasesRunner.class);
        if (!result.wasSuccessful()) {
            System.out.println(" *** FAILURES (" + result.getFailureCount() + ") ***\n");
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.getException().getMessage());
                //System.out.println(failure.toString());
            }
        }
        assertTrue("There were failures.", result.wasSuccessful());
    }
}
