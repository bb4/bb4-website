package com.becker.simulation.snake;

import com.becker.common.util.FileUtil;
import com.becker.common.util.Util;
import com.becker.optimization.Optimizer;
import com.becker.optimization.parameter.DoubleParameter;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;
import com.becker.simulation.common.rendering.BackgroundGridRenderer;
import com.becker.simulation.common.ui.NewtonianSimulator;
import com.becker.simulation.common.ui.SimulatorOptionsDialog;
import com.becker.simulation.snake.data.ISnakeData;
import com.becker.simulation.snake.data.LongSnakeData;
import com.becker.ui.util.GUIUtil;

import javax.swing.*;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.*;

/**
 * Simulates the motion of a snake.
 */
public class SnakeSimulator extends NewtonianSimulator {

    /** the amount to advance the animation in time for each frame in seconds. */
    protected static final int NUM_STEPS_PER_FRAME = 200;

    private SnakeDynamicOptions dynamicOptions_;

    private static final Parameter[] PARAMS = {
            new DoubleParameter( LocomotionParameters.WAVE_SPEED, 0.0001, 0.02, "wave speed" ),
            new DoubleParameter( LocomotionParameters.WAVE_AMPLITUDE, 0.001, 0.2, "wave amplitude" ),
            new DoubleParameter( LocomotionParameters.WAVE_PERIOD, 0.5, 9.0, "wave period" ),
    };
    
    private static final ParameterArray INITIAL_PARAMS = new ParameterArray( PARAMS);

    private Snake snake_ = new Snake(new LongSnakeData());

    /** change in center of the snake between time steps */
    private Point2d oldCenter_;

    /** the overall distance the the snake has travelled so far. */
    private Vector2d distance_ = new Vector2d( 0.0, 0.0 );

    /** magnitude of the snakes velocity vector */
    private double velocity_ = 0;

    /** initial time step */
    protected static final double INITIAL_TIME_STEP = 0.2;

    // size of the background grid
    // note the ground should move, not the snake so that the snake always remains visible.
    private static final int XDIM = 12;
    private static final int YDIM = 10;
    private static final double CELL_SIZE = 80.0;
    private static final Color GRID_COLOR = new Color( 0, 0, 60, 100 );

    private Color gridColor_ = GRID_COLOR;


    public SnakeSimulator() {
        super("Snake");
        commonInit();
    }

    public SnakeSimulator( ISnakeData snakeData ) {
        super("Snake");
        setSnakeData(snakeData);
    }

    @Override
    protected void reset() {
        snake_.reset();
    }
    
    @Override
    protected double getInitialTimeStep() {
        return INITIAL_TIME_STEP;
    }

    public void setSnakeData(ISnakeData snakeData) {
        snake_.setData(snakeData);
        commonInit();
    }

    private void commonInit() {
        oldCenter_ = snake_.getCenter();
        setNumStepsPerFrame(NUM_STEPS_PER_FRAME);

        this.setPreferredSize(new Dimension( (int) (CELL_SIZE * XDIM), (int) (CELL_SIZE * YDIM)) );
        initCommonUI();
    }

    public LocomotionParameters getLocomotionParams() {
        return snake_.getLocomotionParams();
    }

    @Override
    public void setScale( double scale ) {
        snake_.getRenderingParams().setScale(scale);
    }

    @Override
    public double getScale() {
        return snake_.getRenderingParams().getScale();
    }

    @Override
    public void setShowVelocityVectors(boolean show) {
        snake_.getRenderingParams().setShowVelocityVectors(show);
    }
    @Override
    public boolean getShowVelocityVectors() {
        return snake_.getRenderingParams().getShowVelocityVectors();
    }

    @Override
    public void setShowForceVectors( boolean show ) {
        snake_.getRenderingParams().setShowForceVectors(show);
    }
    @Override
    public boolean getShowForceVectors() {
        return snake_.getRenderingParams().getShowForceVectors();
    }

    @Override
    public void setDrawMesh( boolean use ) {
        snake_.getRenderingParams().setDrawMesh(use);
    }
    @Override
    public boolean getDrawMesh() {
        return snake_.getRenderingParams().getDrawMesh();
    }

    @Override
    public void setStaticFriction( double staticFriction ) {
        snake_.getLocomotionParams().setStaticFriction( staticFriction );
    }
    @Override
    public double getStaticFriction() {
        return snake_.getLocomotionParams().getStaticFriction();
    }

    @Override
    public void setDynamicFriction( double dynamicFriction ) {
        snake_.getLocomotionParams().setDynamicFriction(dynamicFriction);
    }
    @Override
    public double getDynamicFriction() {
        return snake_.getLocomotionParams().getDynamicFriction();
    }

    public void setDirection(double direction) {
        snake_.getLocomotionParams().setDirection(direction);
    }

    @Override
    public JPanel createDynamicControls() {
        dynamicOptions_ = new SnakeDynamicOptions(this);
        return dynamicOptions_;
    }

    @Override
    public void doOptimization()  {
        Optimizer optimizer;
        if (GUIUtil.isStandAlone())
            optimizer = new Optimizer( this );
        else
            optimizer = new Optimizer( this, FileUtil.PROJECT_HOME +"performance/snake/snake_optimization.txt" );

        setPaused(false);
        optimizer.doOptimization(  OptimizationStrategyType.GENETIC_SEARCH, INITIAL_PARAMS, 0.3);
    }

    @Override
    public int getNumParameters() {
        return INITIAL_PARAMS.size();
    }

    @Override
    public double timeStep() {
        if ( !isPaused() ) {
            timeStep_ = snake_.stepForward( timeStep_ );
        }
        return timeStep_;
    }

    @Override
    public void paint( Graphics g )  {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                useAntialiasing_ ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );

        Point2d newCenter = snake_.getCenter();
        Vector2d distanceDelta = new Vector2d( oldCenter_.x - newCenter.x, oldCenter_.y - newCenter.y );
        velocity_ = distanceDelta.length() / (getNumStepsPerFrame() * timeStep_);
        distance_.add( distanceDelta );

        BackgroundGridRenderer bgRenderer = new BackgroundGridRenderer(gridColor_);
        bgRenderer.drawGridBackground(g2, CELL_SIZE, XDIM, YDIM, distance_);

        // draw the snake on the grid
        snake_.translate( distanceDelta );
        snake_.render( g2 );

        oldCenter_ = snake_.getCenter();
    }

    // api for setting snake params  /////////////////////////////////

    public Snake getSnake() {
        return snake_;
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
         return new SnakeOptionsDialog( frame_, this );
    }

    @Override
    protected String getStatusMessage() {
        return super.getStatusMessage() + "    velocity=" + Util.formatNumber( velocity_ );
    }

    /**
     * *** implements the key method of the Optimizee interface
     *
     * evaluates the snake's fitness.
     * The measure is purely based on its velocity.
     * If the snake becomes unstable, then 0.0 is returned.
     */
    @Override
    public double evaluateFitness( ParameterArray params ) {

        LocomotionParameters locoParams = snake_.getLocomotionParams();
        locoParams.setWaveSpeed( params.get( 0 ).getValue() );
        locoParams.setWaveAmplitude( params.get( 1 ).getValue() );
        locoParams.setWavePeriod( params.get( 2 ).getValue() );

        boolean stable = true;
        boolean improved = true;
        double oldVelocity = 0.0;
        int ct = 0;

        while ( stable && improved ) {
            try {
                // let the snake run for a while
                Thread.sleep( 1000 + (int) (3000 / (1.0 + 0.2 * ct)) );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            improved = (velocity_ - oldVelocity) > 0.00001;

            oldVelocity = velocity_;
            ct++;
            stable = snake_.isStable();
        }
        if ( !stable )   {
            System.out.println( "SnakeSim unstable" );
            return 0.0;
        }
        else {
            return oldVelocity;
        }
    }
}