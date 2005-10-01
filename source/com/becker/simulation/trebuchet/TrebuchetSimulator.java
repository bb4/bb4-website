package com.becker.simulation.trebuchet;

import com.becker.common.*;
import com.becker.optimization.*;
import com.becker.simulation.common.*;
import com.becker.ui.*;

import java.awt.*;

/**
 * Physically base dynamic simulation of a trebuchet firing.
 *
 *  @@ Try simulating using Breve.
 *  Currently can't get working because of seg fault (because need to recompile for 64 bit?)
 */
public class TrebuchetSimulator extends Simulator implements Optimizee
{

    private Trebuchet trebuchet_ = null;

    public static final String CONFIG_FILE = "com/becker/trebuchet/trebuchetGeom.data";
    private static final String FILE_NAME_BASE = ANIMATION_FRAME_FILE_NAME_PREFIX + "trebuchet/trebuchetFrame";


    // the amount to advance the animation in time for each frame in seconds
    private static final double TIME_STEP = 0.4;

    private static final Color BACKGROUND_COLOR = new Color(253, 250, 253);



    public TrebuchetSimulator()
    {
        super("Trebuchet");
        //final trebuchet trebuchet = new trebuchet(CONFIG_FILE);
        final Trebuchet trebuchet = new Trebuchet();
        commonInit( trebuchet );
    }

    public TrebuchetSimulator( Trebuchet trebuchet )
    {
        super("Trebuchet");
        commonInit( trebuchet );
    }

    private void commonInit( Trebuchet trebuchet )
    {
        trebuchet_ = trebuchet;
        numStepsPerFrame_ = NUM_STEPS_PER_FRAME;
        initCommonUI();
        this.setPreferredSize(new Dimension( 800, 500));
        this.setBackground(BACKGROUND_COLOR);
    }

    public void doOptimization()
    {
        Optimizer optimizer;
        if (GUIUtil.isStandAlone())
            optimizer = new Optimizer( this );
        else
            optimizer = new Optimizer( this, Util.PROJECT_DIR+"performance/trebuchet/trebuchet_optimization.txt" );
        Parameter[] params = new Parameter[3];
        //params[0] = new Parameter( WAVE_SPEED, 0.0001, 0.02, "wave speed" );
        //params[1] = new Parameter( WAVE_AMPLITUDE, 0.001, 0.2, "wave amplitude" );
        //params[2] = new Parameter( WAVE_PERIOD, 0.5, 9.0, "wave period" );
        ParameterArray paramArray = new ParameterArray( params );

        setPaused(false);
        optimizer.doOptimization(  OptimizationType.GENETIC_SEARCH, paramArray, 0.3);
    }

    protected double getInitialTimeStep() {
        return TIME_STEP;
    }


    protected SimulatorOptionsDialog createOptionsDialog() {
         return new TrebuchetOptionsDialog( frame_, this );
    }


    public double timeStep()
    {
        if ( !isPaused() ) {
            timeStep_ = trebuchet_.stepForward( timeStep_ );
        }
        return timeStep_;
    }

    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                useAntialiasing_ ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );


        // draw the trebuchet on the gri
        trebuchet_.render( g2 );

    }



    public void setScale( double scale ) {
        //snake_.setScale(scale);
    }
    public double getScale() {
        //return snake_.getScale();
        return 1.0;
    }

    public void setShowVelocityVectors( boolean show ) {
        //snake_.setShowVelocityVectors(show);
    }
    public boolean getShowVelocityVectors() {
        //return snake_.getShowVelocityVectors();
        return false;
    }

    public void setShowForceVectors( boolean show ) {
        //snake_.setShowForceVectors(show);
    }
    public boolean getShowForceVectors() {
        //return snake_.getShowForceVectors();
        return false;
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


    // api for setting trebuchet params  /////////////////////////////////
    public Trebuchet getTrebuchet()
    {
        return trebuchet_;
    }


    protected String getFileNameBase()
    {
        return FILE_NAME_BASE;
    }



    /////////////// the next methods implement the Optimizee interface  /////////////////////////

    /**
     * evaluates the trebuchet's fitness.
     * The measure is purely based on its velocity.
     * If the trebuchet becomes unstable, then 0.0 is returned.
     */
    public double evaluateFitness( ParameterArray params )
    {
        //trebuchet_.setWaveSpeed( params.get( 0 ).value );
        //trebuchet_.setWaveAmplitude( params.get( 1 ).value );
        //trebuchet_.setWavePeriod( params.get( 2 ).value );

        boolean stable = true;
        boolean improved = true;
        double oldVelocity = 0.0;
        int ct = 0;

        while ( stable && improved ) {
            try {
                // let the trebuchet run for a while
                Thread.sleep( 1000 + (int) (3000 / (1.0 + 0.2 * ct)) );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ct++;
            //stable = trebuchet_.isStable();
        }
        if ( !stable )   {
            System.out.println( "Trebuchet Sim unstable" );
            return 0.0;
        }
        else
            return oldVelocity;
    }


}