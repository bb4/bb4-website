package com.becker.ui;

import com.becker.common.*;
import com.becker.java2d.*;

import java.awt.*;
import java.awt.event.*;

public abstract class AnimationComponent extends Container implements Runnable
{

    protected boolean mTrucking = true;
    protected int numStepsPerFrame_ = 1; // default
    protected long[] mPreviousTimes; // milliseconds
    protected int mPreviousIndex;
    protected boolean mPreviousFilled;
    protected double mFrameRate; // frames per second
    protected Image mImage;

    // incremented for every frame
    protected int frameCount_ = 0;

    // if true it will save all the animation steps to files
    private boolean recordAnimation_ = false;

    // constants. See setSwitch().
    public static final int PAUSE = 0;

    public AnimationComponent()
    {
        mPreviousTimes = new long[128];
        mPreviousTimes[0] = System.currentTimeMillis();
        mPreviousIndex = 1;
        mPreviousFilled = false;
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
    protected String getFileNameBase()
    {
        return "D:/XXX";
    }

    public void run()
    {
        while ( mTrucking ) {
            render();
            frameCount_++;

            if ( recordAnimation_ ) {
                Dimension d = this.getSize();
                //BufferedImage bi = ImageUtil.makeBufferedImage(this.mImage);

                String fname = getFileNameBase() + Integer.toString( 1000000 + frameCount_ );
                if ( mImage != null ) {
                    //JOptionPane.showMessageDialog(this, "mImage("+fname+") width ="+mImage.getWidth(null));
                    //System.out.println("mImage width ="+mImage.getWidth(null));
                    ImageUtil.saveAsImage( fname, this.mImage, "png" );
                }
            }

            for ( int i = 0; i < numStepsPerFrame_; i++ )
                timeStep();


            calculateFrameRate();
        }
    }

    // create ui checkbox
    protected Checkbox createCheckbox( String label, final int item, boolean checked )
    {
        Checkbox check = new Checkbox( label, checked );
        check.addItemListener( new ItemListener()
        {
            public void itemStateChanged( ItemEvent ie )
            {
                setSwitch( item, (ie.getStateChange() == ItemEvent.SELECTED) );
            }
        } );
        return check;
    }

    public abstract void setSwitch( int item, boolean value );

    /**
     * render the animation component as an image
     */
    protected void render()
    {
        Graphics g = getGraphics();
        if ( g != null ) {
            Dimension d = getSize();
            if ( checkImage( d ) ) {
                Graphics imageGraphics = mImage.getGraphics();
                // Clear the image background.
                imageGraphics.setColor( getBackground() );
                imageGraphics.fillRect( 0, 0, d.width, d.height );
                imageGraphics.setColor( getForeground() );
                // Draw this component offscreen.
                paint( imageGraphics );
                // Now put the offscreen image on the screen.
                g.drawImage( mImage, 0, 0, null );
                // Clean up.
                imageGraphics.dispose();
            }
            g.dispose();
        }
    }

    // Offscreen image.
    protected boolean checkImage( Dimension d )
    {
        if ( d.width == 0 || d.height == 0 ) return false;
        if ( mImage == null || mImage.getWidth( null ) != d.width
                || mImage.getHeight( null ) != d.height ) {
            mImage = createImage( d.width, d.height );
        }
        return true;
    }

    protected void calculateFrameRate()
    {
        // Measure the frame rate
        long now = System.currentTimeMillis();
        int numberOfFrames = mPreviousTimes.length;
        double newRate;
        // Use the more stable method if a history is available.
        if ( mPreviousFilled )
            newRate = (double) numberOfFrames /
                    (double) (now - mPreviousTimes[mPreviousIndex]) *
                    1000.0;
        else
            newRate = 1000.0 /
                    (double) (now - mPreviousTimes[numberOfFrames - 1]);
        mFrameRate = newRate;

        firePropertyChange( "statusChanged", getStatusMessage() );

        // Update the history.
        mPreviousTimes[mPreviousIndex] = now;
        mPreviousIndex++;
        if ( mPreviousIndex >= numberOfFrames ) {
            mPreviousIndex = 0;
            mPreviousFilled = true;
        }
    }

    protected String getStatusMessage()
    {
        return Util.formatNumber( getFrameRate() ) + " fps";
    }

    public double getFrameRate()
    {
        return mFrameRate;
    }

    // Property change support.
    private transient AnimationChangeListener mAnimationChangeListener;

    public void setChangeListener( AnimationChangeListener af )
    {
        mAnimationChangeListener = af;
    }

    protected void firePropertyChange( String name, String newValue )
    {
        mAnimationChangeListener.statusChanged( newValue );
    }
}