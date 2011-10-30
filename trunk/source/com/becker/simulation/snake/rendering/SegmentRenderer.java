/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.simulation.snake.rendering;

import com.becker.simulation.snake.Snake;
import com.becker.simulation.snake.geometry.Edge;
import com.becker.simulation.snake.geometry.Particle;
import com.becker.simulation.snake.geometry.Segment;

import java.awt.*;

/**
 *  Render a segment of a snakes body. It is composed of edges.
 *
 *  @author Barry Becker
 */
public class SegmentRenderer {

    // rendering attributes
    private static final Color FORCE_COLOR = new Color( 230, 0, 20, 100 );
    private static final Color FRICTIONAL_FORCE_COLOR = new Color( 50, 10, 0, 200 );
    private static final Color VELOCITY_COLOR = new Color( 80, 100, 250, 100 );

    private static final double VECTOR_SIZE = 130.0;
    private static final BasicStroke VECTOR_STROKE = new BasicStroke( 1 );

    private Graphics2D g;

    /**
     * constructor for the head segment
     */
    public SegmentRenderer(Graphics2D g) {
        this.g = g;
    }

    /**
     * @param segment the segment to render.
     */
    public void render(Segment segment)  {

        EdgeRenderer edgeRenderer = new EdgeRenderer(g);
        Snake snake = segment.getSnake();
        Edge[] edges = segment.getEdges();
        Particle[] particles = segment.getParticles();
        RenderingParameters renderParams = snake.getRenderingParams();

        if (renderParams.getDrawMesh()) {
            for ( int i = 0; i < edges.length; i++ ) {
                if ( i != 3 ) edgeRenderer.render(edges[i] );
            }
        }
        else {
            edgeRenderer.render(edges[0]);
            edgeRenderer.render(edges[2]);
        }

        if ( segment.isHead() ) edgeRenderer.render(edges[1]);
        if ( segment.isTail() ) edgeRenderer.render(edges[3]);

        // draw the force and velocity vectors acting on each particle
        if (renderParams.getShowForceVectors()) {
            renderForceVectors(particles);
        }

        if (renderParams.getShowVelocityVectors()) {
            renderVelocityVectors(particles);
        }
    }

    private void renderForceVectors(Particle[] particles) {
        g.setStroke( VECTOR_STROKE );

        g.setColor( FORCE_COLOR );
        for (Particle particle : particles) {
            g.drawLine((int) particle.x, (int) particle.y,
                    (int) (particle.x + VECTOR_SIZE * particle.force.x),
                    (int) (particle.y + VECTOR_SIZE * particle.force.y));
        }

        g.setColor( FRICTIONAL_FORCE_COLOR );
        for (Particle particle : particles) {
            g.drawLine((int) particle.x, (int) particle.y,
                    (int) (particle.x + VECTOR_SIZE * particle.frictionalForce.x),
                    (int) (particle.y + VECTOR_SIZE * particle.frictionalForce.y));
        }
    }

    private void renderVelocityVectors(Particle[] particles) {
        g.setStroke( VECTOR_STROKE );

        g.setColor( VELOCITY_COLOR );
        for (Particle particle : particles) {
            g.drawLine((int) particle.x, (int) particle.y,
                    (int) (particle.x + VECTOR_SIZE * particle.velocity.x),
                    (int) (particle.y + VECTOR_SIZE * particle.velocity.y));
        }
    }
}