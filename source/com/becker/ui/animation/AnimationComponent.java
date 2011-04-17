package com.becker.ui.animation;

import com.becker.common.util.ImageUtil;
import com.becker.common.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * A ui component for showing animations.
 * The calculation and animation rendering are done in a separate thread
 * So the rest of the ui does not lock up.
 */
public abstract class AnimationComponent extends Container implements Runnable {

    protected boolean animating_ = true;
    protected int numStepsPerFrame_ = 1;
    /** previous times in milliseconds. */
    protected long[] previousTimes_;
    protected int previousIndex_;
    protected boolean previousFilled_;
    /** frames per second. */
    protected double frameRate_;
    protected volatile Image image_;

    // incremented for every frame
    protected int frameCount_ = 0;

    // if true it will save all the animation steps to files
    private boolean recordAnimation_ = false;

    private boolean bPaused_ = true;

    public AnimationComponent() {
        previousTimes_ = new long[64];
        previousTimes_[0] = System.currentTimeMillis();
        previousIndex_ = 1;
        previousFilled_ = false;
    }

    /**
     * if recordAnimation is true then each frame is written to a numbered file for
     * compilation into a movie later
     */
    public void setRecordAnimation( boolean doIt ) {
        recordAnimation_ = doIt;
    }

    public boolean getRecordAnimation()
    {
        return recordAnimation_;
    }

    /**
     * set the number of timesteps to computer for every frame of animation
     *  for unstable calculations using simple numerical methods (like eulers integration for eg)
     *  this can speed things a lot.
     */
    public void setNumStepsPerFrame( int num ) {
        numStepsPerFrame_ = num;
    }

    public int getNumStepsPerFrame() {
        return numStepsPerFrame_;
    }

    public abstract double timeStep();

    // the base filename when recording
    protected abstract String getFileNameBase();

    /**
     * Do the timeStepping and rendering in a separate thread
     * so the rest of the GUI does not freeze and can still handle events.
     */
    public void run() {

        render();
        while ( animating_ ) {

            frameCount_++;

            if ( recordAnimation_ &&  image_ != null ) {             
                    String fname = getFileNameBase() + Integer.toString( 1000000 + frameCount_ );
                    ImageUtil.saveAsImage( fname, this.image_, ImageUtil.ImageType.PNG );
            }

            if (isPaused()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                render();

                for ( int i = 0; i < numStepsPerFrame_; i++ )  {
                    timeStep();
                }
                calculateFrameRate();
            }
        }
    }

    protected boolean isAnimating() {
        return animating_;
    }

    protected void setAnimating(boolean animating) {
        if (animating != animating_) {
            if (animating) {
                animating_ = true;
                new Thread( this ).start();
            } else {
                animating_ = false;
            }
        }
    }

    /**
     *
     * @return  a start button that says Pause or Resume once started.
     */
    protected JToggleButton createStartButton()
    {
        final JToggleButton toggleButton = new JToggleButton( "Start", true);
        toggleButton.addItemListener( new ItemListener()
        {
            public void itemStateChanged( ItemEvent ie )
            {
                //setSwitch( PAUSE, (ie.getStateChange() == ItemEvent.SELECTED) );
                boolean paused = ie.getStateChange() == ItemEvent.SELECTED;
                toggleButton.setText(paused ? "Resume" : "Pause");
                setPaused(paused);
            }
        } );
        return toggleButton;
    }

    //public abstract void setSwitch( int item, boolean value );

    /**
     * render the animation component as an image
     */
    protected void render() {

        Graphics2D g = (Graphics2D)getGraphics();
        if ( g != null ) {
            Dimension d = getSize();

            if ( checkImage( d ) ) {
                Graphics imageGraphics = image_.getGraphics();
                // Clear the image background.
                imageGraphics.setColor( getBackground() );
                imageGraphics.fillRect( 0, 0, d.width, d.height );
                imageGraphics.setColor( getForeground() );
                // Draw this component offscreen.
                paint( imageGraphics );
                // Now put the offscreen image on the screen.
                g.drawImage( image_, 0, 0, null );
                // Clean up.
                imageGraphics.dispose();
            }
        }
    }

    /**
     *  Offscreen image.
     */
    protected boolean checkImage( Dimension d ) {

        if ( d.width <= 0 || d.height <= 0 ) return false;
        if ( image_ == null || image_.getWidth( null ) != d.width
                || image_.getHeight( null ) != d.height) {
            image_ = createImage( d.width, d.height );
        }
        return true;
    }

    /**
     * Determine the number of frames per second as a moving average.
     */
    protected void calculateFrameRate() {

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

        firePropertyChange( "statusChanged", getStatusMessage() );

        // Update the history.
        previousTimes_[previousIndex_] = now;
        previousIndex_++;
        if ( previousIndex_ >= numberOfFrames ) {
            previousIndex_ = 0;
            previousFilled_ = true;
        }
    }

    /**
     *message to show in the status bar at the bottom
     */
    protected String getStatusMessage() {
        return Util.formatNumber( getFrameRate() ) + " fps";
    }

    public double getFrameRate() {
        return frameRate_;
    }

    /**
     * If paused is true the animation is stopped
     */

    public void setPaused( boolean bPaused ) {
        bPaused_ = bPaused;
    }

    public boolean isPaused() {
        return bPaused_;
    }


    // Property change support.
    private transient AnimationChangeListener animationChangeListener_;

    public void setChangeListener( AnimationChangeListener af ) {
        animationChangeListener_ = af;
    }

    protected void firePropertyChange( String name, String newValue ) {
        animationChangeListener_.statusChanged( newValue );
    }
}