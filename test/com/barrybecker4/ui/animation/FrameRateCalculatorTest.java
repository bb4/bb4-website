// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.ui.animation;

import com.barrybecker4.common.concurrency.ThreadUtil;
import junit.framework.TestCase;

/**
 * @author Barry Becker
 */
public class FrameRateCalculatorTest extends TestCase {

    /** instance under test. */
    private static final FrameRateCalculator calculator = new FrameRateCalculator();

    private static final double EPS = 0.00001;


    public void testDefaultFrameRate() {

        assertEquals("Unexpected initial frame count", 0, calculator.getFrameCount());
        assertEquals("Unexpected initial frame rate", 160.0, calculator.getFrameRate(), 100.0);
    }

    public void testFrameCount() {

        calculator.incrementFrameCount();
        calculator.incrementFrameCount();
        assertEquals("Unexpected count", 2, calculator.getFrameCount());
    }

    public void testFrameRateAfter1() {

        calculator.incrementFrameCount();
        assertEquals("Unexpected initial frame rate.", 120.0, calculator.getFrameRate(), 100.0);
    }

    public void testFrameRateAfter1WithDelay() {

        calculator.incrementFrameCount();
        ThreadUtil.sleep(150);
        assertEquals("Unexpected initial frame rate.", 6.3, calculator.getFrameRate(), 0.2);
    }


    public void testFrameRateAfter3() {

        calculator.incrementFrameCount();
        calculator.incrementFrameCount();
        calculator.incrementFrameCount();
        assertEquals("Unexpected initial frame rate.", 6.1, calculator.getFrameRate(), 0.5);
    }

    public void testFrameRateAfter3WithDelayForAllButLast() {

        calculator.incrementFrameCount();
        ThreadUtil.sleep(50);
        calculator.incrementFrameCount();
        ThreadUtil.sleep(50);
        calculator.incrementFrameCount();
        assertEquals("Unexpected initial frame rate.", 3.5, calculator.getFrameRate(), 0.5);
    }

    public void testFrameRateAfter3WithDelay() {

        calculator.incrementFrameCount();
        ThreadUtil.sleep(50);
        calculator.incrementFrameCount();
        ThreadUtil.sleep(50);
        calculator.incrementFrameCount();
        ThreadUtil.sleep(50);
        assertEquals("Unexpected initial frame count", 2.9, calculator.getFrameRate(), 0.5);
    }

    public void testFrameRateAfterFull() {

        for (int i=0; i<200; i++) {
            calculator.incrementFrameCount();
        }
        assertEquals("Unexpected initial frame count", 2.8, calculator.getFrameRate(), 0.4);
    }

    public void testFrameRateWithDelayAfterFull() {

        for (int i=0; i<100; i++) {
            calculator.incrementFrameCount();
            ThreadUtil.sleep(10);
        }

        assertEquals("Unexpected initial frame count", 0.79, calculator.getFrameRate(), 0.1);

    }
}
