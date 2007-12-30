package com.becker.simulation.common;

import com.becker.common.*;
import com.becker.common.util.FileUtil;
import com.becker.common.util.Util;
import com.becker.optimization.*;
import com.becker.ui.*;
import com.becker.ui.animation.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Base class for all simulations.
 *
 * @author Barry Becker Date: Sep 17, 2005
 */
public abstract class Simulator extends AnimationComponent
                                implements Optimizee {


    protected static final String CONFIG_FILE_PATH_PREFIX = FileUtil.PROJECT_DIR + "source/com/becker/simulation/";
    protected static final String ANIMATION_FRAME_FILE_NAME_PREFIX = FileUtil.PROJECT_DIR + "animations/simulation/";

    // debug level of 0 means no debug info, 3 is all debug info
    public static final int DEBUG_LEVEL = 0;


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
    }

    protected abstract double getInitialTimeStep();

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

    public void setScale( double scale ) {};
    public double getScale() {
        return 1;
    }

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
                //System.out.println( "options selected  canceled=" + canceled );
            }
        } );
        return button;
    }


    protected abstract SimulatorOptionsDialog createOptionsDialog();
    
    /**
     *return to the initial state.
     */
    protected abstract void reset();


    public JPanel createTopControls()
    {
        JPanel controls = new JPanel();
        controls.add( createStartButton() );
        
        controls.add( createResetButton() );

        controls.add( createOptionsButton() );
        //controls.add(simulator.getStepButton());
        return controls;
    }

    
    /**
     *
     * @return  a reset button that allows you to restore the initial condition of the simulation.
     */
    protected JButton createResetButton()
    {
        final JButton resetButton = new JButton( "Reset");
        resetButton.addActionListener( new ActionListener()  {
            public void actionPerformed( ActionEvent e )
            {    
                reset();
            }
        });
        return resetButton;
    }
    
    
    /**
     * Override this to return ui elements that can be used to modify the simulation as it is running.
     */
    public JPanel createDynamicControls() {
        return null;
    }

    protected String getStatusMessage()
    {
        return "frames/second=" + Util.formatNumber( getFrameRate() );
    }


    // the next 2 methods implement the unused methods of the optimizee interface.
    // Simulators must implement evaluateFitness ///

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

    public double getOptimalFitness() {
        return 0;
    }


    public void doOptimization()
    {
       System.out.println("not implemented for this simulator");
    }

    public int getNumParameters() {
        return 0;
    }

    /**
     * *** implements the key method of the Optimizee interface
     *
     * evaluates the fitness.
     */
    public double evaluateFitness( ParameterArray params )
    {
        assert false : "not implemented yet";
        return 0.0;
    }
    
}
