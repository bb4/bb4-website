/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.spirograph;

import com.barrybecker4.apps.spirograph.model.GraphState;
import com.barrybecker4.common.concurrency.ThreadUtil;

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
public class GraphPanel extends JPanel implements Runnable {

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private Thread thread;
    private GraphRenderer graphRenderer;
    private DecorationRenderer decorRenderer_;

    /** synchronization monitor.  */
    private final Object pauseLock = new Object();
    private volatile boolean paused = false;
    private volatile GraphState state;


    /**
     * Constructor
     */
    public GraphPanel(GraphState state) {
        this.state = state;
        setBackground( BACKGROUND_COLOR );
        this.state.initialize(getWidth(), getHeight());

        thread = new Thread(this);
        decorRenderer_ = new DecorationRenderer(this.state.params);
        graphRenderer = new GraphRenderer(this.state, this);
        this.addComponentListener( new ComponentAdapter()  {
            @Override
            public void componentResized( ComponentEvent ce ) {
                graphRenderer.clear();
            }
        } );
    }

    public void setPaused( boolean newPauseState ) {
        synchronized (pauseLock) {
            if ( paused != newPauseState ) {
                paused = newPauseState;
                pauseLock.notifyAll();
            }
        }
    }

    public void reset() {
        stopCurrentThread();
        thread = new Thread(this);
        state.reset();
        graphRenderer = new GraphRenderer(state, this);
        paused = true;
        this.repaint();
    }

    public synchronized void drawCompleteGraph() {
        clear();
        state.reset();
        startDrawingGraph();
        waitUntilDoneRendering();
        this.repaint();
    }

    /**
     * If we are just going to draw the graph as quickly as possible, and block until its done,
     * then don't mess with trying to draw it in a separate thread.
     */
    public void startDrawingGraph() {
        if (paused){
            paused = false;
        }

        if (state.isMaxVelocity()) {
            graphRenderer.startDrawingGraph();
        }
        else {
            thread.start();
        }
    }

    private void stopCurrentThread() {
        paused = false;
        graphRenderer.abort();
        waitUntilDoneRendering();
        thread = new Thread( this );
    }

    private void waitUntilDoneRendering() {
        while (state.isRendering()) {
            ThreadUtil.sleep(100);
        }
    }

    /**
     * starts the rendering thread.
     */
    @Override
    public void run() {
        graphRenderer.startDrawingGraph();
        thread = new Thread( this );
    }

    /**
     * Does nothing it not paused.
     * If paused, it will discontinue processing on this thread until pauseLock is released.
     */
    public void waitIfPaused()  {
        try {
            synchronized (pauseLock) {
                while (paused)
                    pauseLock.wait(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        graphRenderer.clear();
        repaint();
    }

    @Override
    public void paint( Graphics g ) {
        Graphics2D g2 = (Graphics2D)g;
        graphRenderer.renderCurrentGraph(g2);
        if ( state.showDecoration()) {
            decorRenderer_.drawDecoration(g2, getWidth(), getHeight());
        }
    }
}