package com.becker.simulation.fluid;

import com.becker.common.*;
import com.becker.common.util.FileUtil;
import com.becker.optimization.*;
import com.becker.simulation.common.*;
import com.becker.ui.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

/**
 *Simulate deep water.
 *Based on work by Jos Stam
 *http://www.dgp.toronto.edu/people/stam/reality/Research/pdf/GDC03.pdf
 *
 *
 *TODO
 *  change measning of foxce vectors checlbox
 *  Shouyld not need to check the show force vector check to see things.
 *  Have the grid resize as the panel resizes
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

    protected SimulatorOptionsDialog createOptionsDialog() {
         return new FluidOptionsDialog( frame_, this );
    }    
    
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
    
    protected void reset() {
        // remove the listeners in order to prevent a memory leak.
        this.removeMouseListener(handler_);      
        this.removeMouseListener(handler_);
        environment_.reset();        
        commonInit();
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
    }
    
    public boolean getShowForceVectors() {
         return envRenderer_.getShowPressures();
    }

    public void setDrawMesh( boolean use ) {
    }
    
    public boolean getDrawMesh() {
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
            optimizer = new Optimizer( this, FileUtil.PROJECT_DIR+"performance/liquid/liquid_optimization.txt" );
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