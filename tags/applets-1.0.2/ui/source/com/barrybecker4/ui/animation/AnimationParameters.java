// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.ui.animation;

/**
 * Parameters that control the animation.
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
}