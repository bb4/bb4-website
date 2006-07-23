package com.becker.spirograph;



/**
 * Program to simulate a super SpiroGraph
 * Instead of having 2 circles, one traveling around the other,
 * we have an aribtrary number with different radii.
 *
 * @author Barry Becker
 *

public class SuperGraphRenderer extends GraphRenderer
{
    protected int NUM_CIRCLES = 4; // must be 2 or more
    protected int ITS_PER_REV = 20; // iteractions per revolution
    // the circle radii. rad[0] is the radius of the innermost circle.
    protected double[] rad = new double[NUM_CIRCLES];
    protected double ro;
    protected Point2D.Double oldp;
    protected Point2D.Double newp;

    SuperGraphRenderer(GraphState state)
    {
        super(state);
        rad[0] = 70.0;
        rad[1] = 41.0;
        rad[2] = 30.0;
        rad[3] = 20.0;
        ro = 41.0;  // the length of the spoke on the outermost circle
        initializeValues();
    }

    // draw the whole pattern (or until point when interupted)
    protected synchronized void doRendering( Thread thisThread )
    {
        count = 0;
        initializeValues();
        boolean refresh = false;

        int gcdiv = (int) rad[NUM_CIRCLES - 1];
        if ( NUM_CIRCLES > 1 )
            gcdiv = gcd( (int) rad[NUM_CIRCLES - 1], (int) rad[NUM_CIRCLES - 2] );
        for ( int i = NUM_CIRCLES - 3; i > 0; i-- ) {
            gcdiv = gcd( gcdiv, (int) rad[i] );
        }
        int revs = (int) (10.0 * rad[NUM_CIRCLES - 1] / gcdiv);
        //int revs = (int)(sign*R2)/gcd((int)R1,(int)(sign*R2));

        Stroke thinStroke = new BasicStroke( 1 );
        offlineGraphics.setPaintMode();
        offlineGraphics.setColor( state_.getColor() );

        //n = 1.0 + 50.0*(Math.abs(p/R2));
        n = ITS_PER_REV * (Math.abs( ro / rad[NUM_CIRCLES - 1] ));
        for ( int i = NUM_CIRCLES - 2; i > 0; i-- ) {
            n *= (ITS_PER_REV * rad[i - 1] / rad[i]);
        }
        n += 1;
        System.out.println( "numrevs = " + revs + " n=" + n + " gcdiv=" + gcdiv );

        while ( count++ < (int) (n * revs + .5) ) {

            if ( count == (int) n * revs + .5 )
                theta = 0.0;
            else
                theta = 2.0 * Math.PI * count / (float) n;
            waitIfPaused();
            refresh = (count % v == 0) && (v != VELOCITY_MAX);

            Stroke stroke = new BasicStroke( width / 3 );
            offlineGraphics.setStroke( stroke );

            newp = computePos( theta, 0 );
            newp.x += OX;
            newp.y += OY;
            //System.out.println("theta="+theta+"  coord="+newp.x+", "+newp.y);
            offlineGraphics.drawLine( (int) oldp.x, (int) oldp.y, (int) newp.x, (int) newp.y );
            oldp = newp;

            if ( refresh ) {
                //System.out.println("ct="+count+" v="+v);
                if ( v < 2 ) {
                    try {
                        Thread.sleep( 100 );
                    } catch (InterruptedException e) {
                    }
                }
                repaint();
            }
        }
        repaint();
        SuperSpiroGraph.draw.setText( "Draw" );
        SuperGraphRenderer.thread = new Thread( this );
    }

    public void initializeValues()
    {

        int rr = 0; // initial x pos
        for ( int i = 0; i < NUM_CIRCLES; i++ ) {
            rr += (int) rad[i];
        }
        rr += (int) p - (int) rad[NUM_CIRCLES - 1];
        oldp = new Point2D.Double( OX + rr, OY + 0 );
    }

    private Point2D.Double computePos( double theta, int num )
    {
        Point2D.Double newp = new Point2D.Double();
        if ( num == NUM_CIRCLES - 1 ) {
            newp.x = ro * Math.cos( theta );
            newp.y = ro * Math.sin( theta );
            return newp;
        }
        else {
            double newTheta = rad[num] / rad[num + 1] * theta;

            // recursive call!
            Point2D.Double p = computePos( newTheta, num + 1 );
            double r = rad[num] + rad[num + 1];
            newp.x = p.x * Math.cos( theta ) + p.y * Math.sin( theta ) + r * Math.cos( theta );
            newp.y = p.x * Math.sin( theta ) + p.y * Math.cos( theta ) + r * Math.sin( theta );
            return newp;
        }
    }
}           */