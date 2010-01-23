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
    private static final Color CIRCLE_COLOR = new Color(220, 0, 20);
    private static final Stroke AXES_STROKE = new BasicStroke( 1 );
    private static final Stroke CIRCLE_STROKE = new BasicStroke( 2 );
    private static final Stroke SPOKE_STROKE = new BasicStroke( 3 );
    private static final int DOT_RAD = 7;
    private static final int HALF_DOT_RAD = 3;

    private Parameters params;
    private int width;
    private int height;

    /**
     * Constructor
     */
    public DecorationRenderer(Parameters params)
    {
        this.params = params;
    }

    public void drawDecoration(Graphics2D g, int width, int height) {
        this.width = width;
        this.height = height;

        drawAxes(g);
        drawCentralCircle(g);
        drawCircleAndDot(g);
    }

    /**
     * Draw axes
     */
    private void drawAxes(Graphics2D g)
    {
        g.setColor( AXES_COLOR );
        g.setStroke( AXES_STROKE );
        g.drawLine( width >> 1, 0, width >> 1, height);
        g.drawLine( 0, height >> 1, width, height >> 1 );
    }

    /**
     * draw central circle.
     */
    private void drawCentralCircle(Graphics2D g) {
        g.setColor( CIRCLE_COLOR );
        g.setStroke( CIRCLE_STROKE );
        float r1 = params.getR1();
        g.drawOval( (int) ((width >> 1) - r1),
                    (int) ((height >> 1) - r1), (int) (2 * r1), (int) (2 * r1) );
    }

    private void drawCircleAndDot(Graphics2D g) {

        g.setColor( CIRCLE_COLOR );
        drawCircle2(g);
        drawLineAndDot(g);
    }

    private void drawCircle2(Graphics2D g)
    {
        Point2D center = params.getCenter(width, height);
        int sign = params.getSign();
        float r2 = params.getR2();
        g.drawOval( (int) (center.getX() - sign * r2), (int) (center.getY() - sign * r2),
                    (int)(2 * sign * r2),  (int)(2 * sign * r2) );
    }

    private void drawLineAndDot(Graphics2D g)
    {
        Point2D center = params.getCenter(width, height);
        int side = params.getSign();
        float pos = params.getPos();
        float r2 = params.getR2();
        float phi = params.getPhi();
        Point dotPos = new Point((int) (center.getX() + pos * Math.cos( phi )),
                                 (int) (center.getY() - pos * Math.sin( phi )));
        if ( pos < 0 ) side = -side;
        g.setStroke( SPOKE_STROKE );
        if (params.getY() > 0) {
            int startX = (int) (center.getX() + side * r2 * Math.cos( phi ));
            int startY = (int) (center.getY() - side * r2 * Math.sin( phi ));
            g.drawLine(startX,  startY, dotPos.x, dotPos.y);
        }
        drawDot(g, dotPos);
    }

    private void drawDot(Graphics2D g, Point dotPos)
    {
        g.fillOval( dotPos.x - HALF_DOT_RAD, dotPos.y - HALF_DOT_RAD,
                    DOT_RAD, DOT_RAD );
    }
}