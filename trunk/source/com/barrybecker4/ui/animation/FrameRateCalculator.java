// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.ui.animation;

/**
 * Calculates the framerate given previous times in milliseconds
 * @see AnimationComponent
 * @author Barry Becker
 */
class FrameRateCalculator {

    /** keep times for the last 64 frames */
    private static final int HISTORY_LENGTH = 32;

    /** frames per second. */
    private double frameRate;

    /** previous times in milliseconds. */
    private long[] previousTimes;

    /** incremented for every frame that is shown */
    private long frameCount;

    /** becomes dirty when the frameCount changes */
    private boolean dirty = true;

    private long startPauseTime;
    private long totalPauseTime = 0;
    private boolean isPaused = false;


    /** Constructor */
    public FrameRateCalculator() {
        previousTimes = new long[HISTORY_LENGTH];
        frameCount = 0;
        previousTimes[0] = System.currentTimeMillis();
    }

    /**
     * This must be called right before each frame is rendered.
     */
    public void incrementFrameCount() {
        frameCount++;
        previousTimes[getIndex()] = System.currentTimeMillis();
        dirty = true;
    }

    /** @return the number of animation frames so far */
    public long getFrameCount() {
        return frameCount;
    }

    public double getFrameRate() {
        if (dirty) {
            calculateFrameRate();
        }
        return frameRate;
    }

    public void setPaused(boolean paused){
        System.out.println("p="+paused);
        if (paused != isPaused)  {
            if (paused) {
                startPauseTime = System.currentTimeMillis();
            }
            else {
                totalPauseTime += (System.currentTimeMillis() - startPauseTime);
            }
            isPaused = paused;
        }
    }

    /**
     * Determine the number of frames per second as a moving average.
     * Use the more stable method of calculation if all history is available.
     */
    private void calculateFrameRate() {

        long now = System.currentTimeMillis();
        double deltaTime;
        int index = getIndex();

        if (frameCount < HISTORY_LENGTH) {
            deltaTime = now - previousTimes[0] - totalPauseTime;
            frameRate = (deltaTime==0) ? 0.0 :(1000.0 * index) / deltaTime;

        } else {
            deltaTime = now - previousTimes[(index + 1) % HISTORY_LENGTH] - totalPauseTime;
            frameRate =  (1000.0 * HISTORY_LENGTH) / deltaTime;
        }
        //System.out.println("index=" + index + " deltaTime="  + deltaTime + " fct=" + frameCount + " fr="+ frameRate);

        dirty = false;
    }

    private int getIndex() {
        return (int)(frameCount % HISTORY_LENGTH);
    }
}