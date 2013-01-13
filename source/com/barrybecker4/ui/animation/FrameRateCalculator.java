// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.ui.animation;

/**
 * Calculates the framerate given previous times in milliseconds
 * @see AnimationComponent
 * @author Barry Becker
 */
class FrameRateCalculator {

    /** keep times for the last 64 frames */
    private static final int HISTORY_LENGTH = 64;

    /** frames per second. */
    private double frameRate;

    /** previous times in milliseconds. */
    private long[] previousTimes;

    private int index;
    private boolean previousFilled;

    /** incremented for every frame that is shown */
    private int frameCount_ = 0;

    /** becomes dirty when the frameCount changes */
    private boolean dirty = true;


    /** Constructor */
    public FrameRateCalculator() {
        previousTimes = new long[HISTORY_LENGTH];
        previousTimes[0] = System.currentTimeMillis();
        index = 1;
        previousFilled = false;
    }

    public void incrementFrameCount() {
        frameCount_++;
        dirty = true;
    }

    /** @return the number of animation frames so far */
    public int getFrameCount() {
        return frameCount_;
    }

    public double getFrameRate() {
        if (dirty) {
            calculateFrameRate();
        }
        return frameRate;
    }

    /**
     * Determine the number of frames per second as a moving average.
     */
    private void calculateFrameRate() {

        long now = System.currentTimeMillis();
        double deltaTime;

        // Use the more stable method if a history is available.
        if ( previousFilled ) {
            deltaTime =  now - previousTimes[(index + 1) % HISTORY_LENGTH];
            System.out.println("prevInd="+ index + " deltaTime="+ deltaTime + " prevTime=" + previousTimes[index] + " fct=" + frameCount_);
            System.out.flush();
            frameRate =  (1000.0 * HISTORY_LENGTH)  / deltaTime;
        }
        else {
            deltaTime = now - previousTimes[0];
            //System.out.println("prevInd="+ index + " deltaTime=" + deltaTime + " previousTimes[0]=" + previousTimes[0]+ " fct=" + frameCount);
            frameRate = 1000.0 / deltaTime;
        }

        // Update the history.
        previousTimes[index++] = now;

        if ( index >= HISTORY_LENGTH ) {
            index = 0;
            previousFilled = true;
        }
        dirty = false;
    }
}