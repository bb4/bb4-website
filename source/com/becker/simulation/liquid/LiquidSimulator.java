package com.becker.simulation.liquid;

import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.parameter.Parameter;
import com.becker.common.util.FileUtil;
import com.becker.optimization.*;
import com.becker.simulation.common.*;
import com.becker.ui.*;

import java.awt.*;

/**
 * @author Barry Becker
 */
public class LiquidSimulator extends NewtonianSimulator
{

    public static final String CONFIG_FILE = "com/becker/liquid/configurations/initialStateTest.data";
    private static final String FILE_NAME_BASE =
            ANIMATION_FRAME_FILE_NAME_PREFIX + "liquid/liquidFrame";

    private LiquidEnvironment environment_;
    private EnvironmentRenderer envRenderer_;

    /** if true, it will save all the animation steps to files */
    public static final boolean RECORD_ANIMATION = false;

    /** The initial time step. It may adapt. */
    private static final double INITIAL_TIME_STEP = 0.002;

    private static final Color BG_COLOR = Color.white;

    private static final int NUM_OPT_PARAMS = 3;


    public LiquidSimulator() {
        super("Liquid");
        reset();
    }

    /**
     *
     * @param environment
     */
    public LiquidSimulator( LiquidEnvironment environment )
    {
        super("Liquid");
        environment_ = environment;
        commonInit();
    }

    protected void reset() {
        boolean oldPaused = this.isPaused();
        setPaused(true);
        environment_ =  new LiquidEnvironment( 20, 15 );
        commonInit();
        setPaused(oldPaused);
    }

    private void commonInit() {
        initCommonUI();
        envRenderer_ = new EnvironmentRenderer(environment_);

        int s = (int) envRenderer_.getScale();
        setPreferredSize(new Dimension( environment_.getWidth() * s, environment_.getHeight() * s));
    }

    protected SimulatorOptionsDialog createOptionsDialog() {
         return new LiquidOptionsDialog( frame_, this );
    }


    protected double getInitialTimeStep() {
        return INITIAL_TIME_STEP;
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


    @Override
    public void setScale( double scale ) {
        envRenderer_.setScale(scale);

    }
    @Override
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

    @Override
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

    @Override
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
        envRenderer_.render(g2 );
    }

    protected String getFileNameBase()
    {
        return FILE_NAME_BASE;
    }

}