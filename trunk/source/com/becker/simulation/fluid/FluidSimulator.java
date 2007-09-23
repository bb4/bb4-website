package com.becker.simulation.fluid;

import com.becker.common.*;
import com.becker.optimization.*;
import com.becker.simulation.common.*;
import com.becker.ui.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 *Simulate deep water.
 *Based on work by Jos Stam
 *http://www.dgp.toronto.edu/people/stam/reality/Research/pdf/GDC03.pdf
 *
 *
 *TODO
 *  change measning of foxce vectors checlbox
 *  need reset button (for all simulations)
 *  Liquid specific parameters 
 *   - number of cells (x,y) - autocalculate the scale size based on the window size.
 *   - diffusion
 *   - viscosity
 *   - force factor
 *   - source_ink factor
 *   - 
 */
public class FluidSimulator extends NewtonianSimulator 
{

    public static final String CONFIG_FILE = "com/becker/liquid/initialStateTest.data";
    private static final String FILE_NAME_BASE = ANIMATION_FRAME_FILE_NAME_PREFIX + "liquid/liquidFrame";

    FluidEnvironment environment_;
    EnvironmentRenderer envRenderer_;

    // if true it will save all the animation steps to files
    public static final boolean RECORD_ANIMATION = false;
    protected static final double TIME_STEP = 0.05;  // initial time step
    protected static final int DEFAULT_STEPS_PER_FRAME = 2;
    private static final Color BG_COLOR = Color.white;
    private static final int NUM_OPT_PARAMS = 3;


    public FluidSimulator() {
        super("Liquid");
        environment_ =  new FluidEnvironment( 80, 80 );
        commonInit();
    }

    public FluidSimulator( FluidEnvironment environment )
    {
        super("Liquid");
        environment_ = environment;
        commonInit();
    }

    private void commonInit() {
        initCommonUI();
        envRenderer_ = new EnvironmentRenderer();
        System.out.println("environment_.getWidth() = "
                           +environment_.getWidth()+ " environment_.getHeight()="+environment_.getHeight());
        int s = (int) envRenderer_.getScale();
        setPreferredSize(new Dimension( environment_.getWidth() * s, environment_.getHeight() * s));
        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);
        
        InteractionHandler handler = new InteractionHandler(environment_.getGrid(), envRenderer_.getScale());
        this.addMouseListener(handler);
        this.addMouseMotionListener(handler);
    }

    protected SimulatorOptionsDialog createOptionsDialog() {
         return new FluidOptionsDialog( frame_, this );
    }

    protected double getInitialTimeStep() {
        return TIME_STEP;
    }


    /**
     * @return  a new recommended time step change.
     */
    public double timeStep()
    {
        if ( !isPaused() ) {
            timeStep_ = environment_.stepForward( timeStep_);
        }
        return timeStep_;
    }


    public void setScale( double scale ) {
        envRenderer_.setScale(scale);

    }
    public double getScale() {
        return envRenderer_.getScale();
    }

    public void setShowVelocityVectors( boolean show ) {
        envRenderer_.setShowVelocities(show);
    }
    public boolean getShowVelocityVectors() {
        return envRenderer_.getShowVelocities();
    }

    public void setShowForceVectors( boolean show ) {
        envRenderer_.setShowPressures(show);
    }
    public boolean getShowForceVectors() {
        return envRenderer_.getShowPressures();
    }

    public void setDrawMesh( boolean use ) {
        //snake_.setDrawMesh(use);
    }
    public boolean getDrawMesh() {
        //return snake_.getDrawMesh();
        return false;
    }


    public void setStaticFriction( double staticFriction ) {
        // do nothing
    }
    public double getStaticFriction() {
        // do nothing
        return 0.1;
    }

    public void setDynamicFriction( double dynamicFriction ) {
       // do nothing
    }
    public double getDynamicFriction() {
        // do nothing
        return 0.01;
    }

    public void doOptimization()
    {
        Optimizer optimizer;
        if (GUIUtil.isStandAlone())
            optimizer = new Optimizer( this );
        else
            optimizer = new Optimizer( this, Util.PROJECT_DIR+"performance/liquid/liquid_optimization.txt" );
        Parameter[] params = new Parameter[3];
        //params[0] = new Parameter( WAVE_SPEED, 0.0001, 0.02, "wave speed" );
        //params[1] = new Parameter( WAVE_AMPLITUDE, 0.001, 0.2, "wave amplitude" );
        //params[2] = new Parameter( WAVE_PERIOD, 0.5, 9.0, "wave period" );
        ParameterArray paramArray = new ParameterArray( params );

        setPaused(false);
        optimizer.doOptimization(OptimizationType.GENETIC_SEARCH, paramArray, 0.3);
    }

    public int getNumParameters() {
        return NUM_OPT_PARAMS;
    }


    /**
     * *** implements the key method of the Optimizee interface
     *
     * evaluates the liquids fitness.
     */
    public double evaluateFitness( ParameterArray params )
    {
        assert false : "not implemented yet";
        return 0.0;
    }

    public double getOptimalFitness() {
        return 0;
    }

    public Color getBackground()
    {
        return BG_COLOR;
    }

    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;
        envRenderer_.render(environment_, g2 );
    }

    protected String getFileNameBase()
    {
        return FILE_NAME_BASE;
    }

}