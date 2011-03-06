package com.becker.simulation.snake.geometry;

import com.becker.simulation.snake.Snake;

import javax.vecmath.Vector2d;

import static com.becker.simulation.snake.SnakeConstants.SCALE;

/**
 *  The tail segment of a snakes body.
 *  @author Barry Becker
 */
public class TailSegment extends Segment {

    /**
     * constructor for all segments but the nose
     * @param width1 the width of the segment that is nearest the nose
     * @param width2 the width of the segment nearest the tail
     * @param segmentInFront the segment in front of this one
     */
    public TailSegment(double width1, double width2, double length, Segment segmentInFront,
                       int segmentIndex, Snake snake) {
        super(width1, width2, length, segmentInFront, segmentIndex, snake);
    }

    /**
     * update particle forces
     * look at how much the springs are deflected to determine how much force to apply
     * to each particle. Also include the frictional forces
     */
    @Override
    public void updateForces() {
        super.updateForces();

        Vector2d e0Force = edges_[0].getForce();
        Vector2d e2Force = edges_[2].getForce();
        Vector2d e3Force = edges_[3].getForce();
        Vector2d e4Force = edges_[4].getForce();
        Vector2d e7Force = edges_[7].getForce();

        // update back 2 particle forces if at tail
        particles_[0].force.set( 0, 0 );
        particles_[0].force.sub( e3Force );
        particles_[0].force.sub( e0Force );
        particles_[0].force.sub( e4Force );

        particles_[3].force.set( 0, 0 );
        particles_[3].force.add( e3Force );
        particles_[3].force.sub( e7Force );
        particles_[3].force.add( e2Force );
    }
}