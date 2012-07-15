/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.animation;

import com.barrybecker4.common.concurrency.ThreadUtil;
import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.common.util.ImageUtil;

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

    /** An image showing the current animation frame */
    protected volatile Image image_;

    /** parameters controlling the animation */
    private AnimationParameters params;


    /** Constructor */
    public AnimationComponent() {
        params = new AnimationParameters();
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

            params.incrementFrameCount();

            if ( params.recordAnimation &&  image_ != null ) {
                    String fname = getFileNameBase() + Integer.toString( 1000000 + params.getFrameCount());
                    ImageUtil.saveAsImage( fname, this.image_, ImageUtil.ImageType.PNG );
            }

            if (isPaused()) {

                ThreadUtil.sleep(500);

            } else {
                render();

                for ( int i = 0; i < params.getNumStepsPerFrame(); i++ )  {
                    timeStep();
                }
                calculateFrameRate();
                firePropertyChange( "statusChanged", getStatusMessage() );
            }
        }
    }

    protected void calculateFrameRate() {
        params.calculateFrameRate();
    }

    protected boolean isAnimating() {
        return params.animating;
    }

    protected void setAnimating(boolean animating) {
        if (animating != params.animating) {
            if (animating) {
                params.animating = true;
                new Thread( this ).start();
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
     * Check for offscreen image. Creates it if needed.
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
     * Message to show in the status bar at the bottom
     */
    protected String getStatusMessage() {
        return FormatUtil.formatNumber(getFrameRate()) + " fps";
    }

    public double getFrameRate() {
        return params.getFrameRate();
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

    protected void firePropertyChange( String name, String newValue ) {
        animationChangeListener_.statusChanged( newValue );
    }
}