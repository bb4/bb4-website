package com.becker.apps.spirograph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Panel to contain the SpiroGraph curve and control its rendering.
 * Adapted from David Little's original work.
 * Rendering happens in a separate thread. Note use of monitor for locking.
 *
 * @author David Little
 * @author Barry Becker
 */
public class GraphPanel extends JPanel implements Runnable
{
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private Thread thread_ = null;
    private GraphRenderer graphRenderer_;
    private DecorationRenderer decorRenderer_;

    // set this var instead of using Thread.stop (see
    // http://java.sun.com/products/jdk/1.2/docs/guide/misc/threadPrimitiveDeprecation.html )
    private volatile boolean paused_ = false;
    // synchronization monitor.
    private final Object pauseLock_ = new Object();
    private GraphState state_;


    /**
     * Constructor
     */
    public GraphPanel(GraphState state)
    {
        state_ = state;
        setBackground( BACKGROUND_COLOR );
        state_.initialize(getWidth(), getHeight());

        thread_ = new Thread(this);
        decorRenderer_ = new DecorationRenderer(state_);
        graphRenderer_ = new GraphRenderer(state_, this);
        this.addComponentListener( new ComponentAdapter()  {
            @Override
            public void componentResized( ComponentEvent ce )
            {
                graphRenderer_.clear();
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

    public void reset() {
        thread_.stop();
        thread_ = new Thread(this);
        this.repaint();
        paused_ = true;
        state_.reset();

    }

    public void start(){
        if ( paused_ ){
            paused_ = false;
        }
        thread_.start();
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
        try {
            graphRenderer_ = new GraphRenderer(state_, this);
            graphRenderer_.startDrawingGraph();
        } catch (InterruptedException e) {
            System.out.println("Drawing interrupted.");
        }
        thread_ = new Thread( this );
    }

    /**
     * Does nothing it not paused.
     * If paused, it will discontinue processing on this thread until pause:ock is released.
     * @return true if interrupted while waiting.
     */
    public boolean waitIfPaused()
    {
        if ( !paused_ ) return false;

        try {
            synchronized (pauseLock_) {
                while ( paused_ ) pauseLock_.wait(100);
            }
        } catch (InterruptedException e) {
            return true;
        }
        return false;
    }

    @Override
    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D)g;
        graphRenderer_.renderCurrentGraph(g2);
        if ( state_.showDecoration()) {
            decorRenderer_.drawDecoration(g2, getWidth(), getHeight());
        }
    }

    public void clear()
    {
        graphRenderer_.clear();
        repaint();
    }
}