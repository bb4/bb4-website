package com.becker.liquid;

import java.awt.geom.*;

/**
 *  Walls form a basis for solid objects in the simulation space.
 *  They are straight lines and immovable.
 *  Endpoints of walls must be on cell boundaries
 *
 *  @author Barry Becker
 */
public class Wall
{

    // the 2 endpoints defining the wall
    protected Line2D.Double segment_;

    // the thickness of the wall. @@ should it have other properties?
    protected float thickness_;

    //Constructor
    public Wall( double x1, double y1, double x2, double y2 )
    {
        segment_ = new Line2D.Double( x1, y1, x2, y2 );
        thickness_ = 2.0f;
    }

    public Point2D.Double getStartPoint()
    {
        return (Point2D.Double) segment_.getP1();
    }

    public Point2D.Double getStopPoint()
    {
        return (Point2D.Double) segment_.getP2();
    }

    public float getThickness()
    {
        return thickness_;
    }

    public boolean intersects( Rectangle2D.Double rect )
    {
        return segment_.intersects( rect );
    }

    /**
     * returns true if the point lies on the wall
     */
    public boolean intersects( double i, double j, double eps )
    {
        return segment_.intersects( i, j, eps, eps );
    }

    public boolean isVertical()
    {
        return (segment_.getX1() == segment_.getX2());
    }

    public boolean isHorizontal()
    {
        return (segment_.getY1() == segment_.getY2());
    }

}
