package com.becker.liquid;

import com.becker.java2d.examples.Bouncer;
import com.becker.java2d.ImageUtil;
import com.becker.ui.AnimationComponent;
import com.becker.ui.AnimationFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LiquidSimulator extends AnimationComponent
{

    //public static final String CONFIG_FILE = "com/becker/liquid/initialState.data";
    public static final String CONFIG_FILE = "com/becker/liquid/initialStateTest.data";

    LiquidEnvironment environment_ = null;

    // Tweakable variables
    private boolean bPause = false;

    // constants. See setSwitch().
    public static final int PAUSE = 0;
    // if true it will save all the animation steps to files
    public static final boolean RECORD_ANIMATION = false;

    public static final double TIME_STEP = 0.04;  // initial time step
    private double timeStep_ = TIME_STEP;

    private static final Color BG_COLOR = Color.white;

    private static final String FILE_NAME_BASE = "e:/becker/animation/frame";
    // incremented for every frame
    private int frameCount_ = 0;

    public LiquidSimulator( LiquidEnvironment environment )
    {

        environment_ = environment;
        // Make sure points are within range.
        addComponentListener( new ComponentAdapter()
        {
            public void componentResized( ComponentEvent ce )
            {
                //System.out.println("resized");
            }
        } );
    }

    public void setSwitch( int item, boolean value )
    {
        switch (item) {
            case PAUSE:
                bPause = value;
                break;
            default:
                break;
        }
    }

    protected Checkbox createCheckbox( String label, final int item )
    {
        Checkbox check = new Checkbox( label, false );
        check.addItemListener( new ItemListener()
        {
            public void itemStateChanged( ItemEvent ie )
            {
                setSwitch( item, (ie.getStateChange() == ItemEvent.SELECTED) );
            }
        } );
        return check;
    }

    /**
     * @return  a new recommended time step change.
     */
    public double timeStep()
    {
        if ( !bPause ) {
            timeStep_ = environment_.stepForward( timeStep_);

            if ( RECORD_ANIMATION ) {
                Dimension d = this.getSize();
                //BufferedImage bi = ImageUtil.makeBufferedImage(this.mImage);

                String fname = getFileNameBase() + Integer.toString( 1000000 + frameCount_ );
                if ( mImage != null ) {
                    //JOptionPane.showMessageDialog(this, "mImage("+fname+") width ="+mImage.getWidth(null));
                    //System.out.println("mImage width ="+mImage.getWidth(null));
                    ImageUtil.saveAsImage( fname, this.mImage, "png" );
                }
            }
            frameCount_++;
        }
        return timeStep_;
    }

    public Color getBackground()
    {
        return BG_COLOR;
    }

    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;
        EnvironmentRenderer.render(environment_, g2 );
    }

    protected void setPause( Graphics2D g2 )
    {
        if ( bPause == false ) return;
        // do something to pause the animation
    }

    protected String getFileNameBase()
    {
        return FILE_NAME_BASE;
    }

    // *************** main *****************************
    public static void main( String[] args )
    {

        final LiquidEnvironment environment =
                new LiquidEnvironment( 20, 15 );
        //new LiquidEnvironment(CONFIG_FILE);

        final LiquidSimulator simulator = new LiquidSimulator( environment );
        JFrame f = new AnimationFrame( simulator );
        f.setFont( new Font( "Serif", Font.PLAIN, 12 ) );
        f.setSize( 600, 800 );

        Panel controls = new Panel();
        controls.add( simulator.createCheckbox( "Pause", Bouncer.ANTIALIASING ) );
        //controls.add(simulator.getStepButton());
        f.getContentPane().add( controls, BorderLayout.NORTH );

        f.setVisible( true );
    }
}