package com.becker.simulation.trebuchet;

import com.becker.ui.*;

import java.awt.*;

import static com.becker.simulation.trebuchet.TrebuchetConstants.*;

/**
 *  Data structure and methods for representing a single dynamic trebuchet (advanced form of a catapult)
 *  The geometry of the trebuchet is defined by constants in TebuchetConstants.

 *  Performance Improvements:
 *    - profile (where is the time spent? rendering or computation)
 *
 *  @author Barry Becker
 */
public class Trebuchet {

    protected static final Font BASE_FONT = new Font( "Sans-serif", Font.PLAIN, 12 );
    protected static final int LOG_LEVEL = 1;

    private static final double MASS_SCALE = 1.5;
    private static final double STATIC_FRICTION = 0.01;
    private static final double DYNAMIC_FRICTION = 0.01;
    protected static final double MIN_EDGE_ANGLE = 0.3;


    private double counterWeightLeverLength_ = DEFAULT_CW_LEVER_LENGTH;
    private double slingLeverLength_ = DEFAULT_SLING_LEVER_LENGTH;
    private double counterWeightMass_ = DEFAULT_COUNTER_WEIGHT_MASS;
    private double slingLength_ = DEFAULT_SLING_LENGTH;
    private double slingReleaseAngle_ = DEFAULT_SLING_RELEASE_ANGLE;

    // the parts
    private Base base_;
    private Lever lever_;
    private CounterWeight counterWeight_;
    private Sling sling_;
    private Projectile projectile_;


    protected static final int NUM_PARTS = 5;
    private RenderablePart[] part_;

    // the time since the start of the simulation
    private double time_ = 0.0;
    private static Log logger_ = null;

    // tweekable rendering parameters
    private boolean showVelocityVectors_ = false;
    private boolean showForceVectors_ = false;


    // scales the geometry of the trebuchet
    private double scale_ = SCALE;


    //Constructor
    // use a harcoded static data interface to initialize
    // so it can be easily run in an applet without using resources.
    public Trebuchet()
    {
        commonInit();
    }


    private void commonInit()
    {
        logger_ = new Log();

        part_ = new RenderablePart[NUM_PARTS];

        double angle = - Math.asin(HEIGHT / slingLeverLength_);
        RenderablePart.setAngle(angle);
        base_ = new Base();
        part_[0] = base_;
        lever_ = new Lever(counterWeightLeverLength_, slingLeverLength_);
        part_[1] = lever_;
        counterWeight_ = new CounterWeight(counterWeightLeverLength_, counterWeightMass_);
        part_[2] = counterWeight_;
        projectile_ = new Projectile(HEIGHT);
        sling_ = new Sling(slingLength_, slingLeverLength_, slingReleaseAngle_, projectile_);
        part_[3] = sling_;
        part_[4] = projectile_;
    }



    /**
     * steps the simulation forward in time
     * if the timestep is too big inaccuracy and instability will result.
     * @return the new timestep
     */
    public double stepForward( double timeStep )
    {
        // Compute u, v for all full cells
        //logger_.println(1, LOG_LEVEL, "stepForward: about to update the velocity field (timeStep="+timeStep+")");

        updateParticleForces( timeStep );
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



    // api for tweeking Trebuchet params ////////////////////////////////////////

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



    ///////////////////////////////////////////////////

    /**
     * update forces
     */
    private void updateParticleForces( double timeStep )
    {
        // update forces based on surrounding contracted springs
        //for ( int i = 0; i < parts_; i++ )
        //    part_[i].updateForces( timeStep );
    }

    /**
     * update accelerations
     */
    private void updateFrictionalForces( double timeStep )
    {
        //for ( int i = 0; i < numSegments_; i++ )
        //    segment_[i].updateFrictionalForce( timeStep );
    }

    /**
     * update accelerations
     */
    private void updateParticleAccelerations( double timeStep )
    {
        //for ( int i = 0; i < numSegments_; i++ )
        //    segment_[i].updateAccelerations( timeStep );
    }

    /**
     * update velocities
     * @return unstable if velocity changes are getting too big
     */
    private boolean updateParticleVelocities( double timeStep )
    {
        boolean unstable = false;
        //for ( int i = 0; i < numSegments_; i++ )
        //    if ( segment_[i].updateVelocities( timeStep ) )
        //        unstable = true;
        return unstable;
    }

    /**
     * move particles according to vector field
     */
    private void updateParticlePositions( double timeStep )
    {
        //for ( int i = 0; i < numSegments_; i++ )
        //    segment_[i].updatePositions( timeStep );
    }

    /**
     * Render the Environment on the screen
     */
    public void render( Graphics2D g )
    {
        //double time = System.currentTimeMillis();
        int i;

        g.setColor( Color.black ); // default

        // render each part
        for ( i = 0; i < NUM_PARTS; i++ ) {
            if (part_[i] != null)
                part_[i].render( g );
        }

    }
}
