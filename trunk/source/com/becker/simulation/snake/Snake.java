package com.becker.simulation.snake;

import com.becker.ui.*;

import javax.vecmath.*;
import java.awt.*;
import java.io.*;

import static com.becker.simulation.snake.SnakeConstants.*;

/**
 *  Data structure and methods for representing a single dynamic snake
 *  The geometry of the snake is defined by a config file passed into the constructor
 *  General Improvements:
 *    - auto optimize with hill-climbing (let the snake learn how to move faster on its own)
 *    - add texture
 *    - add option for square wave force function
 *    - collision detection, walls, multiple snakes
 *    - goal directed path search
 *
 *  Performance Improvements:
 *    - profile (where is the time spent? rendering or computation)
 *    - only draw every nth frame
 *    - run OptimizeIt
 *
 *  @author Barry Becker
 */
public class Snake {

    protected static final Font BASE_FONT = new Font( "Sans-serif", Font.PLAIN, 12 );
    protected static final int LOG_LEVEL = 1;
    private static final boolean USE_FRICTION = true;

    private static final double MASS_SCALE = 1.5;
    private static final double SPRING_K = 0.6;
    private static final double SPRING_DAMPING = 1.2;
    private static final double STATIC_FRICTION = 0.01;
    private static final double DYNAMIC_FRICTION = 0.01;
    protected static final double MIN_EDGE_ANGLE = 0.3;

    protected static int waveType_ = SINE_WAVE;

    // the length of the snake
    private int numSegments_ = 0;
    private double segmentLength_;

    // the array of segments which make up the snake
    private Segment[] segment_ = null;

    // the time since the start of the simulation
    private double time_ = 0.0;
    private static Log logger_ = null;

    // tweekable rendering parameters
    private boolean showVelocityVectors_ = false;
    private boolean showForceVectors_ = false;
    private boolean drawMesh_ = false;
    private double scale_ = SCALE;
    private double staticFriction_ = STATIC_FRICTION;
    private double dynamicFriction_ = DYNAMIC_FRICTION;


    ///////// tweekable snake parameters tha define locamotion/////////////
    // the speed at which the muscular contraction wave travels down the body of the snake
    private double waveSpeed_ = WAVE_SPEED;
    // scale factor for the force function (must be greater than 0 and less than 1.0)
    private double waveAmplitude_ = WAVE_AMPLITUDE;
    // The period of the sinusoidal force finction
    private double wavePeriod_ = WAVE_PERIOD;
    // scales the overall mass of the snake up or down
    private double massScale_ = MASS_SCALE;
    // the stiffness of the springs that make up the snakes body
    private double springK_ = SPRING_K;
    // the amound of spring damping present in the springs
    // this corresponds to how quickly the amplitude of the spring goes to 0
    private double springDamping_ = SPRING_DAMPING;
    // scales the geometry of the snake

    //Constructor
    // use a harcoded static data interface to initialize
    // so it can be easily run in an applet without using resources.
    public Snake()
    {
        initFromData();
        commonInit();
    }

    //Constructor
    public Snake( String configFile )
    {
        readConfigFile( configFile );
        commonInit();
    }
        
    public synchronized void reset() {
        resetFromData();
    }

    // Constructor - not currently supported
    public Snake( int numSegments )
    {
        numSegments_ = numSegments;
        // createTestGeometry();
        commonInit();
    }

    private static void commonInit()
    {
        logger_ = new Log();
    }

    // use this if you need to avoid reading from a file
    private void initFromData()
    {

        numSegments_ = SnakeData.NUM_SEGMENTS;
        segmentLength_ = SnakeData.SEGMENT_LENGTH;
        segment_ = new Segment[numSegments_];

        resetFromData();
    }

    private void resetFromData()
    {

        double width1 = SnakeData.WIDTHS[0];
        double width2 = SnakeData.WIDTHS[1];

        Segment segment = new Segment( width1, width2, segmentLength_, 80 + numSegments_ * segmentLength_ * scale_, 320.0, 0, this );
        segment_[0] = segment; // nose
        Segment segmentInFront = segment;
        width1 = width2;

        for ( int i = 1; i < numSegments_; i++ ) {
            width2 = SnakeData.WIDTHS[i];

            segment = new Segment( width1, width2, segmentLength_, segmentInFront, i, this );
            segment_[i] = segment;
            segmentInFront = segment;

            //System.out.println("segment "+i+" = "+segment_[i]);
            width1 = width2;
        }
    }

    // read the snake geometry from a file
    private void readConfigFile( String configFile )
    {
        // Open a file of the given name.
        File file = new File( configFile );
        FileInputStream configStream = null;

        //byte wtIndex = 0;
        logger_.print("reading config file "+ configFile);

        try {
            configStream = new FileInputStream( file );
        } catch (FileNotFoundException e) {
            System.out.println( "file " + configFile + " not found" + e.getMessage());
        }
        InputStreamReader iStreamReader = new InputStreamReader( configStream );
        BufferedReader inData = new BufferedReader( iStreamReader );
        StreamTokenizer inStream = new StreamTokenizer( inData );
        inStream.commentChar( '#' );
        inStream.slashSlashComments( true );
        inStream.wordChars( '_', '_' + 1 );

        try {
            // the first entry in the file should be the number of segments
            inStream.nextToken();
            numSegments_ = (int) (inStream.nval);
            //System.out.println("num segments = "+numSegments_);

            inStream.nextToken();
            segmentLength_ = inStream.nval;
            System.out.println( "segment length = " + segmentLength_ );

            segment_ = new Segment[numSegments_];

            //The first width is the width of the nose
            inStream.nextToken();
            double width1 = inStream.nval;
            inStream.nextToken();
            double width2 = inStream.nval;

            Segment segment =
                    new Segment( width1, width2, segmentLength_, 80 + numSegments_ * segmentLength_ * scale_, 350.0, 0, this );
            segment_[0] = segment; // nose
            Segment segmentInFront = segment;
            width1 = width2;

            for ( int i = 1; i < numSegments_; i++ ) {
                inStream.nextToken();
                width2 = inStream.nval;

                segment = new Segment( width1, width2, segmentLength_, segmentInFront, i, this );
                segment_[i] = segment;
                segmentInFront = segment;

                //System.out.println("segment "+i+" = "+segment_[i]);
                width1 = width2;
            }
            iStreamReader.close();
        } catch (IOException e) {
            System.out.println( "error occurred while reading " + configFile +' '+ e.getMessage());
        }
    }

    /**
     * steps the simulation forward in time
     * if the timestep is too big inaccuracy and instability will result.
     * @return the new timestep
     */
    public synchronized double stepForward( double timeStep )
    {
        // Compute u, v for all full cells
        //logger_.println(1, LOG_LEVEL, "stepForward: about to update the velocity field (timeStep="+timeStep+")");

        updateParticleForces( timeStep );
        if ( USE_FRICTION )
            updateFrictionalForces( timeStep );
        updateParticleAccelerations( timeStep );
        boolean unstable = updateParticleVelocities( timeStep );
        updateParticlePositions( timeStep );

        time_ += timeStep;
        //logger_.println(1, LOG_LEVEL, "Time= "+time_);
        if ( unstable )
            return timeStep / 2;
        else
            return 1.0 * timeStep;
    }

    /**
     * returns the center opoint of the snake
     */
    public Point2d getCenter()
    {
        Point2d center = new Point2d( 0.0, 0.0 );
        int ct = 0;
        for ( int i = 0; i < numSegments_; i += 2 ) {
            ct++;
            center.add( segment_[i].getCenterParticle() );
        }
        center.scale( 1.0 / (double) ct );
        return center;
    }

    /**
     * shift/translate the whole snake by the specified vector
     */
    public void translate( Vector2d vec )
    {
        for ( int i = 0; i < numSegments_; i++ ) {
            segment_[i].translate( vec );
        }
    }

    // api for tweeking snake params ////////////////////////////////////////

    public void setScale( double scale )
    {
        scale_ = scale;
    }

    public double getScale()
    {
        return scale_;
    }

    public void setShowVelocityVectors( boolean show )
    {
        showVelocityVectors_ = show;
    }

    public boolean getShowVelocityVectors()
    {
        return showVelocityVectors_;
    }

    public void setShowForceVectors( boolean show )
    {
        showForceVectors_ = show;
    }

    public boolean getShowForceVectors()
    {
        return showForceVectors_;
    }

    public void setDrawMesh( boolean use )
    {
        drawMesh_ = use;
    }

    public boolean getDrawMesh()
    {
        return drawMesh_;
    }


    public void setStaticFriction( double staticFriction )
    {
        staticFriction_ = staticFriction;
    }

    public double getStaticFriction()
    {
        return staticFriction_;
    }

    public void setDynamicFriction( double dynamicFriction )
    {
        dynamicFriction_ = dynamicFriction;
    }

    public double getDynamicFriction()
    {
        return dynamicFriction_;
    }

    /**
     * If the wave speed changes even a little bit it can cause
     * the snake to become unstable because the forces applied
     * during muscle contraction will change discontinuously.
     * Therefore we need to reset the snake first.
     */
    public void setWaveSpeed( double waveSpeed )
    {
        waveSpeed_ = waveSpeed;
        resetFromData();
    }

    public double getWaveSpeed()
    {
        return waveSpeed_;
    }

    public void setWaveAmplitude( double waveAmplitude )
    {
        waveAmplitude_ = waveAmplitude;
    }

    public double getWaveAmplitude()
    {
        return waveAmplitude_;
    }

    public void setWavePeriod( double wavePeriod )
    {
        wavePeriod_ = wavePeriod;
    }

    public double getWavePeriod()
    {
        return wavePeriod_;
    }

    public void setMassScale( double massScale )
    {
        massScale_ = massScale;
    }

    public double getMassScale()
    {
        return massScale_;
    }

    public void setSpringK( double springK )
    {
        springK_ = springK;
    }

    public double getSpringK()
    {
        return springK_;
    }

    public void setSpringDamping( double springDamping )
    {
        springDamping_ = springDamping;
    }

    public double getSpringDamping()
    {
        return springDamping_;
    }

    public void setWaveType( int waveType )
    {
        waveType_ = waveType;
    }

    public int getWaveType()
    {
        return waveType_;
    }

    /**
     * the snake is not considered stable if the angle between any edge segments exceeds Snake.MIN_EDGE_ANGLE
     * @return true if the snake has not gotten twisted too badly
     */
    public boolean isStable()
    {
        for ( int i = 2; i < numSegments_; i++ )
            if ( !segment_[i].isStable() )
                return false;
        return true;
    }
    ///////////////////////////////////////////////////

    /**
     * update forces
     */
    private void updateParticleForces( double timeStep )
    {
        // apply the sinusoidal muscular contraction function to the
        // left and right sides of the snake
        for ( int i = 2; i < numSegments_; i++ )
            segment_[i].contractMuscles( waveAmplitude_, time_, waveSpeed_, wavePeriod_ );

        // update forces based on surrounding contracted springs
        for ( int i = 0; i < numSegments_; i++ )
            segment_[i].updateForces( timeStep );
    }

    /**
     * update accelerations
     */
    private void updateFrictionalForces( double timeStep )
    {
        for ( int i = 0; i < numSegments_; i++ )
            segment_[i].updateFrictionalForce( timeStep );
    }

    /**
     * update accelerations
     */
    private void updateParticleAccelerations( double timeStep )
    {
        for ( int i = 0; i < numSegments_; i++ )
            segment_[i].updateAccelerations( timeStep );
    }

    /**
     * update velocities
     * @return unstable if velocity changes are getting too big
     */
    private boolean updateParticleVelocities( double timeStep )
    {
        boolean unstable = false;
        for ( int i = 0; i < numSegments_; i++ )
            if ( segment_[i].updateVelocities( timeStep ) )
                unstable = true;
        return unstable;
    }

    /**
     * move particles according to vector field
     */
    private void updateParticlePositions( double timeStep )
    {
        for ( int i = 0; i < numSegments_; i++ )
            segment_[i].updatePositions( timeStep );
    }

    /**
     * Render the Environment on the screen
     */
    public void render( Graphics2D g )
    {
        //double time = System.currentTimeMillis();
        int i;

        g.setColor( Color.black ); // default

        // render each segment of the snake
        for ( i = 0; i < numSegments_; i++ )
            segment_[i].render( g );

        //double duration = (System.currentTimeMillis()-time)/100.0;
        //logger_.println(1, LOG_LEVEL, "time to render:  ("+duration+") ");
    }
}
