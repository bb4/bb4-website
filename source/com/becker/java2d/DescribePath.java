package com.becker.java2d;

import java.awt.*;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

public class DescribePath
{
    public static void describePath( Shape s )
    {
        PathIterator pi = s.getPathIterator( null );

        while ( pi.isDone() == false ) {
            describeCurrentSegment( pi );
            pi.next();
        }
    }

    public static void describeCurrentSegment( PathIterator pi )
    {
        double[] coordinates = new double[6];
        int type = pi.currentSegment( coordinates );
        switch (type) {
            case PathIterator.SEG_MOVETO:
                System.out.println( "move to " +
                        coordinates[0] + ", " + coordinates[1] );
                break;
            case PathIterator.SEG_LINETO:
                System.out.println( "line to " +
                        coordinates[0] + ", " + coordinates[1] );
                break;
            case PathIterator.SEG_QUADTO:
                System.out.println( "quadratic to " +
                        coordinates[0] + ", " + coordinates[1] + ", " +
                        coordinates[2] + ", " + coordinates[3] );
                break;
            case PathIterator.SEG_CUBICTO:
                System.out.println( "cubic to " +
                        coordinates[0] + ", " + coordinates[1] + ", " +
                        coordinates[2] + ", " + coordinates[3] + ", " +
                        coordinates[4] + ", " + coordinates[5] );
                break;
            case PathIterator.SEG_CLOSE:
                System.out.println( "close" );
                break;
            default:
                break;
        }
    }

    public static void main( String[] args )
    {
        describePath( new Rectangle2D.Double( 0, 0, 72, 72 ) );
    }
}