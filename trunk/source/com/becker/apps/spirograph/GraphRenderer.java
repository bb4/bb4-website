package com.becker.apps.spirograph;

import com.becker.common.math.MathUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Renders the SpiroGraph curve.
 * Adapted from David Little's original work.
 * Rendering happens in a separate thread. Not use of monitor for locking.
 * @author David Little
 * @author Barry Becker
 */
public class GraphRenderer extends JPanel implements Runnable
{
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private Thread thread_ = null;
    private OfflineGraphics offlineGraphics_;
    private float[] center_;

    // set this var instead of using Thread.stop (see
    // http://java.sun.com/products/jdk/1.2/docs/guide/misc/threadPrimitiveDeprecation.html )
    private volatile boolean paused_ = false;
    // synchronization monitor.
    private final Object pauseLock_ = new Object();
    private GraphState state_;
    private DecorationRenderer decorRenderer_;

    /**
     * Constructor
     */
    public GraphRenderer(GraphState state)
    {
        state_ = state;
        setBackground( BACKGROUND_COLOR );
        center_ = new float[2];
        state_.initialize(getWidth(), getHeight());
        //clear();
        thread_ = new Thread(this);
        decorRenderer_ = new DecorationRenderer(state_);

        /** whenever we get resized we need to recreate the offscreen image that we render into.  */
        this.addComponentListener( new ComponentAdapter()  {
            @Override
            public void componentResized( ComponentEvent ce )
            {
                offlineGraphics_ = null;
                repaint();
            }
        } );
    }

    public void pause(){
        paused_ = true;
    }

    public void resume(){
        paused_ = false;
        thread_ = new Thread(this);
        thread_.start();
    }

    public void reset(){
        paused_ = true;
        thread_ = new Thread(this);
        state_.reset();
        setPoint(state_.params.getPos(), 0);
        this.repaint();
    }

    public void start(){
        if ( paused_ ){
            paused_ = false;
        }
        thread_.start();
    }

    private OfflineGraphics getOfflineGraphics() {
        if (offlineGraphics_ == null) {
            offlineGraphics_ = new OfflineGraphics(getSize(), BACKGROUND_COLOR);
        }
        return offlineGraphics_;
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

    /**
     * starts the rendering thread.
     */
    public void run()
    {
        int count = 0;
        state_.initialize(getWidth(), getHeight());
        state_.setRendering(true);

        float r2 = state_.params.getR2();
        float p = state_.params.getPos();

        // avoid degenerate (divide by 0 case) curves.
        if ( r2 == 0 ) return;

        int revs = state_.getNumRevolutions();

        float n = 1.0f + state_.getNumSegmentsPerRev() * (Math.abs( p / r2 ));

        while ( count++ < (int) (n * revs + 0.5)) {
            drawSegment(count, revs, n);
        }
        repaint();

        state_.setRendering(false);
        thread_ = new Thread( this );
    }

    /**
     * Draw a small line segment that makes up the larger sprial curve.
     */
    private void drawSegment(int count, int revs, float n) {
        float r1;
        float r2;
        float p;
        r1 = state_.params.getR1();
        r2 = state_.params.getR2();
        p = state_.params.getPos();
        getOfflineGraphics().setColor( state_.getColor() );

        if ( count == (int) (n * revs + 0.5) )
            state_.params.setTheta(0.0f);
        else
            state_.params.setTheta((float)(2.0f * Math.PI * count / n));
        float theta = state_.params.getTheta();
        state_.params.setPhi(theta * (1.0f + r1 / r2));
        float phi = state_.params.getPhi();
        setPoint(p, phi);

        waitIfPaused();
        Stroke stroke = new BasicStroke( (float)state_.getWidth() / (float)GraphState.INITIAL_LINE_WIDTH );
        getOfflineGraphics().setStroke( stroke );
        getOfflineGraphics().drawLine((int) state_.oldParams.getX(), (int) state_.oldParams.getY(),
                                  (int) state_.params.getX(), (int) state_.params.getY() );

        if ( !state_.isMaxVelocity()) {
            repaint();
            doSmallDelay();
        }
        state_.recordValues();
    }

    private void waitIfPaused()
    {
        if ( !paused_ ) return;
        // pause if we get suspended
        try {
            synchronized (pauseLock_) {
                while ( paused_ ) pauseLock_.wait(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doSmallDelay() {
        try {
            Thread.sleep(state_.getDelayMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D)g;

        int xpos = (getSize().width - getWidth()) >> 1;
        int ypos = (getSize().height - getHeight()) >> 1;
        g.drawImage( getOfflineGraphics().getOfflineImage(), xpos, ypos, this );

        if ( state_.showDecoration()) {
            decorRenderer_.drawDecoration(g2, getWidth(), getHeight());
        }
    }

    /**
     * Sets the center point.
     */
    public void setPoint(float pos, float phi)
    {
        setCenter( state_.params );
        state_.params.setX((float)(center_[0] + pos * Math.cos( phi )));
        state_.params.setY((float)(center_[1] - pos * Math.sin( phi )));
    }

    public void setCenter(Parameters params)
    {
        float r1 = params.getR1();
        float r2 = params.getR2();
        float sign = params.getSign();
        float theta = params.getTheta();
        center_[0] = (float)((this.getWidth() >> 1) + (r1 + r2 * sign) * Math.cos( theta ));
        center_[1] = (float)((this.getHeight() >> 1) - (r1 + r2 * sign) * Math.sin( theta ));
    }

    public void clear()
    {
        getOfflineGraphics().clear();
        this.repaint();
    }
}