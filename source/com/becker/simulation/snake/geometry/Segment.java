package com.becker.simulation.snake.geometry;

import com.becker.simulation.snake.LocomotionParameters;
import com.becker.simulation.snake.Snake;

import javax.vecmath.Vector2d;

import static com.becker.simulation.snake.SnakeConstants.*;

/**
 *  A segment of a snakes body. It is composed of edges and particles
 *  The structure of the segment looks like this:
 *  <pre>
 *    p3<-------e2<-------p2    left edge
 *    ^  \              / ^
 *    |    e7        e6   |
 *    |      \      /     |     all edges in the middle point to p4
 *    e3       \  /       e1
 *    ^        /p4\       ^
 *    |     /       \     |
 *    |   e4          e5  |
 *    | /               \ |
 *    p0------->e0------->p1     right edge
 *  </pre>
 *
 *  @author Barry Becker
 */
public class Segment {

    /** Edge angles are not allowed to become less than this - to prevent instability. */
    private  static final double MIN_EDGE_ANGLE = 0.3;

    /** number of particles per segment (2 of which are shared between segments)  */
    private static final int NUM_PARTICLES = 5;

    /** index of the center particle */
    protected static final int CENTER_INDEX = 4;

    private static final double EPS = 0.00001;
    private static final double MASS_SCALE = 1.0;

    protected double halfLength_ = 0;
    protected double length_ = 0;
    private int segmentIndex_ = 0;

    // keep pointers to the segments in front and in back
    protected Segment segmentInFront_ = null;
    protected Segment segmentInBack_ = null;

    protected Edge[] edges_ = null;
    protected Particle[] particles_ = null;

    /** the snake that this segment belongs to */
    private Snake snake_ = null;

    protected double particleMass_ = 0;

    /** The unit directional spinal vector */
    protected Vector2d direction_ = new Vector2d( 0, 0 );

    /** temporary vector to aid in calculations (saves creating a lot of new vector objects)  */
    private Vector2d vel_ = new Vector2d( 0, 0 );
    private Vector2d change_ = new Vector2d( 0, 0 );

    protected Segment() {}

    /**
     * constructor for all segments but the nose
     * @param width1 the width of the segment that is nearest the nose
     * @param width2 the width of the segment nearest the tail
     * @param segmentInFront the segment in front of this one
     */
    public Segment( double width1, double width2, double length, Segment segmentInFront,
                       int segmentIndex, Snake snake ) {
        Particle center = segmentInFront.getCenterParticle();
        length_ = length;
        halfLength_ = length_ / 2.0;
        commonInit(width1, width2, (center.x - segmentInFront.getHalfLength() - halfLength_),
                   center.y, segmentIndex, snake);

        segmentInFront_ = segmentInFront;
        segmentInFront.segmentInBack_ = this;

        // reused particles
        particles_[1] = segmentInFront.getBackRightParticle();
        particles_[2] = segmentInFront.getBackLeftParticle();

        initCommonEdges();
        edges_[1] = segmentInFront.getBackEdge();  // front
    }

    public Snake getSnake() {
        return snake_;
    }

    public Edge[] getEdges() {
        return edges_;
    }

    public Particle[] getParticles() {
        return particles_;
    }

    /**
     * Initialize the segment.
     */
    protected void commonInit(double width1, double width2, double xpos, double ypos, int segmentIndex, Snake snake) {
        segmentIndex_ = segmentIndex;
        snake_ = snake;
        particles_ = new Particle[5];
        edges_ = new Edge[8];

        double segmentMass_ = (width1 + width2) * halfLength_;
        particleMass_ = MASS_SCALE * segmentMass_ / 3;
        double scale = snake.getRenderingParams().getScale();

        particles_[0] = new Particle( xpos - halfLength_, ypos + scale * width2 / 2.0, particleMass_ );
        particles_[3] = new Particle( xpos - halfLength_, ypos - scale * width2 / 2.0, particleMass_ );
        particles_[CENTER_INDEX] = new Particle( xpos, ypos, particleMass_ );
    }

    protected void initCommonEdges()  {
        edges_[0] = new Edge( particles_[0], particles_[1] ); // bottom (left of snake)
        edges_[2] = new Edge( particles_[2], particles_[3] ); // top (right of snake)
        edges_[3] = new Edge( particles_[0], particles_[3] ); // back

        // inner diagonal edges
        edges_[4] = new Edge( particles_[0], particles_[CENTER_INDEX] );
        edges_[5] = new Edge( particles_[1], particles_[CENTER_INDEX] );
        edges_[6] = new Edge( particles_[2], particles_[CENTER_INDEX] );
        edges_[7] = new Edge( particles_[3], particles_[CENTER_INDEX] );
    }

    public boolean isHead() {
        return (segmentInFront_ == null);
    }

    public boolean isTail() {
        return (segmentInBack_ == null);
    }

    private Edge getBackEdge() {
        return edges_[3];
    }

    private Particle getBackRightParticle() {
        return particles_[0];
    }

    private Particle getBackLeftParticle()
    {
        return particles_[3];
    }

    private Edge getRightEdge() {
        return edges_[0];
    }

    private Edge getLeftEdge() {
        return edges_[2];
    }

    public Particle getCenterParticle() {
        return particles_[CENTER_INDEX];
    }

    private double getHalfLength() {
        return halfLength_;
    }

    protected Vector2d getRightForce() {
        return edges_[0].getForce();
    }

    protected Vector2d getLeftForce() {
        return edges_[2].getForce();
    }

    protected Vector2d getRightBackDiagForce()  {
        return edges_[4].getForce();
    }

    protected Vector2d getLeftBackDiagForce() {
        return edges_[7].getForce();
    }

    private Vector2d getSpinalDirection()  {
        if ( isTail() ) {
            direction_.set( segmentInFront_.getCenterParticle().x - particles_[CENTER_INDEX].x,
                    segmentInFront_.getCenterParticle().y - particles_[CENTER_INDEX].y );
        }
        else if ( isHead() ) {
            direction_.set( particles_[CENTER_INDEX].x - segmentInBack_.getCenterParticle().x,
                    particles_[CENTER_INDEX].y - segmentInBack_.getCenterParticle().y );
        }
        else {
            direction_.set( segmentInFront_.getCenterParticle().x - segmentInBack_.getCenterParticle().x,
                    segmentInFront_.getCenterParticle().y - segmentInBack_.getCenterParticle().y );
        }
        direction_.normalize();
        return direction_;
    }

    /**
     * Contract the muscles on the left and right of the segment.
     * Don't contract the nose because there are no muscles there
     */
    public void contractMuscles( LocomotionParameters params, double time)  {

        double waveSpeed = params.getWaveSpeed();
        double amplitude = params.getWaveAmplitude();
        double period = params.getWavePeriod();

        //Vector2d muscleForce = v;
        double theta = (double) segmentIndex_ / period - waveSpeed * time;
        double offset = 0;

        offset = params.getWaveType().calculateOffset(amplitude, theta);

        double contractionLeft = 1.0 + offset;
        double contractionRight = 1.0 - offset;
        if ( contractionRight < 0 ) {
            System.out.println( "Error contractionRight is less than 0 = " + contractionRight );
            contractionRight = 0.0;
        }

        edges_[0].setContraction( contractionLeft );
        edges_[2].setContraction( contractionRight );
    }

    /**
     * update particle forces
     * look at how much the springs are deflected to determine how much force to apply
     * to each particle. Also include the frictional forces
     */
    public void updateForces() {
        Vector2d e0Force = edges_[0].getForce();
        Vector2d e1Force = edges_[1].getForce();
        Vector2d e2Force = edges_[2].getForce();
        Vector2d e4Force = edges_[4].getForce();
        Vector2d e5Force = edges_[5].getForce();
        Vector2d e6Force = edges_[6].getForce();
        Vector2d e7Force = edges_[7].getForce();

        // update the front 3 particle forces
        particles_[1].force.set( 0, 0 );
        particles_[1].force.add( e0Force );
        particles_[1].force.sub( e5Force );
        particles_[1].force.sub( e1Force );

        particles_[2].force.set( 0, 0 );
        particles_[2].force.sub( e2Force );
        particles_[2].force.sub( e6Force );
        particles_[2].force.add( e1Force );

        particles_[CENTER_INDEX].force.set( 0, 0 );
        particles_[CENTER_INDEX].force.add( e4Force );
        particles_[CENTER_INDEX].force.add( e7Force );
        particles_[CENTER_INDEX].force.add( e5Force );
        particles_[CENTER_INDEX].force.add( e6Force );

        if ( !isHead() ) {
            particles_[1].force.sub( segmentInFront_.getRightForce() );
            particles_[1].force.sub( segmentInFront_.getRightBackDiagForce() );

            particles_[2].force.add( segmentInFront_.getLeftForce() );
            particles_[2].force.sub( segmentInFront_.getLeftBackDiagForce() );
        }
    }

    /**
     * update frictional forces acting on particles
     * The coefficient of static friction should be used until the particle
     * is in motion, then the coefficient of dynamic friction is use to determine the
     * frictional force acting in the direction opposite to the velocity.
     * Static friction acts opposite to the force, while dynamic friction is applied
     * opposite the velocity vector.
     */
    public void updateFrictionalForce()  {
        int i = CENTER_INDEX;
        vel_.set( particles_[i].force );
        double forceMag = vel_.length();
        // the frictional force is the weight of the segment (particle mass *3) * coefficient of friction
        double frictionalForce;
        LocomotionParameters params = snake_.getLocomotionParams();

        // take into account friction for the center particle
        change_.set( particles_[i].velocity );
        double velMag = change_.length();
        if ( velMag > EPS ) {
            change_.normalize();
            frictionalForce = -particles_[i].mass * params.getDynamicFriction();
            change_.scale( frictionalForce );

            // eliminate the frictional force in the spinal direction
            Vector2d spineDir = this.getSpinalDirection();
            double dot = spineDir.dot( change_ );
            if ( dot < 0 ) {
                // then the velocity vector is going at least partially backwards
                // remove the backwards component.
                vel_.set( spineDir );
                vel_.scale( dot );
                change_.sub( vel_ );
            }
        }
        else if ( velMag <= EPS && forceMag > EPS ) {
            change_.set( particles_[i].force );
            change_.normalize();
            frictionalForce = -particles_[i].mass * params.getStaticFriction();
            change_.scale( frictionalForce );
        }
        else {
            // velocity and force are both very neer 0, so make them both 0
            particles_[i].force.set( 0.0, 0.0 );
            particles_[i].velocity.set( 0.0, 0.0 );
            change_.set( 0.0, 0.0 );
        }

        particles_[i].frictionalForce.set( change_ );
    }

    /**
     * update accelerations of particles
     * recall that F=ma so we can get the acceleration
     * by dividing F by m
     */
    public void updateAccelerations() {

        for ( int i = 0; i < NUM_PARTICLES; i++ ) {
            if ( (i != 3 && i != 0) || isTail() ) {
                vel_.set( particles_[i].force );
                vel_.add( particles_[i].frictionalForce );
                vel_.scale( (1.0 / particles_[i].mass) );
                particles_[i].acceleration.set( vel_ );
            }
        }
    }

    /**
     * update velocities of particles by integrating the acceleration
     * recall that this is just  vel0 + acceleration * dt
     *
     * We must also update velocities based taking into account the friction
     * on the bottom of the snake. Only the center particle of the snake segment
     * is in contact with the ground.
     *
     * @return unstable if the velocities are getting to big. This is an indication that we should reduce the timestep.
     */
    public boolean updateVelocities( double timeStep ) {

        boolean unstable = false;
        for ( int i = 0; i < NUM_PARTICLES; i++ ) {
            if ( (i != 3 && i != 0) || isTail() ) {
                // the current velocity v0
                vel_.set( particles_[i].velocity );
                change_.set( particles_[i].acceleration );
                change_.scale( timeStep );

                if ( change_.length() > 100.0 ) {
                    //System.out.println("becoming unstable vel mag="+change_.length());
                    unstable = true;
                }
                vel_.add( change_ );
                particles_[i].velocity.set( vel_ );
                /*
                if (i==CENTER_INDEX) {
                    Vector2d spineDir = this.getSpinalDirection();
                    double dot = spineDir.dot(particles_[CENTER_INDEX].velocity);
                    if (dot < 0 ) {
                        // then the velocity vector is going at least partially backwards
                        // remove the backwards component.
                        change_.set(spineDir);
                        change_.scale(dot);
                        particles_[CENTER_INDEX].velocity.sub(change_);
                    }
                }
                */
            }
        }
        return unstable;
    }

    /**
     * move particles according to vector field by integrating the velocity
     * recall that this is just pos0 + velocity * dt
     * and pos = pos0 + velocity * dt + 1/2 acceleration * dt*dt
     * where dt = timeStep
     */
    public void updatePositions( double timeStep ) {

        for ( int i = 0; i < NUM_PARTICLES; i++ ) {
            if ( (i != 3 && i != 0) || isTail() ) {
                // the current velocity v0
                vel_.set( particles_[i].x, particles_[i].y );
                change_.set( particles_[i].velocity );
                change_.scale( timeStep );
                vel_.add( change_ );
                particles_[i].set( vel_.x, vel_.y );
            }
        }
    }

    public void translate( Vector2d vec ) {
        for ( int i = 0; i < NUM_PARTICLES; i++ ) {
            if ( (i != 3 && i != 0) || isTail() ) {
                vel_.set( particles_[i].x, particles_[i].y );
                vel_.add( vec );
                particles_[i].set( vel_.x, vel_.y );
            }
        }
    }

    /**
     *
     * @return true if either of the edge segments bends to much when compared to its nbr in the next segment
     */
    public boolean isStable() {

        double dot1 = edges_[0].dot( segmentInFront_.getRightEdge() );
        double dot2 = edges_[2].dot( segmentInFront_.getLeftEdge() );
        if ( dot1 < MIN_EDGE_ANGLE || dot2 < MIN_EDGE_ANGLE )   {
            System.out.println( "dot1="+dot1+" dot2="+dot2 );
            return false;
        }
        return true;
    }

    public String toString()  {
        StringBuilder str = new StringBuilder( "Segment particles:\n" );
        for ( int i = 0; i < 5; i++ )
            str.append( " p" + i + '=' + particles_[i] + " \n" );
        return str.toString();
    }
}