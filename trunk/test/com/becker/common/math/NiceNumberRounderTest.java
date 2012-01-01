// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.common.math;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Arrays;

/**
 * @author Barry Becker
 */
public class NiceNumberRounderTest extends TestCase {


    private static double BASE_VALUE = 101.34;
    
    private static double[] EXPECTED_ROUNDED_VALUES = {
        100.0, 100.0, 200.0, 200.0, 200.0, 200.0, 200.0, 
        500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 
        1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0
    };
    private static double[] EXPECTED_CEILED_VALUES = {
        200.0, 200.0, 200.0, 200.0,
        500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 500.0,
        1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0,
        2000.0, 2000.0, 2000.0, 2000.0,
    };

    public void testRoundNumberSmall() {
        assertEquals("Unexpected ", 0.001, NiceNumberRounder.round(0.001234567, true));
    }

    public void testRoundNumberMediumUpper() {
        assertEquals("Unexpected ", 10.0, NiceNumberRounder.round(8.87653, true));
    }

    public void testRoundNumberMediumLower() {
        assertEquals("Unexpected ", 5.0, NiceNumberRounder.round(4.363, true));
    }

    public void testRoundNumberLargeUpper() {
        assertEquals("Unexpected ", 200000000000.0, NiceNumberRounder.round(172034506708.90123, true));
    }

    public void testRoundNumberLargeLower() {
        assertEquals("Unexpected ", 200000000000.0, NiceNumberRounder.round(172034506708.90123, true));
    }
    
    public void testRoundNumber() {
        int index = 0;
        for (double inc = 0; inc < 1000; inc += 30.0) {
            double value = BASE_VALUE + inc;
            //System.out.print(NiceNumberRounder.round(value, true) +", ");
            assertEquals("Unexpected rounded value for " + value,
                    EXPECTED_ROUNDED_VALUES[index], NiceNumberRounder.round(value, true));
            index++;
        }
        assertEquals("Unexpected ", 200000000000.0, NiceNumberRounder.round(172034506708.90123, true));
    }



    public void testCielNumberSmall() {
        assertEquals("Unexpected ", 0.002, NiceNumberRounder.round(0.001234567, false));
    }

    public void testCielNumberMediumUpper() {
        assertEquals("Unexpected ", 10.0, NiceNumberRounder.round(8.87653, false));
    }

    public void testCielNumberMediumLower() {
        assertEquals("Unexpected ", 5.0, NiceNumberRounder.round(4.363, false));
    }

    public void testCielNumberLargeUpper() {
        assertEquals("Unexpected ", 200000000000.0, NiceNumberRounder.round(172034506708.90123, false));
    }

    public void testCielNumberLargeLower() {
        assertEquals("Unexpected ", 200000000000.0, NiceNumberRounder.round(102034506708.90123, false));
    }

    public void testCieledNumber() {
        int index = 0;
        for (double inc = 0; inc < 1000; inc += 30.0) {
            double value = BASE_VALUE + inc;
            //System.out.print(NiceNumberRounder.round(value, false) +", ");
            assertEquals("Unexpected rounded value for " + value,
                    EXPECTED_CEILED_VALUES[index], NiceNumberRounder.round(value, false));
            index++;
        }
    }
}
