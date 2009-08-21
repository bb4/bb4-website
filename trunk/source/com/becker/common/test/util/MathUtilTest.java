package com.becker.common.test.util;

import junit.framework.*;
import com.becker.common.util.MathUtil;

import java.util.*;

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

}
