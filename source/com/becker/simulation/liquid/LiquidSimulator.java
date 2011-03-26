package com.becker.simulation.liquid;

import com.becker.common.ILog;
import com.becker.common.util.FileUtil;
import com.becker.optimization.Optimizer;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;
import com.becker.simulation.common.NewtonianSimulator;
import com.becker.simulation.common.SimulatorOptionsDialog;
import com.becker.simulation.liquid.config.ConfigurationEnum;
import com.becker.simulation.liquid.model.LiquidEnvironment;
import com.becker.simulation.liquid.rendering.EnvironmentRenderer;
import com.becker.ui.dialogs.OutputWindow;
import com.becker.ui.util.GUIUtil;
import com.becker.ui.util.Log;

import javax.swing.*;
import java.awt.*;

/**
 * Main class for particle liquid simulation.
 *
 * @author Barry Becker
 */
public class LiquidSimulator extends NewtonianSimulator {

    private static final String FILE_NAME_BASE =
            ANIMATION_FRAME_FILE_NAME_PREFIX + "liquid/liquidFrame";

    private LiquidEnvironment environment_;
    private EnvironmentRenderer envRenderer_;

    private LiquidDynamicOptions dynamicOptions_;

    /** The initial time step. It may adapt. */
    private static final double INITIAL_TIME_STEP = 0.005;

    private static final Color BG_COLOR = Color.white;

    private static final int NUM_OPT_PARAMS = 3;

    /**
     * Constructor
     */
    public LiquidSimulator() {
        super("Liquid");

        environment_ = new LiquidEnvironment( ConfigurationEnum.SPIGOT.getFileName(), createLogger() );
        commonInit();
    }

    public void loadEnvironment(String configFile) {
        environment_ = new LiquidEnvironment(configFile, createLogger());
        commonInit();
    }

    private ILog createLogger()  {
        ILog logger;
        logger = new Log( new OutputWindow( "Log", null ) );
        logger.setDestination(ILog.LOG_TO_WINDOW);
        return logger;
    }

    @Override
    protected void reset() {
        boolean oldPaused = this.isPaused();
        setPaused(true);
        environment_.reset();
        commonInit();
        setPaused(oldPaused);
    }

    private void commonInit() {
        initCommonUI();
        envRenderer_ = new EnvironmentRenderer(environment_);

        int s = (int) envRenderer_.getScale();
        setPreferredSize(new Dimension( environment_.getWidth() * s, environment_.getHeight() * s));
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
         return new LiquidOptionsDialog( frame_, this );
    }

    @Override
    protected double getInitialTimeStep() {
        return INITIAL_TIME_STEP;
    }

    /**
     * @return a new recommended time step change.
     */
    @Override
    public double timeStep() {

        if ( !isPaused() ) {
            timeStep_ = environment_.stepForward( timeStep_);
        }
        return timeStep_;
    }

    public LiquidEnvironment getEnvironment() {
        return environment_;
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
        envRenderer_.setShowPressures(show);
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
    public JPanel createDynamicControls() {
        dynamicOptions_ = new LiquidDynamicOptions(this);
        return dynamicOptions_;
    }

    @Override
    public void doOptimization() {

        Optimizer optimizer;
        if (GUIUtil.isStandAlone())
            optimizer = new Optimizer( this );
        else
            optimizer = new Optimizer( this, FileUtil.PROJECT_HOME +"performance/liquid/liquid_optimization.txt" );
        Parameter[] params = new Parameter[3];
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
    public Color getBackground()  {
        return BG_COLOR;
    }

    @Override
    public void paint( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        envRenderer_.render(g2 );
    }

    @Override
    protected String getFileNameBase() {
        return FILE_NAME_BASE;
    }
}