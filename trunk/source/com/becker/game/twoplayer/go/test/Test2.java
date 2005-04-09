package com.becker.game.twoplayer.go.test;

import junit.framework.TestCase;
import junit.framework.Assert;
import junit.framework.TestSuite;
import junit.framework.Test;


public class Test2 extends TestCase {

    public void testSimpleAdd2() {

        double a = 5.5;
        double b = 5.6;
        System.out.println("testing simple add2...");
        Assert.assertTrue(b > a);
    }

     public void testSimpleSubtract2() {

        double a = 7.5;
        double b = 8.6;
        System.out.println("testing simple subtract2...");
        Assert.assertTrue(b > a);
    }

     public void testSimpleTimes2() {

        double a = 10;
        double b = 10;
        System.out.println("testing simple times2...");
        Assert.assertTrue( a*b == 100);
    }

    public static Test suite() {
        return new TestSuite(Test2.class);
    }
}