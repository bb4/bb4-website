/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.animation;

import com.barrybecker4.common.concurrency.ThreadUtil;
import com.barrybecker4.common.format.FormatUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * A ui component for showing animations.
 * The calculation and animation rendering are done in a separate thread so the
 * rest of the ui does not lock up.
 */
public abstract class AnimationComponent extends Container
                                      implements Runnable {

    /** parameters controlling the animation */
    private AnimationParameters params;

    /** An image showing the current animation frame */
    protected FrameRecorder recorder;

    private FrameRateCalculator frameRateCalc;


    /** Constructor */
    public AnimationComponent() {
        params = new AnimationParameters();
        frameRateCalc = new FrameRateCalculator();
        recorder = new FrameRecorder(getFileNameBase());
    }

    /**
     * if recordAnimation is true then each frame is written to a numbered file for
     * compilation into a movie later
     * @param doIt if true, then the animation will be recorded.
     */
    public void setRecordAnimation( boolean doIt ) {
        params.recordAnimation = doIt;
    }

    public boolean getRecordAnimation() {
        return params.recordAnimation;
    }

    /**
     * set the number of time steps to computer for every frame of animation
     *  for unstable calculations using simple numerical methods (like Eulers integration for eg)
     *  this can speed things a lot.
     */
    public void setNumStepsPerFrame( int num ) {
        params.numStepsPerFrame = num;
    }

    public int getNumStepsPerFrame() {
        return params.numStepsPerFrame;
    }

    public abstract double timeStep();

    /** @return  the base filename when recording  */
    protected abstract String getFileNameBase();

    /**
     * Do the timeStepping and rendering in a separate thread
     * so the rest of the GUI does not freeze and can still handle events.
     */
    public void run() {

        render();
        while ( params.animating ) {

            if (isPaused()) {
                ThreadUtil.sleep(500);
            }
            else {
                render();
                frameRateCalc.incrementFrameCount();

                if (params.recordAnimation) {
                    recorder.saveFrame(frameRateCalc.getFrameCount());
                }

                for (int i = 0; i < params.getNumStepsPerFrame(); i++)  {
                    timeStep();
                }

                animationChangeListener_.statusChanged(getStatusMessage());
            }
        }
    }

    protected boolean isAnimating() {
        return params.animating;
    }

    protected void setAnimating(boolean animating) {
        if (animating != params.animating) {
            if (animating) {
                params.animating = true;
                new Thread(this).start();
            } else {
                params.animating = false;
            }
        }
    }

    /**
     * @return a start button that says Pause or Resume once started.
     */
    protected JToggleButton createStartButton()  {

        final JToggleButton toggleButton = new JToggleButton( "Start", true);
        toggleButton.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent ie ) {
                boolean paused = ie.getStateChange() == ItemEvent.SELECTED;
                toggleButton.setText(paused ? "Resume" : "Pause");
                setPaused(paused);
            }
        } );
        return toggleButton;
    }

    /**
     * render the animation component as an image
     */
    protected void render() {
        if (params.recordAnimation) {
            recorder.renderImage(this);
        } else {
            paint(getGraphics());
        }
    }

    /**
     * Message to show in the status bar at the bottom
     */
    protected String getStatusMessage() {
        return FormatUtil.formatNumber(getFrameRate()) + " fps";
    }

    public double getFrameRate() {
        return frameRateCalc.getFrameRate();
    }

    /**
     * If paused is true the animation is stopped
     * @param paused true if you want the animation to stop temporarily.
     */
    public void setPaused( boolean paused ) {
        params.paused = paused;
    }

    /**
     * @return true if currently paused.
     */
    public boolean isPaused() {
        return params.paused;
    }

    /** Property change support. */
    private transient AnimationChangeListener animationChangeListener_;

    public void setChangeListener( AnimationChangeListener af ) {
        animationChangeListener_ = af;
    }
}