package com.becker.simulation.common;

import com.becker.common.*;
import com.becker.optimization.*;
import com.becker.ui.*;

import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Barry Becker Date: Sep 17, 2005
 */
public abstract class Simulator extends AnimationComponent implements Optimizee {


    protected static final String CONFIG_FILE_PATH_PREFIX = Util.PROJECT_DIR + "source/com/becker/simulation/";
    protected static final String ANIMATION_FRAME_FILE_NAME_PREFIX = Util.PROJECT_DIR + "animations/simulation/";

    // debug level of 0 means no debug info, 3 is all debug info
    public static final int DEBUG_LEVEL = 0;

    // the amount to advance the animation in time for each frame in seconds
    protected static final int NUM_STEPS_PER_FRAME = 200;

    protected SimulatorOptionsDialog optionsDialog_ = null;
    protected static JFrame frame_ = null;

    // rendering options
    protected double timeStep_;
    protected boolean useAntialiasing_ = true;

    /**
     *
     * @param name the name fo the simulator (eg Snake, Liquid, or Trebuchet)
     */
    public Simulator(String name) {
        setName(name);
        timeStep_ = getInitialTimeStep();
    }

    protected void initCommonUI() {
        GUIUtil.setCustomLookAndFeel();





        //@@ need this?
        addComponentListener( new ComponentAdapter()
        {
            public void componentResized( ComponentEvent ce )
            {
                //System.out.println("resized");
            }
        } );
    }

    protected abstract double getInitialTimeStep();


    public abstract void doOptimization();



    public void setTimeStep( double timeStep )
    {
        timeStep_ = timeStep;
    }

    public double getTimeStep()
    {
        return timeStep_;
    }

    public void setAntialiasing( boolean use ) {
        useAntialiasing_ = use;
    }
    public boolean getAntialiasing() {
        return useAntialiasing_;
    }

    public abstract void setScale( double scale );
    public abstract double getScale();

    public abstract void setShowVelocityVectors( boolean show );
    public abstract boolean getShowVelocityVectors();

    public abstract void setShowForceVectors( boolean show );
    public abstract boolean getShowForceVectors();

    public abstract void setDrawMesh( boolean use );
    public abstract boolean getDrawMesh();

    public abstract void setStaticFriction( double staticFriction );
    public abstract double getStaticFriction();

    public abstract void setDynamicFriction( double dynamicFriction );
    public abstract double getDynamicFriction();


    protected GradientButton createOptionsButton()
    {
        GradientButton button = new GradientButton( "Options" );

        optionsDialog_ = createOptionsDialog();

        button.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                //System.out.println("options button pressed");

                optionsDialog_.setLocationRelativeTo( (Component) e.getSource() );
                // pause the snake while the options are open
                final Simulator simulator = optionsDialog_.getSimulator();
                final boolean oldPauseVal = simulator.isPaused();
                simulator.setPaused( true );
                final boolean canceled = optionsDialog_.showDialog();
                simulator.setPaused( oldPauseVal );
                System.out.println( "options selected  canceled=" + canceled );
            }
        } );
        return button;
    }


    protected abstract SimulatorOptionsDialog createOptionsDialog();


    public void setSwitch( int item, boolean value )
    {
        System.out.println( "item=" + item + " value=" + value );
        switch (item) {
            case PAUSE:
                setPaused(value);
                break;
            default:
                break;
        }
    }


    public JPanel createTopControls()
    {
        JPanel controls = new JPanel();
        controls.add( createCheckbox( "Pause", PAUSE, true ) );

        controls.add( createOptionsButton() );
        //controls.add(simulator.getStepButton());
        return controls;
    }

    protected String getStatusMessage()
    {
        return "frames/second=" + Util.formatNumber( getFrameRate() );
    }

    public static void drawGridBackground(Graphics2D g2, Color gridColor, double cellSize,
                                          int xDim, int yDim, Vector2d offset) {
        // draw the grid background
        g2.setColor( gridColor );
        int xMax = (int) (cellSize * xDim) - 1;
        int yMax = (int) (cellSize * yDim) - 1;
        int j;
        double pos = offset.y % cellSize;
        for ( j = 0; j <= yDim; j++ ) {
            int ht = (int) (pos + j * cellSize);
            g2.drawLine( 1, ht, xMax, ht );
        }
        pos = offset.x % cellSize;
        for ( j = 0; j <= xDim; j++ ) {
            int w = (int) (pos + j * cellSize);
            g2.drawLine( w, 1, w, yMax );
        }
    }



    ///////// the next 2 methods implement the unused methods of the optimizee interface. Simulators must implement evaluateFitness //////

    /**
     * If true is returned then compareFitness will be used and evaluateFitness will not
     * otherwise the reverse will be true.
     * @return return true if we evaluate the fitness by comparison
     */
    public boolean  evaluateByComparison()
     {
         return false;
     }

    public double compareFitness( ParameterArray params1, ParameterArray params2 )
    {
        return 0.0;
    }
}
