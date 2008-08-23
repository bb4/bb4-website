package com.becker.spirograph;

import com.becker.common.*;
import com.becker.common.util.ImageUtil;
import com.becker.common.util.MathUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

/**
 * Program to simulate a SpiroGraph.
 * Adapted from Divid Little's original work.
 *
 *  to do:
 *   - convert to polar coords
 *   - search the space of images using a genetic algorithm
 *
 * @author David Little
 * @author Barry Becker
 */

public class GraphRenderer extends JPanel implements Runnable
{
    public static final int HT = 1000, WD = 1200;

    private static final Color AXES_COLOR = new Color(120, 120, 200);
    private static final Color CIRCLE_COLOR = new Color(90, 90, 150);
    private static final Stroke AXES_STROKE = new BasicStroke( 1 );
    private static final int DOT_RAD = 7;
    private static final int HALF_DOT_RAD = 3;

    public static volatile Thread thread_ = null;

    private static volatile boolean requestClear_ = false;
    private static volatile boolean isRendering_ = false;

    private BufferedImage offImage_;
    private Graphics2D offlineGraphics_;
    private float[] center_;

    // set this var instead of using Thread.stop (see
    // http://java.sun.com/products/jdk/1.2/docs/guide/misc/threadPrimitiveDeprecation.html )
    private boolean paused_ = false;
    private final Object pauseLock_ = new Object(); // monitor

    private GraphState state_;
    private JButton drawButton_;

    GraphRenderer(GraphState state, JButton drawButton)
    {
        drawButton_ = drawButton;
        commonConstructor(state);
    }

    private void commonConstructor(GraphState state)
    {
        state_ = state;
        setBackground( Color.white );
        center_ = new float[2];
        state_.setX((WD >> 1) + state_.getR1() + (state_.getR2() + state_.getSign()) + state_.getPos());
        state_.setY(HT >> 1);
        state_.recordValues();

        offImage_ = ImageUtil.createCompatibleImage( WD, HT );
        if ( offImage_ != null ) {
            offlineGraphics_ = offImage_.createGraphics();
            offlineGraphics_.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON );
            offlineGraphics_.setRenderingHint( RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY );
            offlineGraphics_.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,
                    RenderingHints.VALUE_COLOR_RENDER_QUALITY );
        }
        else {
            System.out.println( "error the offImage is null!" );
        }
        clear();
    }

    /**
     * starts the rendering thread.
     */
    public void run()
    {
        Thread thisThread = Thread.currentThread();
        while ( thread_ == thisThread ) {
            doRendering();
        }
    }

    /**
     * Animate the rendering of the spirograph.
     */
    private synchronized void doRendering()
    {
        int count = 0;
        initializeValues();
        boolean refresh = false;
        isRendering_ = true;

        float r1 = state_.getR1();
        float r2 = state_.getR2();
        float p = state_.getPos();
        float sign = state_.getSign();

        if ( r2 == 0 ) return; // avoid degenerate case - div by 0

        long gcd = MathUtil.gcd( (long) r1, (long) (sign * r2) );
        int revs = (int)((sign * r2) / gcd);

        offlineGraphics_.setPaintMode();

        float n = 1.0f + state_.getNumSegmentsPerRev() * (Math.abs( p / r2 ));

        while ( count++ < (int) (n * revs + 0.5) && isRendering_) {
            r1 = state_.getR1();
            r2 = state_.getR2();
            p = state_.getPos();
            offlineGraphics_.setColor( state_.getColor() );

            if ( count == (int) (n * revs + 0.5) )
                state_.setTheta(0.0f);
            else
                state_.setTheta((float)(2.0f * Math.PI * count / n));
            float theta = state_.getTheta();
            state_.setPhi(theta * (1.0f + r1 / r2));
            float phi = state_.getPhi();
            setPoint(p, phi);

            float oldx = state_.getOldX();
            float oldy = state_.getOldY();

            if (state_.showAxes() && refresh ) {
                // erase the old indicators
                float oldR1 = state_.getOldR1();
                float oldR2 = state_.getOldR2();
                float oldSign = state_.getOldSign();
                float oldPhi = state_.getOldPhi();
                float oldTheta = state_.getOldTheta();

                offlineGraphics_.setColor( CIRCLE_COLOR );
                offlineGraphics_.setStroke( AXES_STROKE );
                //System.out.println("erase r1="+oldR1+" r2="+oldR2 +" theta="+oldTheta);
                drawCircle2( oldR1, oldR2, oldSign, oldTheta );
                drawDot( oldR1, oldR2, oldSign, oldTheta, state_.getOldPos(), oldPhi );
                // @@ this will set x and y in state to oldx and oldy (how did this work before?)
                float x = state_.getX();
                float y = state_.getY();
                drawLineToDot( oldR1, oldR2, oldSign, state_.getOldPos(), oldTheta, oldPhi, oldx, oldy );
                state_.setX(x);
                state_.setY(y);
                offlineGraphics_.setPaintMode();
                offlineGraphics_.setColor( state_.getColor() );
            }
            waitIfPaused();
            int velocity = state_.getVelocity();
            refresh =  (velocity != GraphState.VELOCITY_MAX); // (count % velocity == 0) &&

            Stroke stroke = new BasicStroke( (float)state_.getWidth() / (float)GraphState.INITIAL_LINE_WIDTH );
            offlineGraphics_.setStroke( stroke );
            offlineGraphics_.drawLine( (int) oldx, (int) oldy, (int) state_.getX(), (int) state_.getY() );

            if ( state_.showAxes() && refresh ) {
                offlineGraphics_.setXORMode( getBackground() );
                offlineGraphics_.setColor( CIRCLE_COLOR );
                offlineGraphics_.setStroke( AXES_STROKE );
                //System.out.println("draw r1="+r1+" r2="+r2 +" theta="+theta);
                drawCircle2( r1, r2, sign, theta );
                drawDot( r1, r2, sign, theta, p, phi );
                drawLineToDot( r1, r2, sign, p, theta, phi, state_.getX(), state_.getY() );
            }
            if ( refresh ) {
                //System.out.println("ct="+count+" v="+v);
                repaint();
                if ( velocity < 100 ) {
                    try {
                        Thread.sleep( 200 / velocity );
                    } catch (InterruptedException e) {}
                }
                if (requestClear_) {
                    doClear();
                    requestClear_ = false;
                }
            }
            state_.recordValues();

        }
        repaint();
        drawButton_.setText( SpiroGraph.DRAW_LABEL );
        isRendering_= false;
        thread_ = new Thread( this );
    }

    public void setPaused( boolean newPauseState )
    {
        synchronized (pauseLock_) {
            if ( paused_ != newPauseState ) {
                paused_ = newPauseState;
                pauseLock_.notifyAll();
            }
        }
    }

    private synchronized void waitIfPaused()
    {
        if ( !paused_ ) return;
        // pause if we get suspended
        try {
            synchronized (pauseLock_) {
                while ( paused_ ) pauseLock_.wait();
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
        g.drawImage( offImage_, (getSize().width - WD) >> 1, (getSize().height - HT) >> 1, this );
    }

    public void initializeValues()
    {
        state_.setSign(state_.getR2() < 0 ? -1:1);
        state_.setTheta(0.0f);
        state_.setPhi(0.0f);
        state_.setX((WD >> 1) + state_.getR1() + state_.getR2() + state_.getSign() + state_.getPos());
        state_.setY(HT >> 1);
        state_.recordValues();
    }

    /**
     * @@ has side effect of setting x and y in state.
     * @param p
     * @param phi
     */
    public void setPoint(float p, float phi)
    {
        setCenter( state_.getR1(), state_.getR2(), state_.getSign(), state_.getTheta() );
        state_.setX((float)(center_[0] + p * Math.cos( phi )));
        state_.setY((float)(center_[1] - p * Math.sin( phi )));
    }

    public void setCenter( float r1, float r2, float sign, float theta )
    {
        center_[0] = (float)((WD >> 1) + (r1 + r2 * sign) * Math.cos( theta ));
        center_[1] = (float)((HT >> 1) - (r1 + r2 * sign) * Math.sin( theta ));
    }

    public void clear()
    {
        if (isRendering_) {
            requestClear_ = true;
        } else {
            doClear();
        }
    }

    public void doClear() {
        if ( offlineGraphics_ == null )
            return;
        offlineGraphics_.setPaintMode();
        offlineGraphics_.setColor( getBackground() );
            offlineGraphics_.fillRect( 0, 0, WD, HT );
        drawAxes();
    }

    public void drawAxes()
    {
        if ( state_.showAxes() ) {

            offlineGraphics_.setXORMode( getBackground() );
            offlineGraphics_.setColor( AXES_COLOR );
            offlineGraphics_.drawLine( WD >> 1, 0, WD >> 1, HT );
            offlineGraphics_.drawLine( 0, HT >> 1, WD, HT >> 1 );
            offlineGraphics_.setColor( CIRCLE_COLOR );
            float r1 = state_.getR1();
            float r2 = state_.getR2();
            //float p = state_.getPos();
            float sign = state_.getSign();
            float theta = state_.getTheta();
            //float phi = state_.getPhi();

            drawCircle1( r1 );
            drawCircle2( r1, r2, sign, theta );
            //drawDot( r1, r2, sign, theta, p, phi );
            //drawLineToDot( r1, r2, sign, p, theta, phi, state_.getX(), state_.getY() );
        }
        repaint();
    }


    public void adjustCircle1()
    {
        if ( state_.showAxes() ) {
            drawCircle1( state_.getOldR1() );
            drawCircle1( state_.getR1() );
            adjustCircle2();
        }
        else
            state_.recordValues();
    }

    public void adjustCircle2()
    {
        if ( state_.showAxes() ) {
            drawCircle2( state_.getOldR1(), state_.getOldR2(), state_.getOldSign(), state_.getOldTheta() );
            drawCircle2( state_.getR1(), state_.getR2(), state_.getSign(), state_.getTheta() );
            adjustDot();
        }
        else
            state_.recordValues();
    }

    public void adjustDot()
    {
        if ( state_.showAxes() ) {
            drawDot( state_.getOldR1(), state_.getOldR2(),
                     state_.getOldSign(), state_.getOldTheta(), state_.getOldPos(), state_.getOldPhi() );
            drawDot( state_.getR1(), state_.getR2(),
                     state_.getSign(), state_.getTheta(), state_.getPos(), state_.getPhi() );
            adjustLineToDot();
        }
        else {
            state_.recordValues();
        }
    }

    public void adjustLineToDot()
    {
        /*
        drawLineToDot( state_.getOldR1(), state_.getOldR2(),
                       state_.getOldSign(), state_.getOldPos(), state_.getOldTheta(), state_.getOldPhi(),
                       state_.getOldX(), state_.getOldY() );
        drawLineToDot( state_.getR1(), state_.getR2(),
                       state_.getSign(), state_.getPos(), state_.getTheta(), state_.getPhi(),
                       state_.getX(), state_.getY() );
        */
        state_.recordValues();
        repaint();
    }

    private void drawCircle1( float r1 )
    {
        offlineGraphics_.drawOval( (int) ((WD >> 1) - r1), (int) ((HT >> 1) - r1), (int) (2 * r1), (int) (2 * r1) );
    }

    private void drawCircle2( float r1, float r2, float sign, float theta )
    {
        setCenter( r1, r2, sign, theta );
        offlineGraphics_.drawOval( (int) (center_[0] - sign * r2), (int) (center_[1] - sign * r2),
                       (int) (2 * sign * r2), (int) (2 * sign * r2) );
    }

    private void drawDot( float r1, float r2, float sign, float theta, float p, float phi )
    {
        setCenter( r1, r2, sign, theta );
        //System.out.println("r1="+r1+" r2="+r2 + " p="+p + " phi="+phi+" theta="+theta+ " center="+center_[0] + " "+center_[1]);
        offlineGraphics_.fillOval( (int) (center_[0] + p * Math.cos( phi )) - HALF_DOT_RAD,
                (int) (center_[1] - p * Math.sin( phi )) - HALF_DOT_RAD,
                DOT_RAD, DOT_RAD );
    }


    private void drawLineToDot( float r1, float r2, float sign, float p, float theta, float phi, float x, float y )
    {
        setCenter( r1, r2, sign, theta );
        float side = sign;
        if ( p < 0 ) side = -sign;

        offlineGraphics_.drawLine( (int) (center_[0] + side * r2 * Math.cos( phi )),
                (int) (center_[1] - side * r2 * Math.sin( phi )),
                (int) x, (int) y );
        setPoint(p, phi);
    }

    public void reset() {
        // stop the thread
        clear();
        thread_ = null;
        isRendering_ = false;
        state_.reset();

        setPoint(state_.getPos(), 0);
        adjustCircle2();
    }

}