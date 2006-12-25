package com.becker.common.test;

import junit.framework.*;
import junit.framework.Assert;
import com.becker.common.*;

import java.util.*;

/**
 * @author Barry Becker Date: Apr 2, 2006
 */
public class NiceNumbersTest extends TestCase {


    /**
     * common initialization for all go test cases.
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private static final String[] EXPECTED_LOOSE_CUTS = {"0", "20", "40", "60", "80", "100", "120"};
    private static final String[] EXPECTED_TIGHT_CUTS = {"11", "20", "40", "60", "80", "101"};

    public void testNiceNumbers1() {
        String[] resultLoose = NiceNumbers.getCutPointLabels(11.0, 101.0, 5, false);
        String[] resultTight = NiceNumbers.getCutPointLabels(11.0, 101.0, 5, true);
        Assert.assertTrue("loose "+ Arrays.toString(resultLoose),
                              Arrays.equals(resultLoose, EXPECTED_LOOSE_CUTS));
        Assert.assertTrue("loose "+ Arrays.toString(resultTight),
                              Arrays.equals(resultTight, EXPECTED_TIGHT_CUTS));
    }

    private static final String[] EXPECTED_LOOSE_CUTS2 = {"11.1", "11.15", "11.2", "11.25"};
    private static final String[] EXPECTED_TIGHT_CUTS2 = {"11.1", "11.15", "11.2", "11.23"};

    public void testNiceNumbers2() {
             String[] resultLoose = NiceNumbers.getCutPointLabels(11.1, 11.23, 5, false);
        String[] resultTight = NiceNumbers.getCutPointLabels(11.1, 11.23, 5, true);
        Assert.assertTrue("loose "+ Arrays.toString(resultLoose),
                              Arrays.equals(resultLoose, EXPECTED_LOOSE_CUTS2));
        Assert.assertTrue("loose "+ Arrays.toString(resultTight),
                              Arrays.equals(resultTight, EXPECTED_TIGHT_CUTS2));
    }

    public void testFracDicgits1() {
        double expectedNumFractDigits = 0;
        double f = NiceNumbers.getNumberOfFractionDigits(10, 100, 3);
        Assert.assertTrue("Expecteing f= "+expectedNumFractDigits+", but got " + f,
                                  (f == expectedNumFractDigits));
        f = NiceNumbers.getNumberOfFractionDigits(1000, 100000, 10);
        Assert.assertTrue("Expecteing f= "+expectedNumFractDigits+", but got " + f,
                                  (f == expectedNumFractDigits));

        f = NiceNumbers.getNumberOfFractionDigits(-1000, -100, 300);
        Assert.assertTrue("Expecteing f= "+expectedNumFractDigits+", but got " + f,
                                  (f == expectedNumFractDigits));
    }

    public void testFracDicgits2() {
        double f = NiceNumbers.getNumberOfFractionDigits(0.001, -0.002, 300);
        Assert.assertTrue("Expecteing f= 0.0, but got " + f,
                                  (f == 0.0));
    }


    public void testFracDicgits3() {
        double f = NiceNumbers.getNumberOfFractionDigits(0.0000001, 0.0001, 30);
        Assert.assertTrue("Expecteing f= 6.0, but got " + f,
                                  (f == 6.0));
    }

    public void testFracDicgits4() {
        double f = NiceNumbers.getNumberOfFractionDigits(-100.01, -100.001, 30);
        Assert.assertTrue("Expecteing f= 4.0, but got " + f,
                                  (f == 4.0));
    }

}