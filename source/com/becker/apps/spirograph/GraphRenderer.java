package com.becker.apps.spirograph;

import com.becker.common.math.MathUtil;
import com.becker.common.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Program to simulate a SpiroGraph.
 * Adapted from Divid Little's original work.
 *
 * @author David Little
 * @author Barry Becker
 */

public class GraphRenderer extends JPanel implements Runnable
{
    public static final int IMG_HEIGHT = 1600;
    public static final int IMG_WIDTH = 2400;

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
    //private JButton drawButton_;

    GraphRenderer(GraphState state)
    {
        //drawButton_ = drawButton;
        commonConstructor(state);
    }

    private void commonConstructor(GraphState state)
    {
        state_ = state;
        setBackground( Color.white );
        center_ = new float[2];
        state_.initialize(IMG_WIDTH, IMG_HEIGHT);

        offImage_ = ImageUtil.createCompatibleImage(IMG_WIDTH, IMG_HEIGHT);
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
            throw new InstantiationError("We could not create the offImage.");
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
        state_.initialize(IMG_WIDTH, IMG_HEIGHT);
        boolean refresh = false;
        isRendering_ = true;

        float r1 = state_.params.getR1();
        float r2 = state_.params.getR2();
        float p = state_.params.getPos();
        float sign = state_.params.getSign();

        // avoid degenerate (divide by 0 case) curves.
        if ( r2 == 0 ) return;

        long gcd = MathUtil.gcd( (long) r1, (long) (sign * r2) );
        int revs = (int)((sign * r2) / gcd);

        offlineGraphics_.setPaintMode();

        float n = 1.0f + state_.getNumSegmentsPerRev() * (Math.abs( p / r2 ));

        while ( count++ < (int) (n * revs + 0.5) && isRendering_) {
            refresh = drawSegment(count, refresh, revs, n);
        }
        repaint();
        //drawButton_.setText( SpiroGraph.DRAW_LABEL );
        isRendering_= false;
        thread_ = new Thread( this );
    }

    /**
     * Draw a small line segment that makes up the larger sprial curve.
     * @return true if refreshed after drawing the segment. Do not refresh if velocity is max.
     */
    private boolean drawSegment(int count, boolean refresh, int revs, float n) {
        float r1;
        float r2;
        float p;
        r1 = state_.params.getR1();
        r2 = state_.params.getR2();
        p = state_.params.getPos();
        offlineGraphics_.setColor( state_.getColor() );

        if ( count == (int) (n * revs + 0.5) )
            state_.params.setTheta(0.0f);
        else
            state_.params.setTheta((float)(2.0f * Math.PI * count / n));
        float theta = state_.params.getTheta();
        state_.params.setPhi(theta * (1.0f + r1 / r2));
        float phi = state_.params.getPhi();
        setPoint(p, phi);

        if (state_.showAxes() && refresh ) {
            drawIndicators();
        }
        waitIfPaused();
        int velocity = state_.getVelocity();
        refresh =  (velocity != GraphState.VELOCITY_MAX);

        Stroke stroke = new BasicStroke( (float)state_.getWidth() / (float)GraphState.INITIAL_LINE_WIDTH );
        offlineGraphics_.setStroke( stroke );
        offlineGraphics_.drawLine((int) state_.oldParams.getX(), (int) state_.oldParams.getY(),
                                  (int) state_.params.getX(), (int) state_.params.getY() );

        if ( state_.showAxes() && refresh ) {
            offlineGraphics_.setXORMode( getBackground() );

            drawCircleAndDot(state_.params);
        }
        if ( refresh ) {
            repaint();
            if ( velocity < 100 ) {
                try {
                    Thread.sleep( 200 / velocity );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (requestClear_) {
                doClear();
                requestClear_ = false;
            }
        }
        state_.recordValues();
        return refresh;
    }

    private void drawIndicators() {
        // erase the old indicators
        drawCircleAndDot(state_.oldParams);

        offlineGraphics_.setPaintMode();
        offlineGraphics_.setColor(state_.getColor());
    }


    private void drawCircleAndDot(Parameters params) {

        offlineGraphics_.setColor( CIRCLE_COLOR );
        offlineGraphics_.setStroke( AXES_STROKE );
        drawCircle2(params);
        drawDot(params);
        drawLineToDot(params);
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

    @Override
    public void paint( Graphics g )
    {
        int xpos = (getSize().width - IMG_WIDTH) >> 1;
        int ypos = (getSize().height - IMG_HEIGHT) >> 1;
        g.drawImage( offImage_, xpos, ypos, this );
    }

    /**
     * @@ has side effect of setting x and y in state.
     * @param p
     * @param phi
     */
    public void setPoint(float p, float phi)
    {
        setCenter( state_.params );
        state_.params.setX((float)(center_[0] + p * Math.cos( phi )));
        state_.params.setY((float)(center_[1] - p * Math.sin( phi )));
    }

    public void setCenter(Parameters params)
    {
        float r1 = params.getR1();
        float r2 = params.getR2();
        float sign = params.getSign();
        float theta = params.getTheta();
        center_[0] = (float)((IMG_WIDTH >> 1) + (r1 + r2 * sign) * Math.cos( theta ));
        center_[1] = (float)((IMG_HEIGHT >> 1) - (r1 + r2 * sign) * Math.sin( theta ));
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
            offlineGraphics_.fillRect( 0, 0, IMG_WIDTH, IMG_HEIGHT);
        drawAxes();
    }

    public void drawAxes()
    {
        if ( state_.showAxes() ) {

            offlineGraphics_.setXORMode( getBackground() );
            offlineGraphics_.setColor( AXES_COLOR );
            offlineGraphics_.drawLine( IMG_WIDTH >> 1, 0, IMG_WIDTH >> 1, IMG_HEIGHT);
            offlineGraphics_.drawLine( 0, IMG_HEIGHT >> 1, IMG_WIDTH, IMG_HEIGHT >> 1 );
            offlineGraphics_.setColor( CIRCLE_COLOR );
            float r1 = state_.params.getR1();
            float r2 = state_.params.getR2();
            //float p = state_.params.getPos();
            float sign = state_.params.getSign();
            float theta = state_.params.getTheta();
            //float phi = state_.params.getPhi();

            drawCircle1( r1 );
            drawCircle2( state_.params );
            //drawDot( r1, r2, sign, theta, p, phi );
            //drawLineToDot( r1, r2, sign, p, theta, phi, state_.getX(), state_.getY() );
        }
        repaint();
    }

    public void adjustCircle1()
    {
        if ( state_.showAxes() ) {
            drawCircle1( state_.oldParams.getR1() );
            drawCircle1( state_.params.getR1() );
            adjustCircle2();
        }
        else
            state_.recordValues();
    }

    public void adjustCircle2()
    {
        if ( state_.showAxes() ) {
            drawCircle2( state_.oldParams );
            drawCircle2( state_.params);
            adjustDot();
        }
        else
            state_.recordValues();
    }

    public void adjustDot()
    {
        if ( state_.showAxes() ) {
            drawDot( state_.oldParams );
            drawDot( state_.params);
            adjustLineToDot();
        }
        else {
            state_.recordValues();
        }
    }

    public void adjustLineToDot()
    {
        //drawLineToDot( state_.oldParams );
        //drawLineToDot( state_.params );
        state_.recordValues();
        repaint();
    }

    private void drawCircle1( float r1 )
    {
        offlineGraphics_.drawOval( (int) ((IMG_WIDTH >> 1) - r1), (int) ((IMG_HEIGHT >> 1) - r1), (int) (2 * r1), (int) (2 * r1) );
    }

    private void drawCircle2( Parameters params )
    {
        setCenter( params );
        int sign = params.getSign();
        float r2 = params.getR2();
        offlineGraphics_.drawOval( (int) (center_[0] - sign * r2), (int) (center_[1] - sign * r2),
                                   (int)(2 * sign * r2),  (int)(2 * sign * r2) );
    }

    private void drawDot( Parameters params )
    {
        setCenter( params );
        //System.out.println("r1="+r1+" r2="+r2 + " p="+p + " phi="+phi+" theta="+theta+ " center="+center_[0] + " "+center_[1]);
        float p = params.getPos();
        float phi = params.getPhi();
        offlineGraphics_.fillOval( (int) (center_[0] + p * Math.cos( phi )) - HALF_DOT_RAD,
                (int) (center_[1] - p * Math.sin( phi )) - HALF_DOT_RAD, DOT_RAD, DOT_RAD );
    }


    private void drawLineToDot(Parameters params)
    {
        setCenter( params );
        int side = params.getSign();
        float pos = params.getPos();
        float r2 = params.getR2();
        float phi = params.getPhi();
        if ( pos < 0 ) side = -side;

        offlineGraphics_.drawLine( (int) (center_[0] + side * r2 * Math.cos( phi )),
                (int) (center_[1] - side * r2 * Math.sin( phi )),
                (int) params.getX(), (int) params.getY() );
        setPoint(pos, phi);
    }

    public void reset() {
        // stop the thread
        clear();
        thread_ = null;
        isRendering_ = false;
        state_.reset();

        setPoint(state_.params.getPos(), 0);
        adjustCircle2();
    }
}