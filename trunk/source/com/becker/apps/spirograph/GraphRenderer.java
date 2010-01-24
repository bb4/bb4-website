package com.becker.apps.spirograph;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Renders the SpiroGraph curve.
 * May be interrupted.
 * @author Barry Becker
 */
public class GraphRenderer
{
    private OfflineGraphics offlineGraphics_;
    private GraphState state_;
    private GraphPanel graphPanel_;
    private boolean aborted_ = false;
    
    /**
     * Constructor
     */
    public GraphRenderer(GraphState state, GraphPanel graphPanel)
    {
        state_ = state;
        graphPanel_ = graphPanel;
    }

    /**
     * @return the offline graphics instance. Creates the it if needed before returning.
     */
    private OfflineGraphics getOfflineGraphics() {
        if (offlineGraphics_ == null) {
            offlineGraphics_ = new OfflineGraphics(graphPanel_.getSize(), graphPanel_.getBackground());
        }
        return offlineGraphics_;
    }

    public void startDrawingGraph() {
        int count = 0;
        state_.initialize(graphPanel_.getWidth(), graphPanel_.getHeight());
        state_.setRendering(true);

        float r2 = state_.params.getR2();
        float p = state_.params.getPos();

        // avoid degenerate (divide by 0 case) curves.
        if ( r2 == 0 ) return;

        int revs = state_.getNumRevolutions();

        float n = 1.0f + state_.getNumSegmentsPerRev() * (Math.abs( p / r2 ));

        while ( count++ < (int) (n * revs + 0.5) && !aborted_) {
            drawSegment(count, revs, n);
        }
        graphPanel_.repaint();
        state_.setRendering(false);
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

        graphPanel_.waitIfPaused();
        Stroke stroke = new BasicStroke( (float)state_.getWidth() / (float)GraphState.INITIAL_LINE_WIDTH );
        getOfflineGraphics().setStroke( stroke );
        getOfflineGraphics().drawLine((int) state_.oldParams.getX(), (int) state_.oldParams.getY(),
                                  (int) state_.params.getX(), (int) state_.params.getY() );

        if ( !state_.isMaxVelocity()) {
            graphPanel_.repaint();
            doSmallDelay();
        }
        state_.recordValues();
    }

    private void doSmallDelay() {
        try {
            Thread.sleep(state_.getDelayMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void renderCurrentGraph( Graphics2D g2 )
    {
        int xpos = (graphPanel_.getSize().width - graphPanel_.getWidth()) >> 1;
        int ypos = (graphPanel_.getSize().height - graphPanel_.getHeight()) >> 1;
        g2.drawImage( getOfflineGraphics().getOfflineImage(), xpos, ypos, graphPanel_ );
    }

    /**
     * Sets the center point.
     */
    public void setPoint(float pos, float phi)
    {
        Point2D center = state_.params.getCenter(graphPanel_.getWidth(), graphPanel_.getHeight());
        state_.params.setX((float)(center.getX() + pos * Math.cos( phi )));
        state_.params.setY((float)(center.getY() - pos * Math.sin( phi )));
    }

    /**
     * Stop the rendering as quickly as possible
     */
    public void abort() {
        aborted_ = true;
    }

    public void clear()
    {
        getOfflineGraphics().clear();
    }
}