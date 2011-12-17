/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.simulation.fluid.ui;

import com.becker.common.util.FileUtil;
import com.becker.optimization.Optimizer;
import com.becker.optimization.parameter.NumericParameterArray;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;
import com.becker.simulation.common.Profiler;
import com.becker.simulation.common.ui.Simulator;
import com.becker.simulation.common.ui.SimulatorOptionsDialog;
import com.becker.simulation.fluid.model.FluidEnvironment;
import com.becker.simulation.fluid.rendering.EnvironmentRenderer;
import com.becker.simulation.fluid.rendering.RenderingOptions;
import com.becker.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Simulate deep water.
 * Based on work by Jos Stam
 * http://www.dgp.toronto.edu/people/stam/reality/Research/pdf/GDC03.pdf
 *
 * TODO
 *  Have the grid resize as the panel resizes
 *  Fluid specific parameters
 *   - number of cells (x,y) - auto-calculate the scale size based on the window size.
 */
public class FluidSimulator extends Simulator {

    public static final String CONFIG_FILE = "com/becker/fluid/initialStateTest.data";
    private static final String FILE_NAME_BASE = ANIMATION_FRAME_FILE_NAME_PREFIX + "fluid/fluidFrame";

    FluidEnvironment environment_;
    EnvironmentRenderer envRenderer_;
    InteractionHandler handler_;
    FluidDynamicOptions fluidOptions_;

    public static final int DEFAULT_STEPS_PER_FRAME = 1;

    /** if true it will save all the animation steps to file */
    public static final boolean RECORD_ANIMATION = false;
    public static final double INITIAL_TIME_STEP = 0.03;  // initial time step

    private static final Color BG_COLOR = Color.white;
    private static final int NUM_OPT_PARAMS = 3;
    private RenderingOptions renderOptions;


    public FluidSimulator() {
        this(new FluidEnvironment(250, 200));
    }

    public FluidSimulator( FluidEnvironment environment ) {
        super("Fuild");
        environment_ = environment;
        renderOptions = new RenderingOptions();
        commonInit();
    }

    private void commonInit() {
        initCommonUI();
        envRenderer_ = new EnvironmentRenderer(environment_.getGrid(), renderOptions);
        int scale = (int) envRenderer_.getOptions().getScale();
        setPreferredSize(new Dimension( environment_.getWidth() * scale, environment_.getHeight() * scale));
        setNumStepsPerFrame(DEFAULT_STEPS_PER_FRAME);
        
        handler_ = new InteractionHandler(environment_.getGrid(), scale);
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
    public void setPaused( boolean bPaused ) {
        super.setPaused(bPaused);
        if (isPaused())   {
            Profiler.getInstance().print();
            Profiler.getInstance().resetAll();
        }
    }
    
    @Override
    protected void reset() {
        // remove the listeners in order to prevent a memory leak.
        this.removeMouseListener(handler_);      
        this.removeMouseMotionListener(handler_);
        environment_.reset();        
        commonInit();
    }

    @Override
    protected double getInitialTimeStep() {
        return INITIAL_TIME_STEP;
    }

    /**
     * @return  a new recommended time step change.
     */
    @Override
    public double timeStep() {
        if ( !isPaused() ) {
            timeStep_ = environment_.stepForward( timeStep_);
        }
        return timeStep_;
    }


    @Override
    public void setScale( double scale ) {
        envRenderer_.getOptions().setScale(scale);

    }
    @Override
    public double getScale() {
        return envRenderer_.getOptions().getScale();
    }

    public void setShowVelocityVectors( boolean show ) {
        envRenderer_.getOptions().setShowVelocities(show);
    }
    public boolean getShowVelocityVectors() {
        return envRenderer_.getOptions().getShowVelocities();
    }

    @Override
    public void doOptimization() {

        Optimizer optimizer;
        if (GUIUtil.isStandAlone())
            optimizer = new Optimizer( this );
        else
            optimizer = new Optimizer( this, FileUtil.PROJECT_HOME +"performance/fluid/fluid_optimization.txt" );
        Parameter[] params = new Parameter[3];
        ParameterArray paramArray = new NumericParameterArray( params );

        setPaused(false);
        optimizer.doOptimization(OptimizationStrategyType.GENETIC_SEARCH, paramArray, 0.3);
    }

    /**
     * part of the Optimizee interface
     * evaluates the fluid's fitness.
     */
    @Override
    public double evaluateFitness( ParameterArray params ) {

        assert false : "not implemented yet";
        return 0.0;
    }

    @Override
    public Color getBackground() {
        return BG_COLOR;
    }

    @Override
    public void paint( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        Profiler.getInstance().startRenderingTime();
        envRenderer_.render(g2);
        Profiler.getInstance().stopRenderingTime();
    }

    @Override
    protected String getFileNameBase() {
        return FILE_NAME_BASE;
    }

}