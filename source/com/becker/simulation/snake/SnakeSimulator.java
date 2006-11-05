package com.becker.simulation.snake;

import com.becker.common.*;
import com.becker.optimization.*;
import com.becker.simulation.common.*;
import com.becker.ui.*;

import javax.vecmath.*;
import java.awt.*;

import static com.becker.simulation.snake.SnakeConstants.*;

public class SnakeSimulator extends NewtonianSimulator
{

    public static final String CONFIG_FILE = CONFIG_FILE_PATH_PREFIX + "snake/snakeGeomNormal.data";
    private static final String FILE_NAME_BASE = ANIMATION_FRAME_FILE_NAME_PREFIX + "snake/snakeFrame";

    /** the amount to advance the animation in time for each frame in seconds. */
    protected static final int NUM_STEPS_PER_FRAME = 200;

    private static final Parameter[] PARAMS = {
            new Parameter( WAVE_SPEED, 0.0001, 0.02, "wave speed" ),
            new Parameter( WAVE_AMPLITUDE, 0.001, 0.2, "wave amplitude" ),
            new Parameter( WAVE_PERIOD, 0.5, 9.0, "wave period" ),
    };
    private static final ParameterArray INITIAL_PARAMS = new ParameterArray( PARAMS);

    private Snake snake_ = null;


    // change in center of the snake between timesteps
    private Point2d oldCenter_, newCenter_;

    // the overall distance the the snake has travelled so far.
    private Vector2d distance_ = new Vector2d( 0.0, 0.0 );

    // magnitude of the snakes velocity vector
    private double velocity_ = 0;

    // initial time step
    protected static final double TIME_STEP = 0.4;

    // size of the background grid
    // note the ground should move, not the snake so that the snake always remains visible.
    private static final int XDIM = 12;
    private static final int YDIM = 10;
    private static final double CELL_SIZE = 80.0;
    private static final Color GRID_COLOR = new Color( 0, 0, 60, 100 );

    private Color gridColor_ = GRID_COLOR;

    public SnakeSimulator()
    {
        super("Snake");
        //final Snake snake = new Snake(CONFIG_FILE);
        final Snake snake = new Snake();
        commonInit( snake );
    }

    public SnakeSimulator( Snake snake )
    {
        super("Snake");
        commonInit( snake );
    }

    private void commonInit( Snake snake )
    {
        snake_ = snake;
        oldCenter_ = snake_.getCenter();
        setNumStepsPerFrame(NUM_STEPS_PER_FRAME);

        initCommonUI();
        this.setPreferredSize(new Dimension( (int) (CELL_SIZE * XDIM), (int) (CELL_SIZE * YDIM)) );
    }


    protected double getInitialTimeStep() {
        return TIME_STEP;
    }

    public void setScale( double scale ) {
        snake_.setScale(scale);
    }
    public double getScale() {
        return snake_.getScale();
    }

    public void setShowVelocityVectors( boolean show ) {
        snake_.setShowVelocityVectors(show);
    }
    public boolean getShowVelocityVectors() {
        return snake_.getShowVelocityVectors();
    }

    public void setShowForceVectors( boolean show ) {
        snake_.setShowForceVectors(show);
    }
    public boolean getShowForceVectors() {
        return snake_.getShowForceVectors();
    }

    public void setDrawMesh( boolean use ) {
        snake_.setDrawMesh(use);
    }
    public boolean getDrawMesh() {
        return snake_.getDrawMesh();
    }

    public void setStaticFriction( double staticFriction ) {
        snake_.setStaticFriction( staticFriction );
    }
    public double getStaticFriction() {
        return snake_.getStaticFriction();
    }

    public void setDynamicFriction( double dynamicFriction ) {
        snake_.setDynamicFriction(dynamicFriction);
    }
    public double getDynamicFriction() {
        return snake_.getDynamicFriction();
    }



    public void doOptimization()
    {
        Optimizer optimizer;
        if (GUIUtil.isStandAlone())
            optimizer = new Optimizer( this );
        else
            optimizer = new Optimizer( this, Util.PROJECT_DIR+"performance/snake/snake_optimization.txt" );

        setPaused(false);
        optimizer.doOptimization(  OptimizationType.GENETIC_SEARCH, INITIAL_PARAMS, 0.3);
    }

    public int getNumParameters() {
        return INITIAL_PARAMS.size();
    }


    public double timeStep()
    {
        if ( !isPaused() ) {
            timeStep_ = snake_.stepForward( timeStep_ );
        }
        return timeStep_;
    }

    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                useAntialiasing_ ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );

        newCenter_ = snake_.getCenter();
        Vector2d distanceDelta = new Vector2d( oldCenter_.x - newCenter_.x, 0/*oldCenter_.y - newCenter_.y*/ );
        velocity_ = distanceDelta.length() / (getNumStepsPerFrame() * timeStep_);
        distance_.add( distanceDelta );

        drawGridBackground(g2, gridColor_, CELL_SIZE, XDIM, YDIM, distance_);

        // draw the snake on the grid
        snake_.translate( distanceDelta );
        snake_.render( g2 );

        oldCenter_ = snake_.getCenter();
    }

    // api for setting snake params  /////////////////////////////////
    public Snake getSnake()
    {
        return snake_;
    }


    public void setGridColor( Color c )
    {
        gridColor_ = c;
    }

    public Color getGridColor()
    {
        return gridColor_;
    }

    protected String getFileNameBase()
    {
        return FILE_NAME_BASE;
    }
    //////////////////////////////////////////////////////////////////




    protected SimulatorOptionsDialog createOptionsDialog() {
         return new SnakeOptionsDialog( frame_, this );
    }

    protected String getStatusMessage()
    {
        return super.getStatusMessage() + "    velocity=" + Util.formatNumber( velocity_ );
    }


    /**
     * *** implements the key method of the Optimizee interface
     *
     * evaluates the snake's fitness.
     * The measure is purely based on its velocity.
     * If the snake becomes unstable, then 0.0 is returned.
     */
    public double evaluateFitness( ParameterArray params )
    {
        snake_.setWaveSpeed( params.get( 0 ).getValue() );
        snake_.setWaveAmplitude( params.get( 1 ).getValue() );
        snake_.setWavePeriod( params.get( 2 ).getValue() );

        boolean stable = true;
        boolean improved = true;
        double oldVelocity = 0.0;
        int ct = 0;

        while ( stable && improved ) {
            try {
                // let the snake ru nfor a while
                Thread.sleep( 1000 + (int) (3000 / (1.0 + 0.2 * ct)) );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //System.out.println("vel="+velocity_+ "  timestep="+timeStep_);
            improved = (velocity_ - oldVelocity) > 0.00001;

            oldVelocity = velocity_;
            ct++;
            stable = snake_.isStable();
        }
        if ( !stable )   {
            System.out.println( "SnakeSim unstable" );
            return 0.0;
        }
        else
            return oldVelocity;
    }


    // *************** main *****************************
    /*
    public static void main( String[] args )
    {

        Simulator simulator = new SnakeSimulator();
        JPanel animPanel = new AnimationPanel( simulator );

        animPanel.add( simulator.createTopControls(), BorderLayout.NORTH );

        frame_ = new JFrame( "Snake Simulator" );
        frame_.getContentPane().add( animPanel );
        frame_.setVisible( true );
    }
    */
}