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
    private Thread thread_;
    private GraphRenderer graphRenderer_;
    private DecorationRenderer decorRenderer_;
    
    /** synchronization monitor.  */
    private final Object pauseLock_ = new Object();
    private volatile boolean paused_ = false;
    private volatile GraphState state_;


    /**
     * Constructor
     */
    public GraphPanel(GraphState state)
    {
        state_ = state;
        setBackground( BACKGROUND_COLOR );
        state_.initialize(getWidth(), getHeight());

        thread_ = new Thread(this);
        decorRenderer_ = new DecorationRenderer(state_.params);
        graphRenderer_ = new GraphRenderer(state_, this);
        this.addComponentListener( new ComponentAdapter()  {
            @Override
            public void componentResized( ComponentEvent ce )
            {
                graphRenderer_.clear();
            }
        } );
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

    public void reset() {
        stopCurrentThread();
        thread_ = new Thread(this);
        state_.reset();
        graphRenderer_ = new GraphRenderer(state_, this);
        paused_ = true;
        this.repaint();
    }

    public synchronized void drawCompleteGraph() {
        clear();
        state_.reset();
        startDrawingGraph();
        waitUntilDoneRendering();
        this.repaint();
    }

    /**
     * If we are just going to draw the graph as quickly as possible, and block until its done,
     * then don't mess with trying to draw it in a separate thread.
     */
    public void startDrawingGraph() {
        if ( paused_ ){
            paused_ = false;
        }

        if (state_.isMaxVelocity()) {
            graphRenderer_.startDrawingGraph();
        }
        else {
            thread_.start();
        }
    }

    private void stopCurrentThread() {
        paused_ = false;
        graphRenderer_.abort();
        waitUntilDoneRendering();
        thread_ = new Thread( this );
    }

    private void waitUntilDoneRendering() {
        while (state_.isRendering()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * starts the rendering thread.
     */
    public void run()
    {
        graphRenderer_.startDrawingGraph();
        thread_ = new Thread( this );
    }

    /**
     * Does nothing it not paused.
     * If paused, it will discontinue processing on this thread until pauseLock is released.
     */
    public void waitIfPaused()
    {
        try {
            synchronized (pauseLock_) {
                while ( paused_ )
                    pauseLock_.wait(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clear()
    {
        graphRenderer_.clear();
        repaint();
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
}