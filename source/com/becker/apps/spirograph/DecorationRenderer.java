package com.becker.apps.spirograph;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Draws the Axes, circles, and spoke.
 * The circles and spoke represent the plastic stylus tool of the old spirograph.
 * @author Barry Becker
 */
public class DecorationRenderer
{
    private static final Color AXES_COLOR = new Color(120, 120, 200);
    private static final Color CIRCLE_COLOR = new Color(250, 90, 50);
    private static final Stroke AXES_STROKE = new BasicStroke( 1 );
    private static final int DOT_RAD = 7;
    private static final int HALF_DOT_RAD = 3;

    private GraphState state_;
    private int width;
    private int height;

    /**
     * Constructor
     */
    public DecorationRenderer(GraphState state)
    {
        state_ = state;
    }

    public void drawDecoration(Graphics2D g, int width, int height) {
        this.width = width;
        this.height = height;
        drawAxes(g);
        drawCircleAndDot(g, state_.params);
    }

    private void drawCircleAndDot(Graphics2D g, Parameters params) {

        g.setColor( CIRCLE_COLOR );
        g.setStroke( AXES_STROKE );
        drawCircle2(g, params);
        drawDot(g, params);
        drawLineToDot(g, params);
    }

    /**
     * Draw axes and central circle.
     */
    private void drawAxes(Graphics2D g)
    {
        if ( state_.showDecoration() ) {

            g.setColor( AXES_COLOR );
            g.drawLine( width >> 1, 0, width >> 1, height);
            g.drawLine( 0, height >> 1, width, height >> 1 );
            g.setColor( CIRCLE_COLOR );
            float r1 = state_.params.getR1();

            g.drawOval( (int) ((width >> 1) - r1),
                        (int) ((height >> 1) - r1), (int) (2 * r1), (int) (2 * r1) );
        }
    }

    private void drawCircle2(Graphics2D g, Parameters params )
    {
        Point2D center = params.getCenter(width, height);
        int sign = params.getSign();
        float r2 = params.getR2();
        g.drawOval( (int) (center.getX() - sign * r2),
                    (int) (center.getY() - sign * r2),
                    (int)(2 * sign * r2),  (int)(2 * sign * r2) );
    }

    private void drawDot(Graphics2D g, Parameters params )
    {
        Point2D center = params.getCenter(width, height);
        float p = params.getPos();
        float phi = params.getPhi();
        g.fillOval( (int) (center.getX() + p * Math.cos( phi )) - HALF_DOT_RAD,
                    (int) (center.getY() - p * Math.sin( phi )) - HALF_DOT_RAD,
                    DOT_RAD, DOT_RAD );
    }

    private void drawLineToDot(Graphics2D g, Parameters params)
    {
        Point2D center = params.getCenter(width, height);
        int side = params.getSign();
        float pos = params.getPos();
        float r2 = params.getR2();
        float phi = params.getPhi();
        if ( pos < 0 ) side = -side;
        if (params.getY() > 0) {
            int startX = (int) (center.getX() + side * r2 * Math.cos( phi ));
            int startY = (int) (center.getY() - side * r2 * Math.sin( phi ));
            g.drawLine(startX,  startY,
                      (int) params.getX(), (int) params.getY() );
        }
    }
}