/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.spirograph;

import com.barrybecker4.apps.spirograph.model.GraphState;
import com.barrybecker4.common.concurrency.ThreadUtil;
import com.barrybecker4.ui.renderers.OfflineGraphics;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Renders the SpiroGraph curve.
 * May be interrupted.
 * @author Barry Becker
 */
public class GraphRenderer {

    /** offline rendering is faster */
    private OfflineGraphics offlineGraphics;
    private GraphState state;

    /** UI component to show the offline rendered image in when done. */
    private GraphPanel graphPanel;
    private boolean aborted = false;

    /**
     * Constructor
     */
    public GraphRenderer(GraphState state, GraphPanel graphPanel) {
        this.state = state;
        this.graphPanel = graphPanel;
    }

    /**
     * Draws the graph into the offline image.
     */
    public void startDrawingGraph() {
        int count = 0;
        state.initialize(graphPanel.getWidth(), graphPanel.getHeight());
        state.setRendering(true);

        float r2 = state.params.getR2();
        float p = state.params.getPos();

        // avoid degenerate (divide by 0 case) curves.
        if ( r2 == 0 ) return;

        int revs = state.getNumRevolutions();

        float n = 1.0f + state.getNumSegmentsPerRev() * (Math.abs( p / r2 ));

        while ( count++ < (int) (n * revs + 0.5) && !aborted) {
            drawSegment(count, revs, n);
        }
        state.setRendering(false);
    }

    /**
     * Renders the current offline image into the g2 object.
     * @param g2 graphics to render image into.
     */
    public void renderCurrentGraph( Graphics2D g2 ) {
        int xpos = (graphPanel.getSize().width - graphPanel.getWidth()) >> 1;
        int ypos = (graphPanel.getSize().height - graphPanel.getHeight()) >> 1;
        g2.drawImage(getOfflineGraphics().getOfflineImage().get(), xpos, ypos, graphPanel);
    }

    /**
     * Sets the center point.
     */
    public void setPoint(float pos, float phi) {
        Point2D center = state.params.getCenter(graphPanel.getWidth(), graphPanel.getHeight());
        state.params.setX((float)(center.getX() + pos * Math.cos( phi )));
        state.params.setY((float)(center.getY() - pos * Math.sin( phi )));
    }

    /**
     * Stop the rendering as quickly as possible
     */
    public void abort() {
        aborted = true;
    }

    public void clear() {
        getOfflineGraphics().clear();
    }

    /**
     * Draw a small line segment that makes up the larger spiral curve.
     * Drawn into the offline image.
     */
    private void drawSegment(int count, int revs, float n) {
        float r1;
        float r2;
        float p;
        r1 = state.params.getR1();
        r2 = state.params.getR2();
        p = state.params.getPos();
        getOfflineGraphics().setColor( state.getColor() );

        if ( count == (int) (n * revs + 0.5) )
            state.params.setTheta(0.0f);
        else
            state.params.setTheta((float)(2.0f * Math.PI * count / n));
        float theta = state.params.getTheta();
        state.params.setPhi(theta * (1.0f + r1 / r2));
        float phi = state.params.getPhi();
        setPoint(p, phi);

        graphPanel.waitIfPaused();
        Stroke stroke = new BasicStroke( (float) state.getWidth() / (float)GraphState.INITIAL_LINE_WIDTH );
        getOfflineGraphics().setStroke( stroke );
        getOfflineGraphics().drawLine((int) state.oldParams.getX(), (int) state.oldParams.getY(),
                                  (int) state.params.getX(), (int) state.params.getY() );

        if (!state.isMaxVelocity()) {
            graphPanel.repaint();
            doSmallDelay();
        }
        state.recordValues();
    }

    private void doSmallDelay() {
        ThreadUtil.sleep(state.getDelayMillis());
    }

    /**
     * @return the offline graphics instance. Creates the it if needed before returning.
     */
    private OfflineGraphics getOfflineGraphics() {
        if (offlineGraphics == null) {
            offlineGraphics = new OfflineGraphics(graphPanel.getSize(), graphPanel.getBackground());
        }
        return offlineGraphics;
    }
}