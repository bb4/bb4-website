package com.becker.spirograph;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Program to simulate a SpiroGraph
 * Adapted from Divid Little's original work
 *
 *  to do:
 *   - convert to polar coords
 *   - create a super spirograph with 2 nested circles
 *   - search the space of images using a genetic algorithm
 *
 * @author David Little
 * @author Barry Becker
 * @version drawGraph1.1
 */

public class GraphRenderer extends JPanel implements Runnable
{
    public static final int H = 1000, W = 1200;
    public static final int OX = W / 2, OY = H / 2;
    public static final int VELOCITY_MAX = 100;
    public static final int INITIAL_LINE_WIDTH = 3;
    protected double n = 270;
    protected int v = 2;
    protected int count;
    protected int width = INITIAL_LINE_WIDTH;
    protected BufferedImage offImage;
    protected Graphics2D offg;
    protected double[] center;
    //                   0      1     2        3        4        5     6     7
    protected double R1,    R2,    p,    sign,   theta,     phi,    x,    y;
    protected double oldR1, oldR2, oldp, oldSign, oldTheta, oldPhi, oldx, oldy;

    // set this var instead of using Thread.stop (see
    // http://java.sun.com/products/jdk/1.2/docs/guide/misc/threadPrimitiveDeprecation.html )
    public static volatile Thread thread;
    protected boolean paused = false;
    protected final Object pauseLock = new Object(); // monitor

    GraphRenderer()
    {
        super();
        commonConstructor();
    }

    protected void commonConstructor()
    {
        setBackground( Color.white );
        center = new double[2];
        R1 = 60.0;
        R2 = 60.0;
        p = 60.0;
        sign = 1.0;
        theta = 0.0;
        phi = 0.0;
        x = W / 2 + R1 + (R2 + sign) + p;	//point.x
        y = H / 2;		//point.y
        recordValues();
    }

    /**
     * starts the rendering thread.
     */
    public void run()
    {
        Thread thisThread = Thread.currentThread();
        while ( thread == thisThread ) {
            doRendering( thisThread );
        }
    }

    protected synchronized void doRendering( Thread thisThread )
    {
        count = 0;
        initializeValues();
        boolean refresh = false;
        if ( R2 == 0 ) return; // avoid degenerate case - div by 0

        int revs = (int) (sign * R2) / gcd( (int) R1, (int) (sign * R2) );

        Stroke thinStroke = new BasicStroke( 1 );
        offg.setPaintMode();
        offg.setColor( SpiroGraph.COLOR );

        n = 1.0 + 50.0 * (Math.abs( p / R2 ));
        while ( count++ < (int) (n * revs + .5) ) {

            if ( count == (int) (n * revs + .5) )
                theta = 0.0;
            else
                theta = 2.0 * Math.PI * count / n;
            phi = theta * (1 + R1 / R2);
            setPoint();

            if ( SpiroGraph.hide.getText().equals( "Hide" ) && refresh ) {
                // erase the old indicators
                offg.setStroke( thinStroke );
                drawCircle2( oldR1, oldR2, oldSign, oldTheta );
                drawDot( oldR1, oldR2, oldSign, oldTheta, oldp, oldPhi );
                drawLineToDot( oldR1, oldR2, oldSign, oldp, oldTheta, oldPhi, oldx, oldy );
                offg.setPaintMode();
                offg.setColor( SpiroGraph.COLOR );
            }
            waitIfPaused();
            refresh = (count % (v) == 0) && (v != VELOCITY_MAX);

            Stroke stroke = new BasicStroke( width / 3 );
            offg.setStroke( stroke );
            offg.drawLine( (int) oldx, (int) oldy, (int) x, (int) y );

            if ( SpiroGraph.hide.getText().equals( "Hide" ) && refresh ) {
                offg.setXORMode( getBackground() );
                offg.setColor( Color.black );
                offg.setStroke( thinStroke );
                drawCircle2( R1, R2, sign, theta );
                drawDot( R1, R2, sign, theta, p, phi );
                drawLineToDot( R1, R2, sign, p, theta, phi, x, y );
            }
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

            recordValues();
        }
        repaint();
        SpiroGraph.draw.setText( "Draw" );
        GraphRenderer.thread = new Thread( this );
    }

    public void setPaused( boolean newPauseState )
    {
        synchronized (pauseLock) {
            if ( paused != newPauseState ) {
                paused = newPauseState;
                pauseLock.notifyAll();
            }
        }
    }

    protected void waitIfPaused()
    {
        if ( !paused ) return;
        // pause if we get suspended
        try {
            synchronized (pauseLock) {
                while ( paused ) pauseLock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update( Graphics g )
    {
        paint( g );
    }

    public void paint( Graphics g )
    {
        g.drawImage( offImage, (this.getSize().width - W) / 2, (this.getSize().height - H) / 2, this );
    }

    public void initializeValues()
    {
        R1 = SpiroGraph.RAD1.getValue();
        R2 = SpiroGraph.RAD2.getValue();
        p = SpiroGraph.POS.getValue();
        if ( R2 < 0 )
            sign = -1.0;
        else
            sign = 1.0;
        theta = 0.0;
        phi = 0.0;
        x = W / 2 + R1 + R2 + sign + p;
        y = H / 2;
        recordValues();
    }

    public void setPoint()
    {
        setCenter( R1, R2, sign, theta );
        x = center[0] + p * Math.cos( phi );
        y = center[1] - p * Math.sin( phi );
    }

    public void setCenter( double R1, double R2, double sign, double theta )
    {
        center[0] = W / 2 + (R1 + R2 + sign) * Math.cos( theta );  // + sign?
        center[1] = H / 2 - (R1 + R2 + sign) * Math.sin( theta );
    }

    public void clear()
    {
        if ( offg == null ) return;
        offg.setPaintMode();
        offg.setColor( getBackground() );
        offg.fillRect( 0, 0, W, H );
        drawAxes();
    }

    public void drawAxes()
    {
        if ( SpiroGraph.hide.getText().equals( "Hide" ) ) {
            offg.setXORMode( getBackground() );
            offg.setColor( Color.gray );
            offg.drawLine( W / 2, 0, W / 2, H );
            offg.drawLine( 0, H / 2, W, H / 2 );
            offg.setColor( Color.black );
            drawCircle1( R1 );
            drawCircle2( R1, R2, sign, theta );
            drawDot( R1, R2, sign, theta, p, phi );
            drawLineToDot( R1, R2, sign, p, theta, phi, x, y );
        }
        repaint();
    }

    // find the greatest common divisor of 2 numbers
    public int gcd( int x, int y )
    {
        if ( x % y == 0 ) return y;
        return gcd( y, x % y );
    }

    public void adjustCircle1()
    {
        if ( SpiroGraph.hide.getText().equals( "Hide" ) ) {
            drawCircle1( oldR1 );
            drawCircle1( R1 );
            adjustCircle2();
        }
        else
            recordValues();
    }

    public void adjustCircle2()
    {
        if ( SpiroGraph.hide.getText().equals( "Hide" ) ) {
            drawCircle2( oldR1, oldR2, oldSign, oldTheta );
            drawCircle2( R1, R2, sign, theta );
            adjustDot();
        }
        else
            recordValues();
    }

    public void adjustDot()
    {
        if ( SpiroGraph.hide.getText().equals( "Hide" ) ) {
            drawDot( oldR1, oldR2, oldSign, oldTheta, oldp, oldPhi );
            drawDot( R1, R2, sign, theta, p, phi );
            adjustLineToDot();
        }
        else
            recordValues();
    }

    public void adjustLineToDot()
    {
        drawLineToDot( oldR1, oldR2, oldSign, oldp, oldTheta, oldPhi, oldx, oldy );
        drawLineToDot( R1, R2, sign, p, theta, phi, x, y );
        recordValues();
        repaint();
    }

    public void drawCircle1( double R1 )
    {
        offg.drawOval( (int) (W / 2 - R1), (int) (H / 2 - R1), (int) (2 * R1), (int) (2 * R1) );
    }

    public void drawCircle2( double R1, double R2, double sign, double theta )
    {
        setCenter( R1, R2, sign, theta );
        offg.drawOval( (int) (center[0] - sign * R2), (int) (center[1] - sign * R2),
                       (int) (2 * sign * R2), (int) (2 * sign * R2) );
    }

    public void drawDot( double R1, double R2, double sign, double theta, double p, double phi )
    {
        setCenter( R1, R2, sign, theta );
        offg.fillOval( (int) (center[0] + p * Math.cos( phi )) - 2,
                (int) (center[1] - p * Math.sin( phi )) - 2,
                5, 5 );
    }

    public void drawLineToDot( double R1, double R2, double sign, double p, double theta, double phi, double x, double y )
    {
        setCenter( R1, R2, sign, theta );
        double side = sign;
        if ( p < 0 ) side = -sign;

        offg.drawLine( (int) (center[0] + side * R2 * Math.cos( phi )),
                (int) (center[1] - side * R2 * Math.sin( phi )),
                (int) x, (int) y );
        setPoint();
    }

    public void recordValues()
    {
        oldR1 = R1;
        oldR2 = R2;
        oldp = p;
        oldSign = sign;
        oldTheta = theta;
        oldPhi = phi;
        oldx = x;
        oldy = y;
    }
}