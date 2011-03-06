package com.becker.simulation.snake.rendering;

import com.becker.common.ColorMap;
import com.becker.simulation.snake.geometry.Edge;
import com.becker.simulation.snake.geometry.Particle;

import java.awt.*;

/**
 *  Draws a snake edge (line geometry). It is modeled as a spring to simulate muscles.
 *
 *  @author Barry Becker
 */
public final class EdgeRenderer {

    private static final double EDGE_SCALE = 30.0;

    /** show the edge different colors depending on percentage stretched  ( one being 100% stretched)  */
    private static final double stretchVals_[] = {0.3, 0.9, 1.0, 1.1, 3.0};
    private static final Color stretchColors_[] = {
        new Color( 255, 0, 0, 200 ),
        new Color( 230, 120, 57, 250 ),
        new Color( 50, 90, 60, 250 ),
        new Color( 70, 120, 210, 200 ),
        new Color( 10, 10, 255, 100 )
    };
    private static final ColorMap stretchColorMap_ =
            new ColorMap( stretchVals_, stretchColors_ );

    private Graphics2D graphics;

    /**
     * Constructor
     */
    public EdgeRenderer( Graphics2D g) {
        this.graphics = g;
    }

    public void render(Edge edge) {
        //graphics.setColor(EDGE_COLOR);
        graphics.setColor( stretchColorMap_.getColorForValue( edge.getLength() / edge.getRestingLength() ) );

        double ratio = edge.getRestingLength() / edge.getLength();
        double width = EDGE_SCALE * Math.max(0, (ratio - 0.95));
        BasicStroke stroke =
                new BasicStroke( (float) width );
        graphics.setStroke( stroke );
        Particle part1 = edge.getFirstParticle();
        Particle part2 = edge.getSecondParticle();
        graphics.drawLine((int) part1.x, (int) part1.y,
                          (int) part2.x, (int) part2.y);
    }
}
