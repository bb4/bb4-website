package com.becker.common.test.util;

import junit.framework.*;
import com.becker.common.math.MathUtil;

/**
 * @author Barry Becker Date: Apr 2, 2006
 */
public class MathUtilTest extends TestCase {

    private static final double EPS = 0.00000000000001;

    public void testPositiveGCD() {
        long result;
        
        result = MathUtil.gcd(2l, 4l);               
        Assert.assertEquals(result, 2l);
        
        result = MathUtil.gcd(4l, 2l);               
        Assert.assertEquals(result, 2l);
        
        result = MathUtil.gcd(420l, -40l);               
        Assert.assertEquals(result, 20l);
        
        result = MathUtil.gcd(40l, 420l);               
        Assert.assertEquals(result, 20l);
        
    }
    
    public void testNegativeGCD() {
        long result;
        
        result = MathUtil.gcd(2L, 0L);               
        Assert.assertEquals(result, 2L);
        
        result = MathUtil.gcd(0L, 2L);               
        Assert.assertEquals(result, 2L);
        
        result = MathUtil.gcd(423L, -40L);               
        Assert.assertEquals(result, 1L);               
    }

    public void testIntNeg() {
        Assert.assertEquals("1) ", 2, (int)2.1);
        Assert.assertEquals("2) ", 0, (int)(-0.1));
        Assert.assertEquals("3) ", -2, (int)(-2.1));
        Assert.assertEquals("4) ", -2, (int)(-2.9));
    }

    /** redo using interpolation classes
    public void testSimpleLinearInterpolate() {
        double[] func = {0, 1, 2};

        double y = MathUtil.linearInterpolate(0.1, func);
        Assert.assertEquals("Unexpected y for 0.1", 0.2, y);

        y = MathUtil.linearInterpolate(0.9, func);
        Assert.assertEquals("Unexpected y for 0.9", 1.8, y);
    }


    public void testLinearInterpolate() {
        double[] func = {1, 2, 3};

        double y = MathUtil.linearInterpolate(0.1, func);
        Assert.assertEquals("Unexpected y for 0.1", 1.2, y, EPS);

        y = MathUtil.linearInterpolate(0.9, func);
        Assert.assertEquals("Unexpected y for 0.9", 2.8, y, EPS);
    }

    public void testLinearInterpolateOnePoint() {
        double[] func = {1};

        double y = MathUtil.linearInterpolate(0.0, func);
        Assert.assertEquals("Unexpected y for 0.0", 1.0, y, EPS);
    }

    public void testLinearInterpolateOutOfRangeClosePositive() {
        double[] func = {1, 2};
        try {
            MathUtil.linearInterpolate(1.1, func);
            Assert.fail("Did not expect to get here");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            // Success
        }
    }

    public void testLinearInterpolateOutOfRangeFar() {
        double[] func = {1, 2};
        try {
            MathUtil.linearInterpolate(2.1, func);
            Assert.fail();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            // Success
        }
    }

    public void testLinearInterpolateOutOfRangeNegative() {
        double[] func = {1, 2};
        try {
            MathUtil.linearInterpolate(-1.1, func);
            Assert.fail();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            // Success
        }
    }


    public void testCubicInterpolate2Points() {
        double[] func = {0, 1};
        double y = MathUtil.cubicInterpolate(0.1, func);

        Assert.assertEquals("Unexpected y for 0.1", 0.1, y);
    }


    public void testCubicInterpolate() {
        double[] func = {1, 2, 3};
        double y = MathUtil.cubicInterpolate(0.1, func);

        Assert.assertEquals("Unexpectred y for 0.1", 0.1, y);
    }

    public void testCubicInterpolateLine() {
        double[] func = {1, 2, 3, 4};

        double y = MathUtil.cubicInterpolate(0.5, func);
        Assert.assertEquals("Unexpected y for 0.5", 2.5, y);

        y = MathUtil.cubicInterpolate(0.4, func);
        Assert.assertEquals("Unexpected y for 0.4", 2.296, y, EPS);
    }
    */

}
