// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.ui.animation;

/**
 * Parameters that control the animation.
 * Calculates the framerate.
 * @see AnimationComponent
 * @author Barry Becker
 */
class AnimationParameters  {

    /** True when animating */
    public boolean animating = true;

    /** Set to true when the animation is to be paused. */
    public boolean paused = true;

    /** Number of iteration steps made before showing a new animation frame. */
    public int numStepsPerFrame = 1;

    /** if true it will save all the animation steps to files  */
    public boolean recordAnimation = false;

    /** frames per second. */
    private double frameRate_;

    /** previous times in milliseconds. */
    private long[] previousTimes_;

    private int previousIndex_;
    private boolean previousFilled_;

    /** incremented for every frame that is shown */
    private int frameCount_ = 0;


    /** Constructor */
    public AnimationParameters() {
        previousTimes_ = new long[64];
        previousTimes_[0] = System.currentTimeMillis();
        previousIndex_ = 1;
        previousFilled_ = false;
    }

    /**
     * set the number of time steps to computer for every frame of animation
     *  for unstable calculations using simple numerical methods (like Eulers integration for eg)
     *  this can speed things a lot.
     */
    public void setNumStepsPerFrame( int num ) {
        numStepsPerFrame = num;
    }

    public int getNumStepsPerFrame() {
        return numStepsPerFrame;
    }

    public void incrementFrameCount() {
        frameCount_++;
    }

    /** @return the number of animation frames so far */
    public int getFrameCount() {
        return frameCount_;
    }


    /**
     * Determine the number of frames per second as a moving average.
     */
    public void calculateFrameRate() {

        long now = System.currentTimeMillis();
        int numberOfFrames = previousTimes_.length;
        double newRate;
        // Use the more stable method if a history is available.
        if ( previousFilled_ ) {
            newRate = (double) numberOfFrames /
                    (double) (now - previousTimes_[previousIndex_]) *
                    1000.0;
        }
        else {
            newRate = 1000.0 /
                    (double) (now - previousTimes_[numberOfFrames - 1]);
        }
        frameRate_ = newRate;

        // Update the history.
        previousTimes_[previousIndex_] = now;
        previousIndex_++;
        if ( previousIndex_ >= numberOfFrames ) {
            previousIndex_ = 0;
            previousFilled_ = true;
        }
    }


    public double getFrameRate() {
        return frameRate_;
    }

}