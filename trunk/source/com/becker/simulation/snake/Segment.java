package com.becker.simulation.snake;

import javax.vecmath.Vector2d;
import java.awt.*;

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
public class Segment
{

    // number of edges per segment (1 of which is shared with next semgent)
    private static final int NUM_EDGES = 8;
    // number of particles per segment (2 of which are shared beteeen segments)
    private static final int NUM_PARTICLES = 5;

    // index of the center particle
    private static final int CENTER_INDEX = 4;

    // rendering attributes
    private static final Color FORCE_COLOR = new Color( 230, 0, 20, 100 );
    private static final Color FRICTIONAL_FORCE_COLOR = new Color( 50, 10, 0, 200 );
    private static final Color VELOCITY_COLOR = new Color( 80, 100, 250, 100 );

    private static final double VECTOR_SIZE = 130.0;
    //private static final BasicStroke EDGE_STROKE = new BasicStroke( 3 );
    private static final BasicStroke VECTOR_STROKE = new BasicStroke( 1 );

    private static final double EPS = 0.00001;
    private static final double MASS_SCALE = 1.0;

    private double halfLength_ = 0;
    private double length_ = 0;
    private int segmentIndex_ = 0;

    // keap pointers to the segments in front and in back
    private Segment segmentInFront_ = null;
    private Segment segmentInBack_ = null;

    private Edge[] edge_ = null;
    private Particle[] particle_ = null;

    // the snake that this segment belongs to
    private Snake snake_ = null;

    // the segment's mass will be distributed among the pargiticles
    // the center particle will get 1 shares(@@ this is an approximation that could be improved)
    private double segmentMass_ = 0;
    private double particleMass_ = 0;

    //The unit directional spinal vector
    private Vector2d direction_ = new Vector2d( 0, 0 );

    // temp vector to aid in calculations (saves creating a lot of new vector objects)
    private Vector2d vel_ = new Vector2d( 0, 0 );
    private Vector2d change_ = new Vector2d( 0, 0 );

    /**
     * constructor for the head segment
     * @param width1 the width of the segment that is nearest the nose
     * @param width2 the width of the segment nearest the tail
     * @param xpos position of the center of the segment
     * @param ypos
     */
    protected Segment( double width1, double width2, double length, double xpos, double ypos, int segmentIndex, Snake snake )
    {
        //length_ = Snake.SCALE * Math.max(width1, width2);
        length_ = length;
        halfLength_ = length_ / 2.0;
        commonInit( width1, width2, xpos, ypos, segmentIndex, snake );

        particle_[1] = new Particle( xpos + halfLength_, ypos + SCALE * width1 / 2.0, particleMass_ );
        particle_[2] = new Particle( xpos + halfLength_, ypos - SCALE * width1 / 2.0, particleMass_ );

        initCommonEdges();
        edge_[1] = new Edge( particle_[1], particle_[2] ); // front
    }

    /**
     * constructor for all segments but the nose
     * @param width1 the width of the segment that is nearest the nose
     * @param width2 the width of the segment nearest the tail
     * @param segmentInFront the segment in front of this one
     */
    protected Segment( double width1, double width2, double length, Segment segmentInFront, int segmentIndex, Snake snake )
    {
        Particle center = segmentInFront.getCenterParticle();
        //length_ = Snake.SCALE * Math.max(width1, width2);
        length_ = length;
        halfLength_ = length_ / 2.0;
        commonInit( width1, width2, (center.x - segmentInFront.getHalfLength() - halfLength_), center.y, segmentIndex, snake );

        segmentInFront_ = segmentInFront;
        segmentInFront.segmentInBack_ = this;

        // reused particles
        particle_[1] = segmentInFront.getBackRightParticle();
        particle_[2] = segmentInFront.getBackLeftParticle();

        initCommonEdges();
        edge_[1] = segmentInFront.getBackEdge();  // front
    }

    // returns the length of the segment
    private void commonInit( double width1, double width2, double xpos, double ypos, int segmentIndex, Snake snake )
    {
        segmentIndex_ = segmentIndex;
        snake_ = snake;
        particle_ = new Particle[5];
        edge_ = new Edge[8];

        segmentMass_ = (width1 + width2) * halfLength_;
        particleMass_ = MASS_SCALE * segmentMass_ / 3;

        particle_[0] = new Particle( xpos - halfLength_, ypos + SCALE * width2 / 2.0, particleMass_ );
        particle_[3] = new Particle( xpos - halfLength_, ypos - SCALE * width2 / 2.0, particleMass_ );
        particle_[CENTER_INDEX] = new Particle( xpos, ypos, particleMass_ );
    }

    private void initCommonEdges()
    {
        edge_[0] = new Edge( particle_[0], particle_[1] ); // bottom (left of snake)
        edge_[2] = new Edge( particle_[2], particle_[3] ); // top (right of snake)
        edge_[3] = new Edge( particle_[0], particle_[3] ); // back

        // inner diagonal edges
        edge_[4] = new Edge( particle_[0], particle_[CENTER_INDEX] );
        edge_[5] = new Edge( particle_[1], particle_[CENTER_INDEX] );
        edge_[6] = new Edge( particle_[2], particle_[CENTER_INDEX] );
        edge_[7] = new Edge( particle_[3], particle_[CENTER_INDEX] );
    }

    private Edge getBackEdge()
    {
        return edge_[3];
    }

    private Particle getBackRightParticle()
    {
        return particle_[0];
    }

    private Particle getBackLeftParticle()
    {
        return particle_[3];
    }

    private Edge getRightEdge()
    {
        return edge_[0];
    }

    private Edge getLeftEdge()
    {
        return edge_[2];
    }

    public Particle getCenterParticle()
    {
        return particle_[CENTER_INDEX];
    }

    private double getHalfLength()
    {
        return halfLength_;
    }

    private boolean isNose()
    {
        return (segmentInFront_ == null);
    }

    private boolean isTail()
    {
        return (segmentInBack_ == null);
    }

    private Vector2d getRightForce()
    {
        return edge_[0].getForce();
    }

    private Vector2d getLeftForce()
    {
        return edge_[2].getForce();
    }

    private Vector2d getRightBackDiagForce()
    {
        return edge_[4].getForce();
    }

    private Vector2d getLeftBackDiagForce()
    {
        return edge_[7].getForce();
    }

    private Vector2d getSpinalDirection()
    {
        if ( isTail() ) {
            direction_.set( segmentInFront_.getCenterParticle().x - particle_[CENTER_INDEX].x,
                    segmentInFront_.getCenterParticle().y - particle_[CENTER_INDEX].y );
        }
        else if ( isNose() ) {
            direction_.set( particle_[CENTER_INDEX].x - segmentInBack_.getCenterParticle().x,
                    particle_[CENTER_INDEX].y - segmentInBack_.getCenterParticle().y );
        }
        else {
            direction_.set( segmentInFront_.getCenterParticle().x - segmentInBack_.getCenterParticle().x,
                    segmentInFront_.getCenterParticle().y - segmentInBack_.getCenterParticle().y );
        }
        direction_.normalize();
        return direction_;
    }

    /**
     * contract the muscles on the left and right of the segment
     */
    public void contractMuscles( double amplitude, double time, double waveSpeed, double period )
    {
        // don't contract the nose because there are no muscles there
        if ( !isNose() ) {
            //Vector2d muscleForce = v;
            double theta = (double) segmentIndex_ / period - waveSpeed * time;
            double offset = 0;
            switch (Snake.waveType_) {
                case SINE_WAVE:
                    offset = amplitude * (Math.sin( theta ));
                    break;
                case SQUARE_WAVE:
                    offset = (Math.sin( theta ) > 0.0) ? amplitude : -amplitude;
                    break;
                default : assert false;
            }
            double contractionLeft = 1.0 + offset;
            double contractionRight = 1.0 - offset;
            if ( contractionRight < 0 ) {
                System.out.println( "Error contractionright is less than 0 = " + contractionRight );
                contractionRight = 0.0;
            }

            edge_[0].setContraction( contractionLeft );
            edge_[2].setContraction( contractionRight );
        }
    }

    /**
     * update particle forces
     * look at how much the springs are deflected to determine how much force to apply
     * to each particle. Also include the frictional forces
     */
    public void updateForces( double timeStep )
    {
        Vector2d e0Force = edge_[0].getForce();
        Vector2d e1Force = edge_[1].getForce();
        Vector2d e2Force = edge_[2].getForce();
        Vector2d e4Force = edge_[4].getForce();
        Vector2d e5Force = edge_[5].getForce();
        Vector2d e6Force = edge_[6].getForce();
        Vector2d e7Force = edge_[7].getForce();

        if ( isTail() ) {
            Vector2d e3Force = edge_[3].getForce();

            // update back 2 particle forces if at tail
            particle_[0].force.set( 0, 0 );
            particle_[0].force.sub( e3Force );
            particle_[0].force.sub( e0Force );
            particle_[0].force.sub( e4Force );

            particle_[3].force.set( 0, 0 );
            particle_[3].force.add( e3Force );
            particle_[3].force.sub( e7Force );
            particle_[3].force.add( e2Force );
        }

        // update the front 3 particle forces
        particle_[1].force.set( 0, 0 );
        particle_[1].force.add( e0Force );
        particle_[1].force.sub( e5Force );
        particle_[1].force.sub( e1Force );
        if ( !isNose() ) {
            particle_[1].force.sub( segmentInFront_.getRightForce() );
            particle_[1].force.sub( segmentInFront_.getRightBackDiagForce() );
        }
        //if (segmentIndex_==3)
        //    System.out.println("p1 (seg=3) force="+particle_[1].force);

        particle_[2].force.set( 0, 0 );
        particle_[2].force.sub( e2Force );
        particle_[2].force.sub( e6Force );
        particle_[2].force.add( e1Force );
        if ( !isNose() ) {
            particle_[2].force.add( segmentInFront_.getLeftForce() );
            particle_[2].force.sub( segmentInFront_.getLeftBackDiagForce() );
        }

        particle_[CENTER_INDEX].force.set( 0, 0 );
        particle_[CENTER_INDEX].force.add( e4Force );
        particle_[CENTER_INDEX].force.add( e7Force );
        particle_[CENTER_INDEX].force.add( e5Force );
        particle_[CENTER_INDEX].force.add( e6Force );
    }

    /**
     * update frictioanl forces acting on particles
     * The coefficient of static friction should be used until the particle
     * is in motion, then the coefficient of dynamic friction is use to determine the
     * frictional force acting in the direction oppposite to the velocity.
     * Static friction acts opposite to the force, while dynamic priction is applied
     * opposite the velocity vector.
     */
    public void updateFrictionalForce( double timeStep )
    {
        int i = CENTER_INDEX;
        vel_.set( particle_[i].force );
        double forceMag = vel_.length();
        // the frictional force is the weight of the segment (partical mass *3) * coefficient of friction
        double frictionalForce;

        // take into account friction for the center particle
        change_.set( particle_[i].velocity );
        double velMag = change_.length();
        if ( velMag > EPS ) {
            change_.normalize();
            frictionalForce = -particle_[i].mass * snake_.getDynamicFriction();
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
            change_.set( particle_[i].force );
            change_.normalize();
            frictionalForce = -particle_[i].mass * snake_.getStaticFriction();
            change_.scale( frictionalForce );
        }
        else {
            // velocity and force are both very neer 0, so make them both 0
            particle_[i].force.set( 0.0, 0.0 );
            particle_[i].velocity.set( 0.0, 0.0 );
            change_.set( 0.0, 0.0 );
        }

        particle_[i].frictionalForce.set( change_ );
        //System.out.println("force="+particle_[i].force+" friction="+particle_[i].frictionalForce+" velocity="+particle_[i].velocity);
    }

    /**
     * update accelerations of particles
     * recall that F=ma so we can get the acceleration
     * by dividing F by m
     */
    public void updateAccelerations( double timeStep )
    {
        for ( int i = 0; i < NUM_PARTICLES; i++ ) {
            if ( (i != 3 && i != 0) || isTail() ) {
                vel_.set( particle_[i].force );
                vel_.add( particle_[i].frictionalForce );
                vel_.scale( (1.0 / particle_[i].mass) );
                particle_[i].acceleration.set( vel_ );
            }
        }
    }

    /**
     * update velocities of particles by integrating the acceleration
     * recall that this is just  vel0 + aceleration * dt
     *
     * We must also update velocities based taking into account the friction
     * on the bottom of the snake. Only the center particle of the snake segment
     * is in contact with the ground.
     *
     * @return unstable if the velocities are getting to big. This is an indication that we should reduce the timestep.
     */
    public boolean updateVelocities( double timeStep )
    {
        boolean unstable = false;
        for ( int i = 0; i < NUM_PARTICLES; i++ ) {
            if ( (i != 3 && i != 0) || isTail() ) {
                // the current velocity v0
                vel_.set( particle_[i].velocity );
                change_.set( particle_[i].acceleration );
                change_.scale( timeStep );

                if ( change_.length() > 100.0 ) {
                    //System.out.println("becoming unstable vel mag="+change_.length());
                    unstable = true;
                }
                vel_.add( change_ );
                particle_[i].velocity.set( vel_ );
                /*
                if (i==CENTER_INDEX) {
                    Vector2d spineDir = this.getSpinalDirection();
                    double dot = spineDir.dot(particle_[CENTER_INDEX].velocity);
                    if (dot < 0 ) {
                        // then the velocity vector is going at least partially backwards
                        // remove the backwards component.
                        change_.set(spineDir);
                        change_.scale(dot);
                        particle_[CENTER_INDEX].velocity.sub(change_);
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
    public void updatePositions( double timeStep )
    {
        for ( int i = 0; i < NUM_PARTICLES; i++ ) {
            if ( (i != 3 && i != 0) || isTail() ) {
                // the current velocity v0
                vel_.set( particle_[i].x, particle_[i].y );
                change_.set( particle_[i].velocity );
                change_.scale( timeStep );
                vel_.add( change_ );
                particle_[i].set( vel_.x, vel_.y );
            }
        }
    }

    public void translate( Vector2d vec )
    {
        for ( int i = 0; i < NUM_PARTICLES; i++ ) {
            if ( (i != 3 && i != 0) || isTail() ) {
                vel_.set( particle_[i].x, particle_[i].y );
                vel_.add( vec );
                particle_[i].set( vel_.x, vel_.y );
            }
        }
    }

    /**
     *
     * @return true if either of the edge segments bends to much when compared to its nbr in the next segment
     */
    public boolean isStable()
    {
        double dot1 = edge_[0].dot( segmentInFront_.getRightEdge() );
        double dot2 = edge_[2].dot( segmentInFront_.getLeftEdge() );
        if ( dot1 < Snake.MIN_EDGE_ANGLE || dot2 < Snake.MIN_EDGE_ANGLE )   {
            System.out.println( "dot1="+dot1+" dot2="+dot2 );
            return false;
        }
        return true;
    }

    public void render( Graphics2D g )
    {
        ////g.setStroke(EDGE_STROKE);

        if ( snake_.getDrawMesh() ) {
            for ( int i = 0; i < NUM_EDGES; i++ ) {
                if ( i != 3 ) edge_[i].render( g );
            }
        }
        else {
            edge_[0].render( g );
            edge_[2].render( g );
        }

        if ( isNose() ) edge_[1].render( g );
        if ( isTail() ) edge_[3].render( g );

        // draw the force and velocity vectors acting on each particle
        if ( snake_.getShowForceVectors() ) {
            g.setStroke( VECTOR_STROKE );

            g.setColor( FORCE_COLOR );
            for ( int i = 0; i < NUM_PARTICLES; i++ ) {
                g.drawLine( (int) particle_[i].x, (int) particle_[i].y,
                        (int) (particle_[i].x + VECTOR_SIZE * particle_[i].force.x), (int) (particle_[i].y + VECTOR_SIZE * particle_[i].force.y) );
            }

            g.setColor( FRICTIONAL_FORCE_COLOR );
            for ( int i = 0; i < NUM_PARTICLES; i++ ) {
                g.drawLine( (int) particle_[i].x, (int) particle_[i].y,
                        (int) (particle_[i].x + VECTOR_SIZE * particle_[i].frictionalForce.x), (int) (particle_[i].y + VECTOR_SIZE * particle_[i].frictionalForce.y) );
            }
        }

        if ( snake_.getShowVelocityVectors() ) {
            g.setStroke( VECTOR_STROKE );

            g.setColor( VELOCITY_COLOR );
            for ( int i = 0; i < NUM_PARTICLES; i++ ) {
                g.drawLine( (int) particle_[i].x, (int) particle_[i].y,
                        (int) (particle_[i].x + VECTOR_SIZE * particle_[i].velocity.x), (int) (particle_[i].y + VECTOR_SIZE * particle_[i].velocity.y) );
            }
        }
    }

    public String toString()
    {
        StringBuffer str = new StringBuffer( "Segment particles:\n" );
        for ( int i = 0; i < 5; i++ )
            str.append( " p" + i + '=' + particle_[i] + " \n" );
        return str.toString();
    }

}