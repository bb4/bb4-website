package com.becker.simulation.fluid;

import com.becker.common.util.FileUtil;
import com.becker.optimization.Optimizer;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;
import com.becker.simulation.common.NewtonianSimulator;
import com.becker.simulation.common.SimulatorOptionsDialog;
import com.becker.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 *Simulate deep water.
 *Based on work by Jos Stam
 *http://www.dgp.toronto.edu/people/stam/reality/Research/pdf/GDC03.pdf
 *
 *
 * TODO
 *  change meaning of force vectors checkbox
 *  Should not need to check the show force vector check to see things.
 *  Have the grid resize as the panel resizes
 *  Liquid specific parameters 
 *   - number of cells (x,y) - autocalculate the scale size based on the window size.
 *   - diffusion
 *   - viscosity
 *   - force factor
 *   - source_ink factor
 */
public class FluidSimulator extends NewtonianSimulator 
{

    public static final String CONFIG_FILE = "com/becker/fluid/initialStateTest.data";
    private static final String FILE_NAME_BASE = ANIMATION_FRAME_FILE_NAME_PREFIX + "fluid/fluidFrame";

    FluidEnvironment environment_;
    EnvironmentRenderer envRenderer_;
    InteractionHandler handler_;
    FluidDynamicOptions fluidOptions_;

    // if true it will save all the animation steps to files
    public static final boolean RECORD_ANIMATION = false;
    protected static final double TIME_STEP = 0.03;  // initial time step
    protected static final int DEFAULT_STEPS_PER_FRAME = 1;
    private static final Color BG_COLOR = Color.white;
    private static final int NUM_OPT_PARAMS = 3;


    public FluidSimulator() {
        super("Liquid");
        environment_ =  new FluidEnvironment( 110, 140 );
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
        int s = (int) envRenderer_.getScale();
        setPreferredSize(new Dimension( environment_.getWidth() * s, environment_.getHeight() * s));
        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);
        
        handler_ = new InteractionHandler(environment_.getGrid(), envRenderer_.getScale());
        this.addMouseListener(handler_);
        this.addMouseMotionListener(handler_);
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
         return new FluidOptionsDialog( frame_, this );
    }    
    
    @Override
    public JPanel createDynamicControls() {
        fluidOptions_ = new FluidDynamicOptions(this);
        return fluidOptions_;
    }

    
    public EnvironmentRenderer getRenderer() {
            return envRenderer_;
    }
    
    public FluidEnvironment getEnvironment() {
        return environment_;
    }
    
    public InteractionHandler getInteractionHandler() {
        return handler_;
    }
    
    @Override
    protected void reset() {
        // remove the listeners in order to prevent a memory leak.
        this.removeMouseListener(handler_);      
        this.removeMouseListener(handler_);
        environment_.reset();        
        commonInit();
    }

    @Override
    protected double getInitialTimeStep() {
        return TIME_STEP;
    }


    /**
     * @return  a new recommended time step change.
     */
    @Override
    public double timeStep()
    {
        if ( !isPaused() ) {
            timeStep_ = environment_.stepForward( timeStep_);
        }
        return timeStep_;
    }


    @Override
    public void setScale( double scale ) {
        envRenderer_.setScale(scale);

    }
    @Override
    public double getScale() {
        return envRenderer_.getScale();
    }

    @Override
    public void setShowVelocityVectors( boolean show ) {
        envRenderer_.setShowVelocities(show);
    }
    @Override
    public boolean getShowVelocityVectors() {
        return envRenderer_.getShowVelocities();
    }

    @Override
    public void setShowForceVectors( boolean show ) {
    }
    
    @Override
    public boolean getShowForceVectors() {
         return envRenderer_.getShowPressures();
    }

    @Override
    public void setDrawMesh( boolean use ) {
    }
    
    @Override
    public boolean getDrawMesh() {
        return false;
    }


    @Override
    public void setStaticFriction( double staticFriction ) {
        // do nothing
    }
    @Override
    public double getStaticFriction() {
        // do nothing
        return 0.1;
    }

    @Override
    public void setDynamicFriction( double dynamicFriction ) {
       // do nothing
    }
    @Override
    public double getDynamicFriction() {
        // do nothing
        return 0.01;
    }

    @Override
    public void doOptimization()
    {
        Optimizer optimizer;
        if (GUIUtil.isStandAlone())
            optimizer = new Optimizer( this );
        else
            optimizer = new Optimizer( this, FileUtil.PROJECT_HOME +"performance/liquid/liquid_optimization.txt" );
        Parameter[] params = new Parameter[3];
        //params[0] = new Parameter( WAVE_SPEED, 0.0001, 0.02, "wave speed" );
        //params[1] = new Parameter( WAVE_AMPLITUDE, 0.001, 0.2, "wave amplitude" );
        //params[2] = new Parameter( WAVE_PERIOD, 0.5, 9.0, "wave period" );
        ParameterArray paramArray = new ParameterArray( params );

        setPaused(false);
        optimizer.doOptimization(OptimizationStrategyType.GENETIC_SEARCH, paramArray, 0.3);
    }

    @Override
    public int getNumParameters() {
        return NUM_OPT_PARAMS;
    }


    /**
     * *** implements the key method of the Optimizee interface
     *
     * evaluates the liquids fitness.
     */
    @Override
    public double evaluateFitness( ParameterArray params )
    {
        assert false : "not implemented yet";
        return 0.0;
    }

    @Override
    public double getOptimalFitness() {
        return 0;
    }

    @Override
    public Color getBackground()
    {
        return BG_COLOR;
    }

    @Override
    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;
        envRenderer_.render(environment_, g2 );
    }

    @Override
    protected String getFileNameBase()
    {
        return FILE_NAME_BASE;
    }

}