package com.becker.common.util;

import junit.framework.*;
import com.becker.common.math.MathUtil;

/**
 * @author Barry Becker Date: Apr 2, 2006
 */
public class MathUtilTest extends TestCase {

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

    public void testFactorial() {
        Assert.assertEquals("Unexpected value for 4!", 24.0, MathUtil.factorial(4));
    }
}
