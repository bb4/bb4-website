package com.becker.snake;

import com.becker.optimization.*;
import com.becker.ui.*;
import com.becker.common.Util;

import javax.swing.*;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.*;
import java.awt.event.*;

public class SnakeSimulator extends AnimationComponent implements Optimizee
{

    public static final String CONFIG_FILE = Util.PROJECT_DIR + "source/com/becker/snake/snakeGeomNormal.data";

    private static final String FILE_NAME_BASE = "d:/becker/documents/animations/snake/snakeFrame";
    private Snake snake_ = null;

    // Tweakable variables
    private boolean bPaused_ = true;

    // the amount to advance the animation in time for each frame in seconds
    private static final double TIME_STEP = .2;
    private static final int NUM_STEPS_PER_FRAME = 200;

    // change in center of the snake between timesteps
    private Point2d oldCenter_, newCenter_;

    // the overall distance the the snake has travelled so far.
    private Vector2d distance_ = new Vector2d( 0., 0. );

    //magnitude of the snakes velocity vector
    private double velocity_ = 0;

    // size of the background grid
    // note the ground should move, not the snake so that the snake always remains visible.
    private static final int XDIM = 12;
    private static final int YDIM = 10;
    private static final double CELL_SIZE = 80.0;
    private static final Color GRID_COLOR = new Color( 0, 0, 60, 100 );

    private SnakeOptionsDialog optionsDialog_ = null;
    private static JFrame frame_ = null;

    // rendering options
    private double timeStep_ = TIME_STEP;
    private boolean useAntialiasing_ = true;
    private Color gridColor_ = GRID_COLOR;

    public SnakeSimulator()
    {
        //final Snake snake = new Snake(CONFIG_FILE);
        final Snake snake = new Snake();
        commonInit( snake );
    }

    public SnakeSimulator( Snake snake )
    {
        commonInit( snake );
    }

    private void commonInit( Snake snake )
    {
        snake_ = snake;
        oldCenter_ = snake_.getCenter();
        numStepsPerFrame_ = NUM_STEPS_PER_FRAME;
        GUIUtil.setCustomLookAndFeel();

        addComponentListener( new ComponentAdapter()
        {
            public void componentResized( ComponentEvent ce )
            {
                //System.out.println("resized");
            }
        } );
        this.setPreferredSize(new Dimension( (int) (CELL_SIZE * XDIM), (int) (CELL_SIZE * YDIM)) );
    }

    public void doOptimization()
    {
        Optimizer optimizer = null;
        if (GUIUtil.isStandAlone())
            optimizer = new Optimizer( this );
        else
            optimizer = new Optimizer( this, Util.PROJECT_DIR+"performance/snake/snake_optimization.txt" );
        Parameter[] params = new Parameter[3];
        params[0] = new Parameter( Snake.WAVE_SPEED, 0.0001, .02, "wave speed" );
        params[1] = new Parameter( Snake.WAVE_AMPLITUDE, .001, .2, "wave amplitude" );
        params[2] = new Parameter( Snake.WAVE_PERIOD, 0.5, 9.0, "wave period" );
        ParameterArray paramArray = new ParameterArray( params );

        bPaused_ = false;
        optimizer.doOptimization(  OptimizationType.GENETIC_SEARCH, paramArray, .3);
    }

    public double timeStep()
    {
        if ( !bPaused_ ) {
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
        Vector2d distanceDelta = new Vector2d( oldCenter_.x - newCenter_.x, oldCenter_.y - newCenter_.y );
        velocity_ = distanceDelta.length() / (numStepsPerFrame_ * timeStep_);
        distance_.add( distanceDelta );

        // draw the grid background
        g2.setColor( gridColor_ );
        int xMax = (int) (CELL_SIZE * XDIM) - 1;
        int yMax = (int) (CELL_SIZE * YDIM) - 1;
        int j;
        double pos = distance_.y % CELL_SIZE;
        for ( j = 0; j <= YDIM; j++ ) {
            int ht = (int) (pos + j * CELL_SIZE);
            g2.drawLine( 1, ht, xMax, ht );
        }
        pos = distance_.x % CELL_SIZE;
        for ( j = 0; j <= XDIM; j++ ) {
            int w = (int) (pos + j * CELL_SIZE);
            g2.drawLine( w, 1, w, yMax );
        }

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

    public void setTimeStep( double timeStep )
    {
        timeStep_ = timeStep;
    }

    public double getTimeStep()
    {
        return timeStep_;
    }

    public void setAntialiasing( boolean use )
    {
        useAntialiasing_ = use;
    }

    public boolean getAntialiasing()
    {
        return useAntialiasing_;
    }

    public void setGridColor( Color c )
    {
        gridColor_ = c;
    }

    public Color getGridColor()
    {
        return gridColor_;
    }

    // if paused is true the animation is stopped
    private void setPaused( boolean bPaused )
    {
        bPaused_ = bPaused;
    }

    private boolean getPaused()
    {
        return bPaused_;
    }

    protected String getFileNameBase()
    {
        return FILE_NAME_BASE;
    }
    //////////////////////////////////////////////////////////////////

    protected String getStatusMessage()
    {
        return "frames/second=" + Util.formatNumber( getFrameRate() ) + "    velocity=" + Util.formatNumber( velocity_ );
    }

    private GradientButton createOptionsButton()
    {
        GradientButton button = new GradientButton( "Options" );

        optionsDialog_ = new SnakeOptionsDialog( frame_, this );

        button.addActionListener( new ActionListener()
        {
            public void actionPerformed( final ActionEvent e )
            {
                //System.out.println("options button pressed");

                optionsDialog_.setLocationRelativeTo( (Component) e.getSource() );
                // pause the snake while the options are open
                final SnakeSimulator simulator = optionsDialog_.getSimulator();
                final boolean oldPauseVal = simulator.getPaused();
                simulator.setPaused( true );
                final boolean canceled = optionsDialog_.showDialog();
                simulator.setPaused( oldPauseVal );
                System.out.println( "options selected  canceled=" + canceled );
            }
        } );
        return button;
    }

    public void setSwitch( int item, boolean value )
    {
        System.out.println( "item=" + item + " value=" + value );
        switch (item) {
            case PAUSE:
                bPaused_ = value;
                break;
            default:
                break;
        }
    }

    public JPanel createTopControls()
    {
        JPanel controls = new JPanel();
        controls.add( this.createCheckbox( "Pause", PAUSE, true ) );

        controls.add( this.createOptionsButton() );
        //controls.add(simulator.getStepButton());
        return controls;
    }

    /////////////// the next 3 methods implement the Optimizee interface  /////////////////////////
    /**
     *
     * If true is returned then compareFitness will be used and evaluateFitness will not
     * otherwise the reverse will be true.
     * @return return true if we evaluate the fitness by comparison
     */
    public boolean  evaluateByComparison()
     {
         return false;
     }

    /**
     * evaluates the snake's fitness.
     * The measure is purely based on its velocity.
     * If the snake becomes unstable, then 0.0 is returned.
     */
    public double evaluateFitness( ParameterArray params )
    {
        snake_.setWaveSpeed( params.get( 0 ).value );
        snake_.setWaveAmplitude( params.get( 1 ).value );
        snake_.setWavePeriod( params.get( 2 ).value );

        boolean stable = true;
        boolean improved = true;
        double oldVelocity = 0.0;
        int ct = 0;

        while ( stable && improved ) {
            try {
                Thread.sleep( 1000 + (int) (3000 / (1.0 + .2 * ct)) );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //System.out.println("vel="+velocity_+ "  timestep="+timeStep_);
            improved = (velocity_ - oldVelocity) > .00001;

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

    public double compareFitness( ParameterArray params1, ParameterArray params2 )
    {
        return 0.0;
    }

    // *************** main *****************************
    public static void main( String[] args )
    {

        final SnakeSimulator simulator = new SnakeSimulator();
        JPanel animPanel = new AnimationPanel( simulator );

        animPanel.add( simulator.createTopControls(), BorderLayout.NORTH );

        frame_ = new JFrame( "Snake Simulator" );
        frame_.getContentPane().add( animPanel );
        frame_.setVisible( true );
    }
}