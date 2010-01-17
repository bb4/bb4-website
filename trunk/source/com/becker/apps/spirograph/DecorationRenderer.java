package com.becker.apps.spirograph;

import java.awt.*;

/**
 * Draws the Axes, circles, and spoke.
 * The circles and spoke represent the plastic stylus tool of the old spirograph.
 * @author Barry Becker
 */
public class DecorationRenderer
{
    private static final Color AXES_COLOR = new Color(120, 120, 200);
    private static final Color CIRCLE_COLOR = new Color(90, 90, 150);
    private static final Stroke AXES_STROKE = new BasicStroke( 1 );
    private static final int DOT_RAD = 7;
    private static final int HALF_DOT_RAD = 3;

    private float[] center_;
    private GraphState state_;
    private int width;
    private int height;

    /**
     * Constructor
     */
    public DecorationRenderer(GraphState state)
    {
        state_ = state;
        center_ = new float[2];
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

    private void setCenter(Parameters params)
    {
        float r1 = params.getR1();
        float r2 = params.getR2();
        float sign = params.getSign();
        float theta = params.getTheta();
        center_[0] = (float)((width >> 1) + (r1 + r2 * sign) * Math.cos( theta ));
        center_[1] = (float)((height >> 1) - (r1 + r2 * sign) * Math.sin( theta ));
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
        setCenter( params );
        int sign = params.getSign();
        float r2 = params.getR2();
        g.drawOval( (int) (center_[0] - sign * r2), (int) (center_[1] - sign * r2),
                                   (int)(2 * sign * r2),  (int)(2 * sign * r2) );
    }

    private void drawDot(Graphics2D g, Parameters params )
    {
        setCenter( params );
        float p = params.getPos();
        float phi = params.getPhi();
        g.fillOval( (int) (center_[0] + p * Math.cos( phi )) - HALF_DOT_RAD,
                (int) (center_[1] - p * Math.sin( phi )) - HALF_DOT_RAD, DOT_RAD, DOT_RAD );
    }

    private void drawLineToDot(Graphics2D g, Parameters params)
    {
        setCenter( params );
        int side = params.getSign();
        float pos = params.getPos();
        float r2 = params.getR2();
        float phi = params.getPhi();
        if ( pos < 0 ) side = -side;
        if (params.getY() > 0) {
            g.drawLine( (int) (center_[0] + side * r2 * Math.cos( phi )),
                (int) (center_[1] - side * r2 * Math.sin( phi )),
                (int) params.getX(), (int) params.getY() );
        }
    }
}