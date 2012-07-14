/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.snake;

import com.barrybecker4.simulation.snake.data.ISnakeData;
import com.barrybecker4.simulation.snake.geometry.HeadSegment;
import com.barrybecker4.simulation.snake.geometry.Segment;
import com.barrybecker4.simulation.snake.geometry.TailSegment;
import com.barrybecker4.simulation.snake.rendering.RenderingParameters;
import com.barrybecker4.simulation.snake.rendering.SegmentRenderer;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.*;

/**
 *  Data structure and methods for representing a single dynamic snake
 *  The geometry of the snake is defined by ISnakeData
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
 *  3/2011 frames per second for basic snake = 64.5
 *         Speed = 0.32
 *
 *  @author Barry Becker
 */
public class Snake {

    /** defines the snake geometry */
    private ISnakeData snakeData_;

    /** the array of segments which make up the snake */
    private Segment[] segment_ = null;

    /** the time since the start of the simulation */
    private double time_ = 0.0;

    private LocomotionParameters locomotionParams = new LocomotionParameters();
    private RenderingParameters renderingParams = new RenderingParameters();

    /**
     * Constructor
     * use a hardcoded static data interface to initialize
     * so it can be easily run in an applet without using resources.
     */
    public Snake(ISnakeData snakeData) {
        setData(snakeData);
    }

    /**
     * @param snakeData the data defining the snakes geometrical shape.
     */
    public void setData(ISnakeData snakeData) {
        snakeData_ = snakeData;
        initFromData();
    }

    public void reset() {
        resetFromData();
    }

    /** use this if you need to avoid reading from a file */
    private void initFromData()  {

        segment_ = new Segment[snakeData_.getNumSegments()];
        resetFromData();
    }

    private void resetFromData() {

        double width1 = snakeData_.getWidths()[0];
        double width2 = snakeData_.getWidths()[1];
        int numSegments = snakeData_.getNumSegments();
        double segmentLength = snakeData_.getSegmentLength();

        double length = 80 + numSegments * segmentLength * renderingParams.getScale();
        Segment segment = new HeadSegment(width1, width2, segmentLength, length, 320.0, 0, this);
        segment_[0] = segment;
        Segment segmentInFront = segment;
        width1 = width2;

        for ( int i = 1; i < numSegments-1; i++ ) {
            width2 = snakeData_.getWidths()[i];

            segment = new Segment( width1, width2, segmentLength, segmentInFront, i, this );
            segment_[i] = segment;
            segmentInFront = segment;
            width1 = width2;
        }
        segment_[numSegments -1] = new TailSegment(width1, width2, segmentLength, segmentInFront, numSegments-1, this );
    }

    /**
     * steps the simulation forward in time
     * if the timestep is too big inaccuracy and instability will result.
     * @return the new timestep
     */
    public double stepForward( double timeStep )  {

        updateParticleForces();
        if ( locomotionParams.getUseFriction() ) {
            updateFrictionalForces();
        }
        updateParticleAccelerations();
        boolean unstable = updateParticleVelocities( timeStep );
        updateParticlePositions( timeStep );

        time_ += timeStep;
        if ( unstable ) {
            return timeStep / 2;
        }
        else {
            return 1.0 * timeStep;
        }
    }

    /**
     * @return the center point of the snake
     */
    public Point2d getCenter() {
        Point2d center = new Point2d( 0.0, 0.0 );
        int ct = 0;
        for ( int i = 0; i < snakeData_.getNumSegments(); i += 2 ) {
            ct++;
            center.add( segment_[i].getCenterParticle() );
        }
        center.scale( 1.0 / (double) ct );
        return center;
    }

    /**
     * shift/translate the whole snake by the specified vector
     */
    public void translate( Vector2d vec ) {
        for ( int i = 0; i < snakeData_.getNumSegments(); i++ ) {
            segment_[i].translate( vec );
        }
    }

    /**
     * Parameters controlling the movement (i.e. locomotion)
     */
    public LocomotionParameters getLocomotionParams() {
        return locomotionParams;
    }

    /**
     *
     * @return params for controlling different rendering options.
     */
    public RenderingParameters getRenderingParams() {
        return renderingParams;
    }

    /**
     * the snake is not considered stable if the angle between any edge segments exceeds Snake.MIN_EDGE_ANGLE
     * @return true if the snake has not gotten twisted too badly
     */
    public boolean isStable() {
        for ( int i = 2; i < snakeData_.getNumSegments(); i++ )
            if ( !segment_[i].isStable() )
                return false;
        return true;
    }

    ///////////////////////////////////////////////////

    /**
     * update forces
     */
    private void updateParticleForces() {

        // apply the sinusoidal muscular contraction function to the
        // left and right sides of the snake
        for ( int i = 2; i <snakeData_.getNumSegments(); i++ )
            segment_[i].contractMuscles(locomotionParams, time_);

        // update forces based on surrounding contracted springs
        for ( int i = 0; i < snakeData_.getNumSegments(); i++ )
            segment_[i].updateForces();
    }

    /**
     * update accelerations
     */
    private void updateFrictionalForces() {
        for ( int i = 0; i < snakeData_.getNumSegments(); i++ )
            segment_[i].updateFrictionalForce();
    }

    /**
     * update accelerations
     */
    private void updateParticleAccelerations() {
        for ( int i = 0; i < snakeData_.getNumSegments(); i++ )
            segment_[i].updateAccelerations();
    }

    /**
     * update velocities
     * @return unstable if velocity changes are getting too big
     */
    private boolean updateParticleVelocities( double timeStep ) {
        boolean unstable = false;
        for ( int i = 0; i < snakeData_.getNumSegments(); i++ )
            if ( segment_[i].updateVelocities( timeStep ) )
                unstable = true;
        return unstable;
    }

    /**
     * move particles according to vector field
     */
    private void updateParticlePositions( double timeStep ) {
        for ( int i = 0; i < snakeData_.getNumSegments(); i++ )
            segment_[i].updatePositions( timeStep );
    }

    /**
     * Render the Environment on the screen
     */
    public void render( Graphics2D g ) {

        int i;
        g.setColor( Color.black );

        SegmentRenderer segmentRenderer = new SegmentRenderer(g);

        // render each segment of the snake
        for ( i = 0; i < snakeData_.getNumSegments(); i++ )
            segmentRenderer.render(segment_[i]);
    }
}
