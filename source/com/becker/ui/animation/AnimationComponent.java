package com.becker.ui.animation;

import com.becker.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A ui conponent for showing animations.
 */
public abstract class AnimationComponent extends Container implements Runnable
{

    protected boolean animating_ = true;
    protected int numStepsPerFrame_ = 1; // default
    protected long[] previousTimes_; // milliseconds
    protected int previousIndex_;
    protected boolean previousFilled_;
    protected double frameRate_; // frames per second
    protected Image image_;

    // incremented for every frame
    protected int frameCount_ = 0;

    // if true it will save all the animation steps to files
    private boolean recordAnimation_ = false;

    private boolean bPaused_ = true;

    // constants. See setSwitch().
    //public static final int PAUSE = 0;

    public AnimationComponent()
    {
        previousTimes_ = new long[128];
        previousTimes_[0] = System.currentTimeMillis();
        previousIndex_ = 1;
        previousFilled_ = false;
    }

    /**
     * if recordAnimation is true then each frame is written to a numbered file for
     * compilation into a movie later
     */
    public void setRecordAnimation( boolean doIt )
    {
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
    public void setNumStepsPerFrame( int num )
    {
        numStepsPerFrame_ = num;
    }

    public int getNumStepsPerFrame()
    {
        return numStepsPerFrame_;
    }

    public abstract double timeStep();

    // the base filename when recording
    protected abstract String getFileNameBase();

    public void run()
    {
        render();
        while ( animating_ ) {

            frameCount_++;

            if ( recordAnimation_ ) {
                //Dimension d = this.getSize();
                //BufferedImage bi = ImageUtil.makeBufferedImage(this.mImage);

                String fname = getFileNameBase() + Integer.toString( 1000000 + frameCount_ );
                if ( image_ != null ) {
                    //JOptionPane.showMessageDialog(this, "mImage("+fname+") width ="+mImage.getWidth(null));
                    //System.out.println("mImage width ="+mImage.getWidth(null));
                    ImageUtil.saveAsImage( fname, this.image_, ImageUtil.ImageType.PNG );
                }
            }

            if (isPaused()) {
                try {
                   Thread.sleep(100);
                } catch (InterruptedException e) {e.printStackTrace();};
            } else {
                render();

                for ( int i = 0; i < numStepsPerFrame_; i++ )  {
                    timeStep();
                }
                calculateFrameRate();
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
    protected void render()
    {
        Graphics g = getGraphics();
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
            g.dispose();
        }
    }

    // Offscreen image.
    protected boolean checkImage( Dimension d )
    {
        if ( d.width <= 0 || d.height <= 0 ) return false;
        if ( image_ == null || image_.getWidth( null ) != d.width
                || image_.getHeight( null ) != d.height) {
            image_ = createImage( d.width, d.height );
        }
        return true;
    }

    protected void calculateFrameRate()
    {
        // Measure the frame rate
        long now = System.currentTimeMillis();
        int numberOfFrames = previousTimes_.length;
        double newRate;
        // Use the more stable method if a history is available.
        if ( previousFilled_ )
            newRate = (double) numberOfFrames /
                    (double) (now - previousTimes_[previousIndex_]) *
                    1000.0;
        else
            newRate = 1000.0 /
                    (double) (now - previousTimes_[numberOfFrames - 1]);
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

    protected String getStatusMessage()
    {
        return Util.formatNumber( getFrameRate() ) + " fps";
    }

    public double getFrameRate()
    {
        return frameRate_;
    }


    // if paused is true the animation is stopped
    public void setPaused( boolean bPaused )
    {
        bPaused_ = bPaused;
    }

    public boolean isPaused()
    {
        return bPaused_;
    }


    // Property change support.
    private transient AnimationChangeListener animationChangeListener_;

    public void setChangeListener( AnimationChangeListener af )
    {
        animationChangeListener_ = af;
    }

    protected void firePropertyChange( String name, String newValue )
    {
        animationChangeListener_.statusChanged( newValue );
    }
}