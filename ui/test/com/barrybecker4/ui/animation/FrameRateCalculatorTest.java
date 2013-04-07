// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.ui.animation;

import com.barrybecker4.common.concurrency.ThreadUtil;
import junit.framework.TestCase;

/**
 * @author Barry Becker
 */
public class FrameRateCalculatorTest extends TestCase {

    /** instance under test. */
    private FrameRateCalculator calculator;


    @Override
    public void setUp() {
        calculator = new FrameRateCalculator();
    }


    /** The frame rate given on the first frame is 0 because no frame rendered yet. */
    public void testDefaultFrameRate() {

        assertEquals("Unexpected initial frame count", 0, calculator.getFrameCount());
        assertEquals("Unexpected initial frame rate", 0.0, calculator.getFrameRate());
    }

    public void testFrameCount() {
        calculator.incrementFrameCount();
        calculator.incrementFrameCount();
        assertEquals("Unexpected count", 2, calculator.getFrameCount());
    }

    public void testFrameRateAfter1() {
        ThreadUtil.sleep(100);
        calculator.incrementFrameCount();
        assertEquals("Unexpected initial frame rate.", 10.0, calculator.getFrameRate(), 0.5);
    }

    public void testInitialFrameRateWithDelay() {
        ThreadUtil.sleep(100);
        assertEquals("Unexpected initial frame rate.", 0.0, calculator.getFrameRate(), 1.0);
    }

    public void testFrameRateAfter1WithDelay() {
        calculator.incrementFrameCount();
        ThreadUtil.sleep(100);
        assertEquals("Unexpected initial frame rate.", 9.9, calculator.getFrameRate(), 1.0);
    }

    public void testFrameRateAfter3() {
        verifyFrameRateAfterN(3, 0, 0.0, 0.1);
    }

    public void testFrameRateAfter3WithDelayForAllButLast() {
        calculator.incrementFrameCount();
        ThreadUtil.sleep(50);
        calculator.incrementFrameCount();
        ThreadUtil.sleep(50);
        calculator.incrementFrameCount();
        assertEquals("Unexpected frame rate.", 30.0, calculator.getFrameRate(), 0.1);
    }

    public void testFrameRateAWithPause() {
        calculator.incrementFrameCount();
        ThreadUtil.sleep(50);
        calculator.incrementFrameCount();
        ThreadUtil.sleep(50);

        calculator.setPaused(true);
        ThreadUtil.sleep(200);
        calculator.setPaused(false);

        calculator.incrementFrameCount();
        assertEquals("Unexpected frame rate.", 30.0, calculator.getFrameRate(), 0.1);
    }

    public void testFrameRateAWithTwoPauses() {
        calculator.incrementFrameCount();
        calculator.setPaused(false);
        ThreadUtil.sleep(50);

        calculator.setPaused(true);
        calculator.setPaused(true);
        ThreadUtil.sleep(200);
        calculator.setPaused(false);

        calculator.incrementFrameCount();
        ThreadUtil.sleep(50);

        calculator.setPaused(false);
        calculator.setPaused(true);
        ThreadUtil.sleep(200);
        calculator.setPaused(false);

        calculator.incrementFrameCount();
        assertEquals("Unexpected frame rate.", 29.8, calculator.getFrameRate(), 0.2);
    }

    public void testFrameRateAfter3WithDelayOf50() {
        verifyFrameRateAfterN(3, 50, 20.0);
    }

    public void testFrameRateAfter3WithDelayOf100() {
        verifyFrameRateAfterN(3, 100, 9.9);
    }

    public void testFrameRateAfter6WithDelayOf100() {
        verifyFrameRateAfterN(6, 100, 10.0);
    }

    public void testFrameRateAfter12WithDelayOf100() {
        verifyFrameRateAfterN(12, 100, 10.0);
    }

    public void testFrameRateAfter24WithDelayOf100() {
        verifyFrameRateAfterN(24, 100, 10.0);
    }

    /*
    public void testFrameRateAfter48WithDelayOf100() {
        verifyFrameRateAfterN(48, 100, 10.0);
    }
    public void testFrameRateAfter96WithDelayOf100() {
        verifyFrameRateAfterN(96, 100, 10.0);
    }
    public void testFrameRateAfter3WithDelayOf200() {
        verifyFrameRateAfterN(3, 200, 5.0);
    }  */

    public void testFrameRateAfterFilled() {
        verifyFrameRateAfterN(100, 0, Double.POSITIVE_INFINITY);
    }

    public void testFrameRateWithDelayAfterFilled100() {
        verifyFrameRateAfterN(100, 10, 98.0, 5.0);
    }

    /*
    public void testFrameRateWithDelayAfterFilled200() {
        verifyFrameRateAfterN(200, 10, 100.0);
    } */

    private void verifyFrameRateAfterN(int numFrames, int frameDelay, double expRate) {
         verifyFrameRateAfterN(numFrames, frameDelay, expRate, 0.2);
    }

    /**
     *
     * @param numFrames  number of frames to simulate
     * @param frameDelay artificial delay in milliseconds to create each frame
     * @param expRate expected frame rate.
     * @param tolerance
     */
    private void verifyFrameRateAfterN(int numFrames, int frameDelay, double expRate, double tolerance) {
        for (int i=0; i<numFrames; i++)  {
            calculator.incrementFrameCount();
            ThreadUtil.sleep(frameDelay);
        }
        assertEquals("Unexpected frameRate", expRate, calculator.getFrameRate(), tolerance);
    }

}
